package com.synload.videoConverter;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.synload.framework.SynloadFramework;
import com.synload.videoConverter.converter.Video;

public class SynloadConverter{
	public SynloadConverter(){}
	public static List<Video> uploadQueue = new ArrayList<Video>();
	public static List<Video> history = new ArrayList<Video>();
	public static void addHistory(Video value){
		history.add(value);
		storeHistory();
	}
	public static void addUploadQueue(Video value){
		uploadQueue.add(value);
		storeUploadQueue();
	}
	public static void storeHistory(){
		FileOutputStream fout;
		try {
			fout = new FileOutputStream("history.json");
			ObjectOutputStream oos = new ObjectOutputStream(fout);
			oos.writeObject(history);
		} catch (IOException e) {
			if(SynloadFramework.debug){
				e.printStackTrace();
			}
		}
	}
	public static void storeUploadQueue(){
		FileOutputStream fout;
		try {
			fout = new FileOutputStream("uploadqueue.json");
			ObjectOutputStream oos = new ObjectOutputStream(fout);
			oos.writeObject(uploadQueue);
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
		mapper.registerSubtypes(Video.class);*/
		try {
			//history = mapper.reader(mapper.getTypeFactory().constructCollectionType(List.class, Object.class)).readValue(new File("history.json"));
			FileInputStream streamIn = new FileInputStream("history.json");
            ObjectInputStream objectinputstream = new ObjectInputStream(streamIn);
            history = (List) objectinputstream.readObject();
		} catch (IOException | ClassNotFoundException e) {
			if(SynloadFramework.debug){
				e.printStackTrace();
			}
		}
		try {
			//uploadQueue = mapper.reader(mapper.getTypeFactory().constructCollectionType(List.class, Object.class)).readValue(new File("uploadqueue.json"));
			FileInputStream streamIn = new FileInputStream("uploadqueue.json");
            ObjectInputStream objectinputstream = new ObjectInputStream(streamIn);
            uploadQueue = (List) objectinputstream.readObject();
		} catch (IOException | ClassNotFoundException e) {
			if(SynloadFramework.debug){
				e.printStackTrace();
			}
		}
	}
}