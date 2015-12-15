package com.synload.converter.presets;

import com.synload.converter.models.ConvertFile;
import com.synload.converter.models.Preset;

import net.bramp.ffmpeg.probe.FFmpegProbeResult;

public class VP9 extends Preset {
	public VP9(FFmpegProbeResult video, String source, String output, String extra){
		super(
			commandify(
				video, 
				source, 
				output, 
				extra, 
				"%FFMPEG% -i %TEMP%%EXTRA% -threads 8 -r %FPS% -vcodec vp9 -acodec libvorbis -ac 2 -maxrate 1000k -b:v 1000k -b:a 128k -ar 44100 -s 1920x1080 %FINAL%_vp9_1080.webm"
			),
			output,
			"_vp9_1080.webm"
		);
		this.setName("vp9_1080");
	}
}
