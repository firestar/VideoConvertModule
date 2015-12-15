package com.synload.converter.thread;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import com.synload.converter.Converter;
import com.synload.converter.models.ConvertFile;
import com.synload.converter.models.FileData;
import com.synload.converter.models.Preset;
import com.synload.converter.presets.Presets;
import com.synload.framework.Log;
import com.synload.framework.SynloadFramework;
import com.synload.framework.modules.ModuleRegistry;
import com.synload.talksystem.Client;
import com.synload.talksystem.ConnectionType;
import com.synload.talksystem.ServerTalk;
import com.synload.talksystem.info.InformationDocument;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;

public class ConverterThread implements Runnable {
	public static List<ConvertFile> queue = new ArrayList<ConvertFile>();
	public Process pr = null;
	
	@Override
	public void run() {
		Converter cServer = (Converter) ModuleRegistry.get("Converter");
		while(true){
			if(queue.size()>0){
				ConvertFile cF = queue.get(0);
				try {
					FFmpeg ffmpeg = new FFmpeg("ffmpeg");
					FFprobe ffprobe = new FFprobe("ffprobe");
					
					FileData cFParent = cServer.getFileStorage().get(cF.getParentUUID());
					
					
					FFmpegProbeResult result = ffprobe.probe(cFParent.getFileName());
					try {
						Class<?> c = (new Presets(cF.getPreset())).get();
						Constructor<?> p = c.getDeclaredConstructor(FFmpegProbeResult.class, String.class, String.class, String.class);
						Preset preset = (Preset) p.newInstance(new Object[]{
							result,
							cFParent.getFileName(),
							cF.getSource(),
							""
						});
						ProcessBuilder builder = new ProcessBuilder(preset.getCmd().split(" "));
						if(builder!=null){
							builder.redirectErrorStream(true);
							pr = builder.start();
							InputStream is = pr.getInputStream();
					        InputStreamReader isr = new InputStreamReader(is);
					        BufferedReader br = new BufferedReader(isr);
					        
							String line = "";
							while ((line = br.readLine()) != null) {
								if(SynloadFramework.debug){
									System.out.println(line);
								}
							}
							br.close();
							isr.close();
							is.close();
						}
						
						FileData fdN = new FileData(cF.getVideoUUID(), preset.getOutputFile());
						cServer.storeFile(cF.getVideoUUID(), fdN);
						InformationDocument iD = new InformationDocument("c_complete",cF.getVideoUUID());
						iD.getObjects().put("size", (new File(preset.getOutputFile())).length());
						iD.getObjects().put("runtime", result.getFormat().duration);
						iD.getObjects().put("type", preset.getName());
						iD.getObjects().put("name", preset.getOutputFile());
						iD.getObjects().put("parent", cFParent.getId());
						for(Client client: ServerTalk.getConnected()){
							client.write(iD); // broadcast finished video
						}
						
						
						
						queue.remove(0);
					} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
						e.printStackTrace();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				Thread.sleep(1L);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
