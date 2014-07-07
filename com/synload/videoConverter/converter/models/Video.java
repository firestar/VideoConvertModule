package com.synload.videoConverter.converter.models;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.Part;

import org.apache.commons.io.FileUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.synload.framework.SynloadFramework;
import com.synload.framework.users.User;
import com.synload.videoConverter.VideoConvertModule;
import com.synload.videoConverter.converter.ConverterProcessing;


@JsonTypeInfo(use=JsonTypeInfo.Id.NAME, include=JsonTypeInfo.As.PROPERTY, property="class")
public class Video implements Serializable{
	public String sourceFile,video,data,mData,fps,id;
	public User account = null;
	//public List<String> convertString = new ArrayList<String>();
	public Long sourceSize, vid = (long) 0;
	public HashMap<String, String> commands = new HashMap<String, String>();
	public Float duration = (float) 0;
	@JsonIgnore
	private Part part = null;
	public Video(){}
	
	public Video(String sourceFile, User user){
		this.account = user;
		String filename = randomString()+".video";
		try {
			FileUtils.moveFile(new File(this.getSourceFile()), new File(VideoConvertModule.prop.getProperty("uploadPath")+filename));
			this.sourceFile = filename;
			this.sourceSize = (new File(sourceFile)).length();
			this.id = this.randomString();
			this.buildVideo();
			this.create();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Video(Long size, Part part, User user){
		this.account = user;
		this.part = part;
		this.sourceSize = size;
		this.id = this.randomString();
		this.buildVideo();
		this.create();
	}
	public Video(ResultSet rs){
		try {
			vid = rs.getLong("vid");
			account = User.findUser(rs.getLong("uid"));
			id = rs.getString("id");
			sourceFile = rs.getString("source_file");
			sourceSize = rs.getLong("source_size");
			fps = rs.getString("fps");
			data = rs.getString("data");
			try {
				commands = (HashMap<String, String>)VideoConvertModule.stringToObject(rs.getString("commands"));
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	public void create(){
			try{
				PreparedStatement s = SynloadFramework.sql.prepareStatement(
					"INSERT INTO `videos` ( `id`, `data`, `fps`, `source_file`, `source_size`, `uid`, `commands` ) VALUES ( ?, ?, ?, ?, ?, ?, ? );"
				);
				s.setString(1, id);
				s.setString(2, data);
				s.setString(3, fps);
				s.setString(4, sourceFile);
				s.setLong(5, sourceSize);
				s.setLong(6, account.getId());
				s.setString(7, VideoConvertModule.objectToString(commands));
				s.execute();
				ResultSet keys = s.getGeneratedKeys();
				if(keys.next()){
					vid = keys.getLong(1);
				}
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
	
	public String getVideo(){
		return video;
	}
	
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
	
	public void setFPS(String framesPerSecond){
		fps = framesPerSecond;
	}
	
	
	
	public String getFps() {
		return fps;
	}

	public void setFps(String fps) {
		this.fps = fps;
	}

	public Long getVid() {
		return vid;
	}

	public void setVid(Long vid) {
		this.vid = vid;
	}

	public void setDuration(Float duration) {
		this.duration = duration;
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
	public List<Subtitle> getSubtitles(){
		return Subtitle.getByVid(this.vid);
	}
	
	@JsonIgnore
	public int subtitleSize(){
		return Subtitle.length(this.vid);
	}
	
	@JsonIgnore
	public List<Task> getTasks(){
		return Task.getByVideoId(this.vid);
	}
	
	@JsonIgnore
	public List<Task> getTasksComplete(){
		return Task.getByVideoIdStatus(this.vid, 0);
	}
	
	@JsonIgnore
	public List<Task> getTasksWaitlist(){
		return Task.getByVideoIdStatus(this.vid, 3);
	}
	
	@JsonIgnore
	public List<Task> getTasksConverting(){
		return Task.getByVideoIdStatus(this.vid, 2);
	}
	@JsonIgnore
	public List<Task> getTasksConvertComplete(){
		return Task.getByVideoIdStatus(this.vid, 1);
	}
	
	@JsonIgnore
	public void addSubtitle( String fileName, String title, String language, HashMap<String, String> commands){
		new Subtitle(fileName, title, language, this.vid, commands);
	}
	
	@JsonIgnore
	public Subtitle getSubtitle( long sid){
		Subtitle sub = Subtitle.getById(sid);
		if(sub.getVid()==this.vid){
			return sub;
		}else{
			return null;
		}
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
		this.prepVideo();
	}
	public void prepVideo(){
		this.data = ConverterProcessing.cmdExec(VideoConvertModule.prop.getProperty("ffmpeg")+" -i "+VideoConvertModule.prop.getProperty("uploadPath")+this.getSourceFile());
		this.video = this.randomString();
		this.duration = ConverterProcessing.getDuration(this);
		this.fps = ConverterProcessing.getFPS(this);
		ConverterProcessing.extractSubs(this);
		this.sourceSize = (new File(VideoConvertModule.prop.getProperty("uploadPath")+this.getSourceFile())).length();
	}
	
	@JsonIgnore
	public static Video getById(long videoId){
		try{
			PreparedStatement s = SynloadFramework.sql.prepareStatement(
				"SELECT `id`, `data`, `fps`, `source_file`, `source_size`, `uid`, `commands`, `vid` FROM `tasks` WHERE `vid`=?"
			);
			s.setLong(1, videoId);
			ResultSet rs = s.executeQuery();
			while(rs.next()){
				Video v = new Video(rs);
				rs.close();
				s.close();
				return v;
			}
			rs.close();
			s.close();
			return null;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	@JsonIgnore
	public static long lengthAll(){
		int i = 0;
		try{
			PreparedStatement s = SynloadFramework.sql.prepareStatement(
					"SELECT `id` FROM `videos`"
			);
			ResultSet rs = s.executeQuery();
			while(rs.next()){
				i++;
			}
		}catch(Exception e){
			e.printStackTrace();
			return 0;
		}
		return i;
	}
	
	@JsonIgnore
	public static long length(long uid){
		int i = 0;
		try{
			PreparedStatement s = SynloadFramework.sql.prepareStatement(
					"SELECT `id` FROM `videos` WHERE `uid`=?"
			);
			s.setLong(1, uid);
			ResultSet rs = s.executeQuery();
			while(rs.next()){
				i++;
			}
		}catch(Exception e){
			e.printStackTrace();
			return 0;
		}
		return i;
	}
	
	@JsonIgnore
	public static List<Video> getAll(int page){
		List<Video> vids = new ArrayList<Video>();
		try{
			PreparedStatement s = SynloadFramework.sql.prepareStatement(
				"SELECT `id`, `data`, `fps`, `source_file`, `source_size`, `uid`, `commands`, `vid` FROM `videos` LIMIT "+(page*25)+", 25"
			);
			ResultSet rs = s.executeQuery();
			while(rs.next()){
				Video v = new Video(rs);
				vids.add(v);
			}
			rs.close();
			s.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		return vids;
	}
	
	@JsonIgnore
	public static List<Video> getAll(int page, long uid){
		List<Video> vids = new ArrayList<Video>();
		try{
			PreparedStatement s = SynloadFramework.sql.prepareStatement(
				"SELECT `id`, `data`, `fps`, `source_file`, `source_size`, `uid`, `commands`, `vid` FROM `videos` WHERE `uid`=? LIMIT "+(page*25)+", 25"
			);
			s.setLong(1, uid);
			ResultSet rs = s.executeQuery();
			while(rs.next()){
				Video v = new Video(rs);
				vids.add(v);
			}
			rs.close();
			s.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		return vids;
	}
}