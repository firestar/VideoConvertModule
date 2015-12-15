package com.synload.converter.presets;

import com.synload.converter.models.Preset;

import net.bramp.ffmpeg.probe.FFmpegProbeResult;

public class VP9480 extends Preset {
	public VP9480(FFmpegProbeResult video, String source, String output, String extra){
		super(
			commandify(
				video, 
				source, 
				output, 
				extra, 
				"%FFMPEG% -i %TEMP%%EXTRA% -threads 8 -r %FPS% -vcodec vp9 -acodec libvorbis -ac 2 -maxrate 1500k -b:v 2500k -b:a 128k -ar 44100 -s 852x480 %FINAL%_vp9_480.webm"
			),
			output,
			"_vp9_480.webm"
		);
		this.setName("vp9_480");
	}
}
