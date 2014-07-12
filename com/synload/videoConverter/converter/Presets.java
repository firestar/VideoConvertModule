package com.synload.videoConverter.converter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import com.synload.videoConverter.VideoConvertModule;
import com.synload.videoConverter.converter.models.Video;

public class Presets{
	public static String vp84k(Video video, String extra){
		
		return VideoConvertModule.prop.getProperty("ffmpeg")+
		" -i "+
		VideoConvertModule.prop.getProperty("uploadPath")+video.getSourceFile()+
		extra +
		" -threads 8 -r "+
		video.getFPS()+
		" -vcodec vp8 -acodec libvorbis -ac 2 -maxrate 10000k -b:v 30000k -b:a 196k -ar 44100 -s 3840x2160 "+
		VideoConvertModule.prop.getProperty("videoPath")+video.getSourceFile()+
		".webm";
	}
	public static String custom(Video video,String customName, String extra) throws IOException{
		boolean foundMatch = customName.matches("(?si)([0-9a-zA-Z.]+)");
		if(!foundMatch){
			return vp8480(video, extra);
		}
		InputStream ips = new FileInputStream(new File(VideoConvertModule.prop.getProperty("customPath")+customName));
        InputStreamReader isr = new InputStreamReader(ips);
        BufferedReader br = new BufferedReader(isr);
		String line,out = "";
		while ((line = br.readLine()) != null) {
			out+=line;
		}
		br.close();
		isr.close();
		return out.replace(
			"%FFMPEG%", 
			VideoConvertModule.prop.getProperty("ffmpeg")
		).replace(
			"%FPS%", 
			video.getFPS()
		).replace(
			"%EXTRA%", 
			extra
		).replace(
			"%TEMP%", 
			VideoConvertModule.prop.getProperty("uploadPath")+video.getSourceFile()
		).replace(
			"%FINAL%", 
			VideoConvertModule.prop.getProperty("videoPath")+video.getSourceFile()
		);
	}
	public static String vp8480(Video video, String extra){
		return VideoConvertModule.prop.getProperty("ffmpeg")+
		" -i "+
		VideoConvertModule.prop.getProperty("uploadPath")+video.getSourceFile()+
		extra +
		" -threads 8 -r "+
		video.getFPS()+
		" -vcodec vp8 -acodec libvorbis -ac 2 -maxrate 1500k -b:v 2500k -b:a 196k -ar 44100 -s 852x480 "+
		VideoConvertModule.prop.getProperty("videoPath")+video.getSourceFile()+
		".webm";
	}
	public static String vp8(Video video, String extra){
		return VideoConvertModule.prop.getProperty("ffmpeg")+
		" -i "+
		VideoConvertModule.prop.getProperty("uploadPath")+video.getSourceFile()+
		extra +
		" -threads 8 -r "+
		video.getFPS()+
		" -vcodec vp8 -acodec libvorbis -ac 2 -maxrate 3000k -b:v 5000k -b:a 196k -sn -ar 44100 -s 1600x900 "+
		VideoConvertModule.prop.getProperty("videoPath")+video.getSourceFile()+
		".webm";
	}
}