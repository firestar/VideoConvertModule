package com.synload.videoConverter.converter.models;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Serializable;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Part;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.synload.framework.SynloadFramework;
import com.synload.framework.users.User;
import com.synload.videoConverter.VideoConvertModule;
import com.synload.videoConverter.converter.Converter;
import com.synload.videoConverter.converter.ConverterProcessing;


@JsonTypeInfo(use=JsonTypeInfo.Id.NAME, include=JsonTypeInfo.As.PROPERTY, property="class")
public class Video implements Serializable{
	public String fileName,sourceFile,video,data,mData,fps,id;
	public User account = null;
	//public List<String> convertString = new ArrayList<String>();
	public Long sourceSize, vid = (long) 0;
	public HashMap<String, String> commands = new HashMap<String, String>();
	public Float duration = (float) 0;
	@JsonIgnore
	private Part part = null;
	public Video(){}
	
	public Video(String fileName, Long size, Part part, User user){
		this.account = user;
		this.fileName = fileName;
		this.part = part;
		this.sourceSize = size;
		this.id = this.randomString();
		this.create();
	}
	public void create(){
			try{
				PreparedStatement s = SynloadFramework.sql.prepareStatement(
					"INSERT INTO `videos` ( `id`, `filename`, `data`, `fps`, `source_file`, `source_size`, `uid`, `commands` ) VALUES ( ?, ?, ?, ?, ?, ?, ?, ? );"
				);
				s.setString(1, id);
				s.setString(2, fileName);
				s.setString(3, data);
				s.setString(4, fps);
				s.setString(5, sourceFile);
				s.setLong(6, sourceSize);
				s.setLong(7, account.getId());
				s.setString(7, VideoConvertModule.objectToString(commands));
				s.execute();
			}catch(Exception e){
				e.printStackTrace();
			}
	}
	public HashMap<String, String> getCommands() {
		return commands;
	}

	public void setCommands(HashMap<String, String> commands) {
		this.commands = commands;
	}

	/*public void setParams(HashMap<String, String> params) {
		this.params = params;
	}*/

	public User getAccount() {
		return account;
	}

	public void setAccount(User account2) {
		this.account = account2;
	}
	public Long getSourceSize() {
		return sourceSize;
	}

	public void setSourceSize(Long sourceSize) {
		this.sourceSize = sourceSize;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName(){
		return fileName;
	}
	
	
	/*public HashMap<String, String> getParams(){
		return params;
	}*/
	
	public String getVideo(){
		return video;
	}
	
	/*public String getVideoFile(){
		return videofile;
	}
	
	public void setVideoFile(String videoFile){
		this.videofile = videoFile;
	}*/
	
	/*@JsonIgnore
	public String getFormat(){
		if(getParams().containsKey("size")){
			if(getParams().get("size").equalsIgnoreCase("custom")){
				if(getParams().containsKey("quality")){
					return getParams().get("quality");
				}else{
					return "vp8";
				}
			}else{
				return getParams().get("size");
			}
		}else{
			return "vp8";
		}
	}*/
	
	/*public String getTemp(){
		return pathToVideo;
	}
	
	public String getTarget(){
		return uploadURL;
	}*/
	
	public long getSize(){
		return sourceSize;
	}
	
	
	public String getSourceFile() {
		return sourceFile;
	}

	public void setSourceFile(String sourceFile) {
		this.sourceFile = sourceFile;
	}

	public String getData(){
		return data;
	}
	
	
	public float getDuration(){
		return duration;
	}
	
	
	public String getMData(){
		return mData;
	}
	
	public void setMData(String mkvMergeData){
		mData = mkvMergeData;
	}
	
	
	public void setName(String fileName){
		this.fileName = fileName;
	}
	
	public void setFPS(String framesPerSecond){
		fps = framesPerSecond;
	}
	
	
	public String getFPS(){
		return fps;
	}
	
	@JsonIgnore
	public void delete(){
		(new File(VideoConvertModule.prop.getProperty("videoPath")+this.getSourceFile())).delete();
	}
	
	@JsonIgnore
	public String randomString(){
		SecureRandom random = new SecureRandom();
	    return new BigInteger(130, random).toString(32);
	}
	
	@JsonIgnore
	public void buildVideo(){
		try {
			this.setSourceFile( randomString()+".video");
			InputStream is = part.getInputStream();
			OutputStream out = new FileOutputStream(VideoConvertModule.prop.getProperty("uploadPath")+this.getSourceFile());
			int bytesRead;
			byte[] buffer = new byte[8 * 1024];
			while ((bytesRead = is.read(buffer)) != -1) {
				out.write(buffer, 0, bytesRead);
			}
			out.close();
			is.close();
			part.delete();
		} catch (IOException e) {
			if(SynloadFramework.debug){
				e.printStackTrace();
			}
		}
		
		this.data = ConverterProcessing.cmdExec(VideoConvertModule.prop.getProperty("ffmpeg")+" -i "+VideoConvertModule.prop.getProperty("uploadPath")+this.getSourceFile());
		this.video = this.randomString();
		this.duration = ConverterProcessing.getDuration(this);
		this.sourceSize = (new File(VideoConvertModule.prop.getProperty("uploadPath")+this.getSourceFile())).length();
		Converter.addQueue(this);
		System.out.println("file added to queue!");
	}
	
	@JsonIgnore
	public void prepVideo(){
		fps = ConverterProcessing.getFPS(this);
		try{
			Process p = Runtime.getRuntime().exec(VideoConvertModule.prop.getProperty("mkvmerge")+" -i "+VideoConvertModule.prop.getProperty("uploadPath")+this.getSourceFile());
	        InputStream is = p.getInputStream();
	        InputStreamReader isr = new InputStreamReader(is);
	        BufferedReader br = new BufferedReader(isr);
			String line;
			while ((line = br.readLine()) != null) {
				if(SynloadFramework.debug){
					System.out.println(line);
				}
				//ConverterProcessing.getH264(this,line);
				ConverterProcessing.extractSubs(this,line);
			}
			br.close();
			isr.close();
			is.close();
			/*if(this.subs.size()>0){
				ConverterProcessing.removeSubs(this);
			}*/
		} catch (IOException e) {
			if(SynloadFramework.debug){
				e.printStackTrace();
			}
		}
	}
}