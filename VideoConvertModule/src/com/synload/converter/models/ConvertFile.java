package com.synload.converter.models;

import com.synload.converter.presets.Presets;

public class ConvertFile {
	private String preset;
	private String source;
	private String output;
	public ConvertFile(String source, String output, String preset){
		this.setPreset(preset);
		this.setOutput(output);
		this.setSource(source);
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getOutput() {
		return output;
	}
	public void setOutput(String output) {
		this.output = output;
	}
	public String getPreset() {
		return preset;
	}
	public void setPreset(String preset) {
		this.preset = preset;
	}
}
