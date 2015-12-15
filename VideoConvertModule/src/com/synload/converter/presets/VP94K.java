package com.synload.converter.presets;

import com.synload.converter.models.ConvertFile;
import com.synload.converter.models.Preset;

import net.bramp.ffmpeg.probe.FFmpegProbeResult;

public class VP94K extends Preset {
	public VP94K(FFmpegProbeResult video, String source, String output, String extra){
		super(
			commandify(
				video, 
				source, 
				output, 
				extra, 
				"%FFMPEG% -i %TEMP%%EXTRA% -threads 8 -r %FPS% -vcodec vp9 -acodec libvorbis -ac 2 -maxrate 23000k -b:v 20000k -b:a 196k -ar 44100 -s 3840x2160 %FINAL%_vp9_4k.webm"
			),
			output,
			"_vp9_4k.webm"
		);
		this.setName("vp9_4k");
	}
}


