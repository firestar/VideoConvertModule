package com.synload.converter.presets;

import com.synload.converter.Converter;
import com.synload.converter.models.ConvertFile;
import com.synload.converter.models.Preset;

import net.bramp.ffmpeg.probe.FFmpegProbeResult;

public class VP8480 extends Preset {
	public VP8480(FFmpegProbeResult video, String source, String output, String extra){
		super(Converter.getProp().getProperty("ffmpeg")+
		" -i "+
		source+
		extra +
		" -threads 8 -r "+
		(getVideoStream(video).avg_frame_rate.getNumerator()/getVideoStream(video).avg_frame_rate.getDenominator())+
		" -vcodec vp8 -acodec libvorbis -ac 2 -maxrate 1500k -b:v 2500k -b:a 196k -ar 44100 -s 852x480 "+
		output+
		"_vp8_480.webm");
	}
}
