package com.synload.videoConverter.converter;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import com.synload.framework.SynloadFramework;
import com.synload.videoConverter.SynloadConverter;
import com.synload.videoConverter.VideoConvertModule;
import com.synload.videoConverter.converter.models.Video;

public class ConverterThread implements Runnable{
	@SuppressWarnings("unused")
	private Converter cMain = null;
	public Process pr = null;
	
	public ConverterThread(Converter converter){
		cMain = converter;
	}
	@Override
	public void run() {
		while(true){
			if(Converter.queue.size()>0){
				Video item = Converter.queue.get(0);
				Converter.queue.remove(0);
				if(item!=null && item.getParams().size()>0){
					Converter.storeQueue();
					System.out.println("Item in queue!");
					item.prepVideo();
					System.out.println("Video prepared, sending to converter!");
					ArrayList<String> subs = item.getSubtitles();
					String quality = "std";
					if(item.getParams().containsKey("size")){
						quality = item.getParams().get("size");
					}
					String outputFile = "";
					try {
						if(subs.size()>0){
							String extraData = "";
							System.out.println("Subtitles found, using subs encoder!");
							if(item.getParams().containsKey("subType")){
								if(item.getParams().get("subType").equalsIgnoreCase("hard")){
									extraData = " -vf ass="+subs.get(0)+".ass";
								}
							}
							ProcessBuilder builder;
							if(quality.equalsIgnoreCase("4k")){
								builder = new ProcessBuilder(Presets.vp84k(item,extraData).split(" "));
							}else if(quality.equalsIgnoreCase("480")){
								builder = new ProcessBuilder(Presets.vp8480(item,extraData).split(" "));
							}else if(quality.equalsIgnoreCase("custom")){
								if(item.getParams().containsKey("quality")){
									builder = new ProcessBuilder(Presets.custom(item,item.getParams().get("quality"),extraData).split(" "));
								}else{
									builder = new ProcessBuilder(Presets.vp8(item,extraData).split(" "));
								}
							}else{
								builder = new ProcessBuilder(Presets.vp8(item,extraData).split(" "));
							}
							builder.redirectErrorStream(true);
							pr = builder.start();
							InputStream is = pr.getInputStream();
					        InputStreamReader isr = new InputStreamReader(is);
					        BufferedReader br = new BufferedReader(isr);
					        
							String line, lines = "";
							cMain.current.put("video", item);
							while ((line = br.readLine()) != null) {
								lines += line;
								if(SynloadFramework.debug){
									System.out.println(line);
								}
								//item.addConvertString(line);
								cMain.current.put("bitrate", ConverterProcessing.getCurrentBitrate(line));
								cMain.current.put("fps", ConverterProcessing.getFPS(line));
								cMain.current.put("frames", ConverterProcessing.getFrames(line));
								cMain.current.put("frame", ConverterProcessing.getPosition(line));
								cMain.current.put("percent", (((float)cMain.current.get("frame"))/item.getDuration()*100));
								float timeLeft = (
									(
										(
											Float.valueOf(item.getFPS())
											*
											item.getDuration()
										)
										-
										Float.valueOf((String)cMain.current.get("frames"))
									)
									/
									Float.valueOf((String)cMain.current.get("fps"))
								);
								cMain.current.put("timeLeft", timeLeft);
								if(outputFile.equals("")){
									outputFile = ConverterProcessing.getOutput(line);
									item.setVideoFile(outputFile);
									cMain.current.put("vid",item.getParams().get("vid"));
									cMain.current.put("video", item);
								}
							}
							item.commands.put("convertString", lines);
							br.close();
							isr.close();
							is.close();
							/*if(hardSubs){
								int retries = 100;
								while(!(new File(outputFile)).exists() && retries>0){
									retries--;
									try {
										Thread.sleep(100);
									} catch (InterruptedException e) {
										e.printStackTrace();
									}
								}
								System.out.println("Converting with hardsubs!");
								String tempName = SynloadConverter.path+item.randomString()+"."+outputFile.substring(outputFile.lastIndexOf('.')+1);
								Runtime rt = Runtime.getRuntime();
								pr = rt.exec(SynloadConverter.ffmpeg+" -i "+outputFile+" -vf subtitles="+subs.get(0)+".ass "+tempName);
								try {
									pr.waitFor();
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
								is = pr.getInputStream();
						        isr = new InputStreamReader(is);
						        br = new BufferedReader(isr);
								while ((line = br.readLine()) != null) {
									SynloadConverter.current.put("Bitrate", ConverterProcessing.getCurrentBitrate(line));
									SynloadConverter.current.put("FPS", ConverterProcessing.getFPS(line));
									SynloadConverter.current.put("Frames", ConverterProcessing.getFrames(line));
									SynloadConverter.current.put("Position", ConverterProcessing.getPosition(line));
									SynloadConverter.current.put("Action", "Hardcoding Subtitles");
								}
								br.close();
								isr.close();
								is.close();
								
								(new File(outputFile)).delete();
								(new File(tempName)).renameTo(new File(outputFile));
							}*/
						}else{
							System.out.println("No subtitles found, encoding video/audio only!");
							ProcessBuilder builder;
							if(quality.equalsIgnoreCase("4k")){
								builder = new ProcessBuilder(Presets.vp84k(item,"").split(" "));
							}else if(quality.equalsIgnoreCase("480")){
									builder = new ProcessBuilder(Presets.vp8480(item,"").split(" "));
							}else if(quality.equalsIgnoreCase("custom")){
								if(item.getParams().containsKey("quality")){
									builder = new ProcessBuilder(Presets.custom(item,item.getParams().get("quality"),"").split(" "));
								}else{
									builder = new ProcessBuilder(Presets.vp8(item,"").split(" "));
								}
							}else{
								builder = new ProcessBuilder(Presets.vp8(item,"").split(" "));
							}
							builder.redirectErrorStream(true);
							Process p = builder.start();
							InputStream is = p.getInputStream();
					        InputStreamReader isr = new InputStreamReader(is);
					        BufferedReader br = new BufferedReader(isr);
							String line, lines = "";
							cMain.current.put("video", item);
							while ((line = br.readLine()) != null) {
								lines += line;
								if(SynloadFramework.debug){
									System.out.print(line);
								}
								//item.addConvertString(line);
								cMain.current.put("bitrate", ConverterProcessing.getCurrentBitrate(line));
								cMain.current.put("fps", ConverterProcessing.getFPS(line));
								cMain.current.put("frames", ConverterProcessing.getFrames(line));
								cMain.current.put("frame", ConverterProcessing.getPosition(line));
								cMain.current.put("percent", (((float)cMain.current.get("frame"))/item.getDuration()*100));
								float timeLeft = (
									(
										(
											Float.valueOf(item.getFPS())
											*
											item.getDuration()
										)
										-
										Float.valueOf((String)cMain.current.get("frames"))
									)
									/
									Float.valueOf((String)cMain.current.get("fps"))
								);
								cMain.current.put("timeLeft", timeLeft);
								if(outputFile.equals("")){
									outputFile = ConverterProcessing.getOutput(line);
									item.setVideoFile(outputFile);
									cMain.current.put("vid",item.getParams().get("vid"));
									cMain.current.put("video", item);
								}
							}
							item.commands.put("convertString", lines);
							br.close();
							isr.close();
							is.close();
						}
					} catch (IOException e) {
						System.out.println("Error!");
						if(SynloadFramework.debug){
							e.printStackTrace();
						}
					}
					cMain.current = new HashMap<String,Object>();
					(new File(VideoConvertModule.prop.getProperty("uploadPath")+item.getTemp())).delete();
					if(item.getUploadURL()!=null){
						SynloadConverter.addUploadQueue(item);
					}
					SynloadConverter.addHistory(item);
					System.out.println("Video Finished!");
				}
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				if(SynloadFramework.debug){
					e.printStackTrace();
				}
			}
		}
	}
}