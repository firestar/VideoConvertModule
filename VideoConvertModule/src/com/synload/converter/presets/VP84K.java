package com.synload.converter.presets;
import com.synload.converter.Converter;
import com.synload.converter.models.ConvertFile;
import com.synload.converter.models.Preset;

import net.bramp.ffmpeg.probe.FFmpegProbeResult;

public class VP84K extends Preset {
	public VP84K(FFmpegProbeResult video, String source, String output, String extra){
		super(Converter.getProp().getProperty("ffmpeg")+
		" -i "+
		source+
		extra +
		" -threads 8 -r "+
		(getVideoStream(video).avg_frame_rate.getNumerator()/getVideoStream(video).avg_frame_rate.getDenominator())+
		" -vcodec vp8 -acodec libvorbis -ac 2 -maxrate 23000k -b:v 20000k -b:a 196k -ar 44100 -s 3840x2160 "+
		output+".webm",
		output,
		".webm");
		this.setName("vp8_4k");
	}
}
