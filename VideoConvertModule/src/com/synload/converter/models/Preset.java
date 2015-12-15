package com.synload.converter.models;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.synload.converter.Converter;
import com.synload.converter.presets.H264;
import com.synload.converter.presets.H264480;
import com.synload.converter.presets.H265;
import com.synload.converter.presets.H265480;
import com.synload.converter.presets.H2654K;
import com.synload.converter.presets.VP8;
import com.synload.converter.presets.VP8480;
import com.synload.converter.presets.VP84K;
import com.synload.converter.presets.VP9;
import com.synload.converter.presets.VP9480;
import com.synload.converter.presets.VP94K;

import net.bramp.ffmpeg.probe.FFmpegProbeResult;
import net.bramp.ffmpeg.probe.FFmpegStream;
import net.bramp.ffmpeg.probe.FFmpegStream.CodecType;

public class Preset{
	private String cmd = "";
	private String outputFile = "";
	private String name;
	public Preset(String cmd, String output, String ext){
		this.cmd = cmd;
		this.outputFile = output+ext;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCmd() {
		return cmd;
	}
	public void setCmd(String cmd) {
		this.cmd = cmd;
	}
	public static boolean videoFormats(String s){
		if(s.contains("h264") || s.contains("h265") || s.contains("vp8") || s.contains("vp9")){
			return true;
		}
		return false;
	}
	public static FFmpegStream getVideoStream(FFmpegProbeResult video){
		for (FFmpegStream s : video.getStreams()){
			if(videoFormats(s.codec_name.toLowerCase())){
				return s;
			}
		}
		return null;
	}
	public static String commandify(FFmpegProbeResult video, String source, String output, String extra, String out){
		return out.replaceAll(
			"%FFMPEG%", 
			Converter.getProp().getProperty("ffmpeg")
		).replaceAll(
			"%FPS%", 
			String.valueOf(getVideoStream(video).avg_frame_rate.getNumerator()/getVideoStream(video).avg_frame_rate.getDenominator())
		).replaceAll(
			"%EXTRA%", 
			extra
		).replaceAll(
			"%TEMP%", 
			source
		).replaceAll(
			"%FINAL%", 
			output
		);
	}	
	public static String custom(FFmpegProbeResult video, String source, String output, String extra, String format) throws IOException{
		boolean foundMatch = format.matches("(?si)([0-9a-zA-Z.]+)");
		if(!foundMatch){
			return (new VP8480(video, source, output, extra)).getCmd();
		}
		switch(format){
			case "vp8_480":
				return (new VP8480(video, source, output, extra)).getCmd();
			case "vp8_1080":
				return (new VP8(video, source, output, extra)).getCmd();
			case "vp8_4k":
				return (new VP84K(video, source, output, extra)).getCmd();
			case "vp9_480":
				return (new VP9480(video, source, output, extra)).getCmd();
			case "vp9_1080":
				return (new VP9(video, source, output, extra)).getCmd();
			case "vp9_4k":
				return (new VP94K(video, source, output, extra)).getCmd();
			case "h264_480":
				return (new H264480(video, source, output, extra)).getCmd();
			case "h264_1080":
				return (new H264(video, source, output, extra)).getCmd();
			case "h265_1080":
				return (new H265(video, source, output, extra)).getCmd();
			case "h265_480":
				return (new H265480(video, source, output, extra)).getCmd();
			case "h265_4k":
				return (new H2654K(video, source, output, extra)).getCmd();
		}
		InputStream ips = new FileInputStream(new File(Converter.getProp().getProperty("customPath")+format));
        InputStreamReader isr = new InputStreamReader(ips);
        BufferedReader br = new BufferedReader(isr);
		String line,out = "";
		while ((line = br.readLine()) != null) {
			out+=line;
		}
		br.close();
		isr.close();
		return commandify(video, source, output, extra, out);
	}
	public String getOutputFile() {
		return outputFile;
	}
	public void setOutputFile(String outputFile) {
		this.outputFile = outputFile;
	}
	
}