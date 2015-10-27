package com.synload.converter.models;

import java.io.Serializable;
import java.util.UUID;

public class FileData implements Serializable {
	private UUID id = null; 
	private String fileName = "";
	public FileData(UUID id, String fileName){
		this.id = id;
		this.fileName = fileName;
	}
	public UUID getId() {
		return id;
	}
	public void setId(UUID id) {
		this.id = id;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
}
