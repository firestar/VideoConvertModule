package com.synload.videoConverter.converter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import com.synload.framework.SynloadFramework;
import com.synload.videoConverter.VideoConvertModule;
import com.synload.videoConverter.converter.models.Video;

public class ConverterProcessing{
	public static String getFPS(Video video){
		String ResultString="1";
		Pattern regex = Pattern.compile("([0-9.]+) fps");
		Matcher regexMatcher = regex.matcher(video.getData());
		if (regexMatcher.find()) {
			ResultString = regexMatcher.group(1);
		}
		if(ResultString.equals("1")){
			regex = Pattern.compile("-> ([0-9.]+)");
			regexMatcher = regex.matcher(video.getData());
			if (regexMatcher.find()) {
				ResultString = regexMatcher.group(1);
			}
		}
		if(ResultString.equals("1")){
			regex = Pattern.compile("([0-9.]+) tbr");
			regexMatcher = regex.matcher(video.getData());
			if (regexMatcher.find()) {
				ResultString = regexMatcher.group(1);
			}
		}
		return ResultString;
	}
	public static String getFPS(String line){
		String ResultString="1";
		Pattern regex = Pattern.compile("fps=(|[ ]+)([0-9.]+)");
		Matcher regexMatcher = regex.matcher(line);
		if (regexMatcher.find()) {
			ResultString = regexMatcher.group(2);
		}
		if(ResultString.equals("1")){
			regex = Pattern.compile("([0-9.]+) fps");
			regexMatcher = regex.matcher(line);
			if (regexMatcher.find()) {
				ResultString = regexMatcher.group(1);
			}
		}
		if(ResultString.equals("1")){
			regex = Pattern.compile("-> ([0-9.]+)");
			regexMatcher = regex.matcher(line);
			if (regexMatcher.find()) {
				ResultString = regexMatcher.group(1);
			}
		}
		if(ResultString.equals("1")){
			regex = Pattern.compile("([0-9.]+) tbr");
			regexMatcher = regex.matcher(line);
			if (regexMatcher.find()) {
				ResultString = regexMatcher.group(1);
			}
		}
		return ResultString;
	}
	public static float getDuration(Video video){
		float totalPlay = 0;
		try {
			Pattern regex = Pattern.compile("Duration: ([0-9]{1,2}):([0-9]{1,2}):([0-9]{1,2}).([0-9]{1,2})");
			Matcher regexMatcher = regex.matcher(video.getData());
			while (regexMatcher.find()) {
				totalPlay = ((Integer.valueOf(regexMatcher.group(1))*60)*60)+(Integer.valueOf(regexMatcher.group(2))*60)+(Integer.valueOf(regexMatcher.group(3)));
			}
		} catch (PatternSyntaxException e) {
			if(SynloadFramework.debug){
				e.printStackTrace();
			}
		}
		return totalPlay;
	}
	public static String getOutput(String line){
		try {
			Pattern regex = Pattern.compile("Output #0, ([a-zA-Z0-9.]+), to '(.*?)':");
			Matcher regexMatcher = regex.matcher(line);
			while (regexMatcher.find()) {
				return regexMatcher.group(2);
			}
		} catch (PatternSyntaxException e) {
			if(SynloadFramework.debug){
				e.printStackTrace();
			}
		}
		return "";
	}
	public static float getPosition(String line){
		float totalPlay = 0;
		try {
			Pattern regex = Pattern.compile("time=(|[ ]+)([0-9]{1,2}):([0-9]{1,2}):([0-9]{1,2}).([0-9]{1,2})");
			Matcher regexMatcher = regex.matcher(line);
			while (regexMatcher.find()) {
				totalPlay = ((Integer.valueOf(regexMatcher.group(2))*60)*60)+(Integer.valueOf(regexMatcher.group(3))*60)+(Integer.valueOf(regexMatcher.group(4)));
			}
		} catch (PatternSyntaxException e) {
			if(SynloadFramework.debug){
				e.printStackTrace();
			}
		}
		return totalPlay;
	}
	public static String getCurrentBitrate(String line){
		try {
			Pattern regex = Pattern.compile("bitrate=(|[ ]+)([0-9a-zA-Z/.]+)");
			Matcher regexMatcher = regex.matcher(line);
			while (regexMatcher.find()) {
				return regexMatcher.group(2);
			}
		} catch (PatternSyntaxException e) {
			if(SynloadFramework.debug){
				e.printStackTrace();
			}
		}
		return null;
	}
	public static String getFrames(String line){
		try {
			Pattern regex = Pattern.compile("frame=(|[ ]+)([0-9]+)");
			Matcher regexMatcher = regex.matcher(line);
			while (regexMatcher.find()) {
				return regexMatcher.group(2);
			}
		} catch (PatternSyntaxException e) {
			if(SynloadFramework.debug){
				e.printStackTrace();
			}
		}
		return "0";
	}
	public static void extractSubs(Video video){
		List<String> tracks = new ArrayList<String>();
		try {
			Pattern regex = Pattern.compile("Stream #([0-9]+)\\.([0-9]+)(?:\\([a-z]+\\)|): Subtitle: (.*?)\r\n    Metadata:\r\n      title([ ]+): (.*?)\r");
			Matcher regexMatcher = regex.matcher(video.getData());
			while (regexMatcher.find()) {
				try {
					HashMap<String, String> commands = new HashMap<String,String>();
					System.out.println("Found Subtitles");
					String filename = video.randomString();
					String cmd = VideoConvertModule.prop.getProperty("mkvextract")+" tracks "+VideoConvertModule.prop.getProperty("uploadPath")+video.getSourceFile()+" "+regexMatcher.group(2)+":"+VideoConvertModule.prop.getProperty("uploadPath")+filename+".ass";
					ProcessBuilder builder = new ProcessBuilder(cmd.split(" "));
					builder.redirectErrorStream(true);
					Process pr = builder.start();
					InputStream is = pr.getInputStream();
			        InputStreamReader isr = new InputStreamReader(is);
			        BufferedReader br = new BufferedReader(isr);
			        String lines = "", line = "";
					while ((line = br.readLine()) != null) {
						if(SynloadFramework.debug){
							System.out.println(line);
						}
						lines += line;
					}
					commands.put("exportSubtitle", lines);
					br.close();
					isr.close();
					is.close();
					cmd = VideoConvertModule.prop.getProperty("ffmpeg")+" -i "+VideoConvertModule.prop.getProperty("uploadPath")+filename+".ass "+VideoConvertModule.prop.getProperty("uploadPath")+filename+".srt";
					builder = new ProcessBuilder(cmd.split(" "));
					builder.redirectErrorStream(true);
					pr = builder.start();
					is = pr.getInputStream();
			        isr = new InputStreamReader(is);
			        br = new BufferedReader(isr);
			        lines = "";
			        line = "";
					while ((line = br.readLine()) != null) {
						if(SynloadFramework.debug){
							System.out.println(line);
						}
						lines += line;
					}
					System.out.println("Subtitle extracted");
					commands.put("convertSubtitle", lines);
					br.close();
					isr.close();
					is.close();
					System.out.println("Converting subtitle");
					OutputStream outfile = new FileOutputStream(new File(VideoConvertModule.prop.getProperty("uploadPath")+filename+".vtt"));
					SRT2VTT.convert(VideoConvertModule.prop.getProperty("uploadPath")+filename+".srt", outfile);
					System.out.println("conversion complete");
					if((new File(VideoConvertModule.prop.getProperty("uploadPath")+filename+".srt")).exists()){
						
					}
					System.out.println("setting subtitles");
					video.addSubtitle( filename, regexMatcher.group(6), regexMatcher.group(3), commands);
					System.out.println("All done");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} catch (PatternSyntaxException ex) {
			// Syntax error in the regular expression
		}
	}
	/*public static void getH264(Video video, String mDataLine){
		if(!video.isH264()){
			Pattern regex = Pattern.compile("h264");
			Matcher regexMatcher = regex.matcher(mDataLine);
			if (regexMatcher.find()) {
				video.setH264(true);
			} else {
			}
			regex = Pattern.compile("x264");
			regexMatcher = regex.matcher(mDataLine);
			if (regexMatcher.find()) {
				video.setH264(true);
			} else {
			}
		}
	}*/
	public static void removeSubs(Video video) throws IOException{
		String cmd = VideoConvertModule.prop.getProperty("ffmpeg")+" -i "+VideoConvertModule.prop.getProperty("uploadPath")+video.getSourceFile()+" -sn -vcodec copy -acodec copy "+VideoConvertModule.prop.getProperty("uploadPath")+video.getSourceFile()+".mkv";
		ProcessBuilder builder = new ProcessBuilder(cmd.split(" "));
		builder.redirectErrorStream(true);
		Process pr = builder.start();
		InputStream is = pr.getInputStream();
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        String lines="",line="";
		while ((lines = br.readLine()) != null) {
			if(SynloadFramework.debug){
				System.out.println(line);
			}
			lines += line;
		}
		video.commands.put("subtitleRemoval", lines);
		br.close();
		isr.close();
		is.close();
		(new File(VideoConvertModule.prop.getProperty("uploadPath")+video.getSourceFile())).delete();
		(new File(VideoConvertModule.prop.getProperty("uploadPath")+video.getSourceFile()+".mkv")).renameTo(new File(VideoConvertModule.prop.getProperty("uploadPath")+video.getSourceFile()));
	}
	public static String cmdExec(String cmdLine) {
	    String output = "";
	    try {
	        Process p = Runtime.getRuntime().exec(cmdLine);
	        InputStream is = p.getErrorStream();
	        InputStreamReader isr = new InputStreamReader(is);
	        BufferedReader br = new BufferedReader(isr);
	        String line;
	        while ((line = br.readLine()) != null) {
	        	output+=line;
	        }
	    }catch (Exception ex) {
	    	if(SynloadFramework.debug){
	    		ex.printStackTrace();
	    	}
	    }
	    return output;
	}
}