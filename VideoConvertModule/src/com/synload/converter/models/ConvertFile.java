package com.synload.converter.models;

import java.util.UUID;

import com.synload.converter.presets.Presets;

public class ConvertFile {
	private String preset;
	private String source;
	private UUID videoUUID;
	private UUID parentUUID;
	public ConvertFile(String source, String preset, UUID uuid, UUID parent){
		this.setPreset(preset);
		this.setSource(source);
		this.setVideoUUID(uuid);
		this.setParentUUID(parent);
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getPreset() {
		return preset;
	}
	public void setPreset(String preset) {
		this.preset = preset;
	}
	public UUID getVideoUUID() {
		return videoUUID;
	}
	public void setVideoUUID(UUID videoUUID) {
		this.videoUUID = videoUUID;
	}
	public UUID getParentUUID() {
		return parentUUID;
	}
	public void setParentUUID(UUID parentUUID) {
		this.parentUUID = parentUUID;
	}
}
