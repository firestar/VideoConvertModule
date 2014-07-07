package com.synload.videoConverter.converter;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import com.synload.framework.SynloadFramework;
import com.synload.videoConverter.VideoConvertModule;
import com.synload.videoConverter.converter.models.Task;
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
			if(Task.lengthNew()>0){
				Task tk = Task.getLatest();
				Video item = tk.getVideo();
				String outputFile = "";
				try {
					if(tk.getSid()>0){
						System.out.println("Subtitles found, using subs encoder!");
						String extraData = " -vf ass="+VideoConvertModule.prop.getProperty("uploadPath")+tk.getSubtitle().getFileName()+".ass";
						ProcessBuilder builder;
						if(tk.getPreset().equalsIgnoreCase("4k")){
							builder = new ProcessBuilder(Presets.vp84k(item,extraData).split(" "));
						}else if(tk.getPreset().equalsIgnoreCase("480")){
							builder = new ProcessBuilder(Presets.vp8480(item,extraData).split(" "));
						}else if(!tk.getPreset().equals("")){
							builder = new ProcessBuilder(Presets.custom(item,tk.getPreset(),extraData).split(" "));
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
								Video v = new Video(outputFile,tk.getUser());
								tk.setOvid(v.getVid());
								tk.setStatus(1);
							}
						}
						tk.addCommand("convertString", lines);
						br.close();
						isr.close();
						is.close();
					}else{
						System.out.println("No subtitles found, encoding video/audio only!");
						ProcessBuilder builder;
						if(tk.getPreset().equalsIgnoreCase("4k")){
							builder = new ProcessBuilder(Presets.vp84k(item,"").split(" "));
						}else if(tk.getPreset().equalsIgnoreCase("480")){
								builder = new ProcessBuilder(Presets.vp8480(item,"").split(" "));
						}else if(!tk.getPreset().equals("")){
							builder = new ProcessBuilder(Presets.custom(item,tk.getPreset(),"").split(" "));
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
								Video v = new Video(outputFile,tk.getUser());
								tk.setOvid(v.getVid());
								tk.setStatus(1);
							}
						}
						tk.addCommand("convertString", lines);
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
				System.out.println("Video Finished!");
			}
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				if(SynloadFramework.debug){
					e.printStackTrace();
				}
			}
		}
	}
}