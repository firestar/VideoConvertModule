package com.synload.videoConverter.converter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.synload.framework.SynloadFramework;
import com.synload.videoConverter.converter.models.Video;

public class Converter{
	public Thread cThread;
	public ConverterThread processing = null;
	//public static List<Video> queue = new ArrayList<Video>();
	public HashMap<String,Object> current = new HashMap<String,Object>();
	public static Hashtable<String,Video> threadqueue = new Hashtable<String,Video>();
	public Converter(){
		processing = new ConverterThread(this);
		cThread = new Thread(processing);
	}
	public void start(){
		cThread.setDaemon(true);
		cThread.start();
	}
	public Thread getcThread() {
		return cThread;
	}
	public void setcThread(Thread cThread) {
		this.cThread = cThread;
	}
}