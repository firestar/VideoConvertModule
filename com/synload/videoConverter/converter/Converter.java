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

public class Converter{
	public Thread cThread;
	public ConverterThread processing = null;
	public static List<Video> queue = new ArrayList<Video>();
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
	public static void addQueue(Video value){
		queue.add(value);	
		storeQueue();
	}
	public static void storeQueue(){
		/*ObjectMapper mapper = new ObjectMapper()
			.setVisibility(PropertyAccessor.FIELD, Visibility.ANY)
			.enableDefaultTypingAsProperty(DefaultTyping.OBJECT_AND_NON_CONCRETE, "class")
			.enable(SerializationFeature.INDENT_OUTPUT);
		mapper.registerSubtypes(Video.class);
		try {
			ObjectWriter ow = mapper.writerWithType(mapper.getTypeFactory().constructCollectionType(List.class, Video.class));
			ow.writeValue(new File("queue.json"), queue);
		} catch (IOException e) {
			if(SynloadFramework.debug){
				e.printStackTrace();
			}
		}*/
		FileOutputStream fout;
		try {
			fout = new FileOutputStream("queue.json");
			ObjectOutputStream oos = new ObjectOutputStream(fout);
			oos.writeObject(queue);
		} catch (IOException e) {
			if(SynloadFramework.debug){
				e.printStackTrace();
			}
		}
	}
	public static void load(){
		/*ObjectMapper mapper = new ObjectMapper()
			.setVisibility(PropertyAccessor.FIELD, Visibility.ANY)
			.enableDefaultTypingAsProperty(DefaultTyping.OBJECT_AND_NON_CONCRETE, "class")
			.enable(SerializationFeature.INDENT_OUTPUT);
		mapper.registerSubtypes(Video.class);
		try {
			queue = mapper.reader(mapper.getTypeFactory().constructCollectionType(List.class, Video.class)).readValue(new File("queue.json"));
		} catch (IOException e) {
			if(SynloadFramework.debug){
				e.printStackTrace();
			}
		}*/
		try {
			//uploadQueue = mapper.reader(mapper.getTypeFactory().constructCollectionType(List.class, Object.class)).readValue(new File("uploadqueue.json"));
			FileInputStream streamIn = new FileInputStream("queue.json");
            ObjectInputStream objectinputstream = new ObjectInputStream(streamIn);
            queue = (List) objectinputstream.readObject();
		} catch (IOException | ClassNotFoundException e) {
			if(SynloadFramework.debug){
				e.printStackTrace();
			}
		}
	}
}