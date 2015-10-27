package com.synload.converter.presets;

import com.synload.converter.models.ConvertFile;
import com.synload.converter.models.Preset;

import net.bramp.ffmpeg.probe.FFmpegProbeResult;

public class H2654K extends Preset {
	public H2654K(FFmpegProbeResult video, String source, String output, String extra){
		super(commandify(video, source, output, extra, "%FFMPEG% -i %TEMP%%EXTRA% -threads 8 -r %FPS% -vcodec libx265 -acodec aac -strict experimental -ac 2 -maxrate 23000k -b:v 20000k -b:a 196k -ar 44100 -s 3840x2160 %FINAL%.mp4"));
	}
}
