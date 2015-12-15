package com.synload.converter.presets;

import com.synload.converter.models.ConvertFile;
import com.synload.converter.models.Preset;

import net.bramp.ffmpeg.probe.FFmpegProbeResult;

public class H264480 extends Preset {
	public H264480(FFmpegProbeResult video, String source, String output, String extra){
		super(
			commandify(
				video, 
				source, 
				output, 
				extra, 
				"%FFMPEG% -i %TEMP%%EXTRA% -threads 8 -r %FPS% -vcodec libx264 -acodec aac -strict experimental -ac 2 -maxrate 1500k -b:v 2500k -b:a 128k -ar 44100 -s 852x480 %FINAL%_h264_480.mp4"
			),
			output,
			"_h264_480.mp4"
		);
		this.setName("h264_480");
	}
}
