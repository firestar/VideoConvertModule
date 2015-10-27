package com.synload.converter.presets;

import com.synload.converter.Converter;
import com.synload.converter.models.ConvertFile;
import com.synload.converter.models.Preset;

import net.bramp.ffmpeg.probe.FFmpegProbeResult;

public class VP8 extends Preset {
	public VP8(FFmpegProbeResult video, String source, String output, String extra){
		super( Converter.getProp().getProperty("ffmpeg")+
		" -i "+source+
		extra +
		" -threads 8 -r "+
		(getVideoStream(video).avg_frame_rate.getNumerator()/getVideoStream(video).avg_frame_rate.getDenominator())+
		" -vcodec vp8 -acodec libvorbis -ac 2 -maxrate 3000k -b:v 5000k -b:a 196k -sn -ar 44100 -s 1920x1080 "+
		output+".webm");
	}
}
