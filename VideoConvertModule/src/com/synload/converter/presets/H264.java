package com.synload.converter.presets;

import com.synload.converter.models.ConvertFile;
import com.synload.converter.models.Preset;

import net.bramp.ffmpeg.probe.FFmpegProbeResult;

public class H264 extends Preset {
	public H264(FFmpegProbeResult video, String source, String output, String extra){
		super(commandify(video, source, output, extra, "%FFMPEG% -i %TEMP%%EXTRA% -threads 8 -r %FPS% -vcodec libx264 -acodec aac -strict experimental -ac 2 -maxrate 4000k -b:v 3000k -b:a 256k -ar 44100 -s 1920x1080 %FINAL%.mp4"));
	}
}
