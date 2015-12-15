package com.synload.converter.presets;

import com.synload.converter.models.Preset;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;

public class H265 extends Preset {
	public H265(FFmpegProbeResult video, String source, String output, String extra){
		super(
			commandify(
				video, 
				source, 
				output, 
				extra, 
				"%FFMPEG% -i %TEMP%%EXTRA% -threads 8 -r %FPS% -vcodec libx265 -acodec aac -strict experimental -ac 2 -maxrate 3000k -b:v 5000k -b:a 196k -ar 44100 -s 1920x1080 %FINAL%_h265_1080.mp4"
			),
			output,
			"_h265_1080.mp4"
		);
		this.setName("h265_1080");
	}
}

