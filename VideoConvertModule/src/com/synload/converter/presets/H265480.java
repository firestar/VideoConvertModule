package com.synload.converter.presets;

import com.synload.converter.models.Preset;

import net.bramp.ffmpeg.probe.FFmpegProbeResult;

public class H265480 extends Preset {
	public H265480(FFmpegProbeResult video, String source, String output, String extra){
		super(
			commandify(
				video, 
				source, 
				output, 
				extra, 
				"%FFMPEG% -i %TEMP%%EXTRA% -threads 8 -r %FPS% -vcodec libx265 -acodec aac -strict experimental -ac 2 -maxrate 1500k -b:v 2500k -b:a 196k -ar 44100 -s 852x480 %FINAL%_h265_480.mp4"
			),
			output,
			"_h265_480.mp4"
		);
		this.setName("h265_480");
	}
}
