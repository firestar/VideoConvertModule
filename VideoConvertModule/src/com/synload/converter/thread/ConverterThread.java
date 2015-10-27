package com.synload.converter.thread;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import com.synload.converter.models.ConvertFile;
import com.synload.converter.models.Preset;
import com.synload.converter.presets.Presets;
import com.synload.framework.Log;
import com.synload.framework.SynloadFramework;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;

public class ConverterThread implements Runnable {
	public static List<ConvertFile> queue = new ArrayList<ConvertFile>();
	public Process pr = null;
	
	@Override
	public void run() {
		while(true){
			if(queue.size()>0){
				ConvertFile cF = queue.get(0);
				try {
					FFmpeg ffmpeg = new FFmpeg("ffmpeg");
					FFprobe ffprobe = new FFprobe("ffprobe");
					FFmpegProbeResult result = ffprobe.probe(cF.getSource());
					
					try {
						Class<?> c = (new Presets(cF.getPreset())).get();  
						Log.info(c.getName(), this.getClass());
						Constructor<?> p = c.getDeclaredConstructor(FFmpegProbeResult.class, String.class, String.class, String.class);
						Preset preset = (Preset) p.newInstance(new Object[]{
							result,
							"./"+cF.getSource(),
							"./"+cF.getOutput(),
							""
						});
						ProcessBuilder builder = new ProcessBuilder(preset.getCmd().split(" "));
						if(builder!=null){
							builder.redirectErrorStream(true);
							pr = builder.start();
							InputStream is = pr.getInputStream();
					        InputStreamReader isr = new InputStreamReader(is);
					        BufferedReader br = new BufferedReader(isr);
					        
							String line, lines = "";
							while ((line = br.readLine()) != null) {
								lines += line;
								if(SynloadFramework.debug){
									System.out.println(line);
								}
							}
							br.close();
							isr.close();
							is.close();
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
