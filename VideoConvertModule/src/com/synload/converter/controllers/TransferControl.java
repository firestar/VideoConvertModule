package com.synload.converter.controllers;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import com.synload.converter.Converter;
import com.synload.converter.models.FileData;
import com.synload.framework.Log;
import com.synload.framework.SynloadFramework;
import com.synload.framework.modules.ModuleRegistry;
import com.synload.framework.modules.annotations.Event;
import com.synload.framework.modules.annotations.Event.Type;
import com.synload.talksystem.Client;
import com.synload.talksystem.ServerTalk;
import com.synload.talksystem.commands.CommandDocument;
import com.synload.talksystem.filetransfer.FileReceiveEvent;
import com.synload.talksystem.info.InformationDocument;

import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;
import net.bramp.ffmpeg.probe.FFmpegStream;

public class TransferControl {
	public static boolean wait = false;
	@Event(name="receive File",description="Receive File From The File Bridge",type=Type.OTHER)
	public void receiveFile(FileReceiveEvent e){
		
		Log.info("Received file "+e.getFileName(), TransferControl.class);
		Log.info("File Size: "+(new File(e.getTempFilePath()).length()),TransferControl.class);
		try {
			Log.info(SynloadFramework.getPlugins().toString(), TransferControl.class);
			Converter cServer = (Converter) ModuleRegistry.get("Converter");
			if(cServer!=null){
				String storedFile = e.getChain().toString()+".vid";
				new File(cServer.getVideoPath()+storedFile).delete();
				
				FileUtils.moveFile(new File(e.getTempFilePath()), new File(cServer.getVideoPath()+storedFile));
				FFprobe ffprobe = new FFprobe("ffprobe");
				FFmpegProbeResult result = ffprobe.probe(cServer.getVideoPath()+storedFile);
				int index = 0;
				for(FFmpegStream s: result.getStreams()){
					if(s.codec_name.equals("ass") || s.codec_name.equals("srt")){
						if(s.tags.containsKey("language")){
							if(s.tags.get("language").toLowerCase().contains("eng")){
								index = s.index;
								break;
							}else{
								System.out.println(s.tags.get("language"));
								try {
									Thread.sleep(10000);
								} catch (InterruptedException e1) {
									e1.printStackTrace();
								}
							}
						}else{
							index = s.index;
							break;
						}
					}
				}
				
				if(index!=0){
					while(wait){
						try {
							Thread.sleep(10000);
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}
					}
					wait=true;
					String cmd = "ffmpeg -i "+cServer.getVideoPath()+storedFile+" -an -vn -c:s:"+index+" ass "+cServer.getVideoPath()+storedFile+".ass";
					ProcessBuilder builder = new ProcessBuilder(cmd.split(" "));
					if(builder!=null){
						builder.redirectErrorStream(true);
						Process pr = builder.start();
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
					/*String possibleSubtitleName = cServer.getVideoPath()+storedFile+".ass";
					String ext = FilenameUtils.getExtension(e.getFileName());
					String tmpFile = "tmp/"+cServer.getVideoPath()+storedFile+"."+ext;
					if((new File(possibleSubtitleName)).exists()){
						cmd = "ffmpeg -i "+cServer.getVideoPath()+storedFile+" -vf ass="+possibleSubtitleName+" "+tmpFile;
						Log.debug(cmd, this.getClass());
						builder = new ProcessBuilder(cmd.split(" "));
						if(builder!=null){
							builder.redirectErrorStream(true);
							Process pr = builder.start();
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
						FileUtils.forceDelete(new File(cServer.getVideoPath()+storedFile));
						FileUtils.moveFile(new File(tmpFile), new File(cServer.getVideoPath()+storedFile));
						
					}*/
					
					wait=false;
				}
				FileData file = new FileData(e.getChain(), cServer.getVideoPath()+storedFile);
				cServer.storeFile(e.getChain(), file);
				String possibleSubtitleName = file.getFileName()+".ass";
				if((new File(possibleSubtitleName)).exists()){
					InformationDocument iD = new InformationDocument("c_subtitle", file.getId());
					iD.getObjects().put("size", (new File(possibleSubtitleName)).length());
					iD.getObjects().put("parent", file.getId());
					for(Client client: ServerTalk.getConnected()){
						client.write(iD); // broadcast finished video
					}
				}
				e.getClient().write(new InformationDocument("c_recieved", file.getId()));
				
			}
			//ConvertFile cF = new ConvertFile(e.getFileName(), e.getFileName()+".out", "h265");
			//ConverterThread.queue.add(cF);
		} catch (IOException e1) {
			e1.printStackTrace();
			wait=false;
		}
	}
}