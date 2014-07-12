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
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mysql.jdbc.Statement;
import com.synload.framework.SynloadFramework;
import com.synload.framework.users.User;
import com.synload.videoConverter.VideoConvertModule;
import com.synload.videoConverter.converter.ConverterProcessing;


@JsonTypeInfo(use=JsonTypeInfo.Id.NAME, include=JsonTypeInfo.As.PROPERTY, property="class")
public class Video implements Serializable{
	public String sourceFile,video,fps,id;
	public JsonNode data = null;
	public User account = null;
	//public List<String> convertString = new ArrayList<String>();
	public Long sourceSize, vid = (long) 0;
	
	@JsonIgnore
	public HashMap<String, String> commands = new HashMap<String, String>();
	
	public double duration = (double) 0;
	
	@JsonIgnore
	private Part part = null;
	
	public Video(){}
	
	public Video(String sourceFile, User user){
		this.account = user;
		String filename = randomString()+".video";
		try {
			this.sourceFile = filename;
			this.sourceSize = (new File(sourceFile)).length();
			FileUtils.moveFile(new File(sourceFile), new File(VideoConvertModule.prop.getProperty("uploadPath")+filename));
			this.id = this.randomString();
			try {
				this.prepVideo();
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			this.create();
			ConverterProcessing.extractSubs(this);
			try {
				if(this.getSubtitles().size()>0){
					ConverterProcessing.removeSubs(this);
				}
			} catch (IOException | InterruptedException e) {
				e.printStackTrace();
			}
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
		ConverterProcessing.extractSubs(this);
		try {
			if(this.getSubtitles().size()>0){
				ConverterProcessing.removeSubs(this);
			}
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}
	public Video(ResultSet rs){
		try {
			vid = rs.getLong("vid");
			account = User.findUser(rs.getLong("uid"));
			id = rs.getString("id");
			sourceFile = rs.getString("source_file");
			sourceSize = rs.getLong("source_size");
			duration = rs.getDouble("duration");
			fps = rs.getString("fps");
			//data = rs.getString("data");
			try {
				commands = (HashMap<String, String>)VideoConvertModule.stringToObject(rs.getString("commands"));
			} catch (Exception e) {}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	public void create(){
			try{
				PreparedStatement s = SynloadFramework.sql.prepareStatement(
					"INSERT INTO `videos` ( `id`, `data`, `fps`, `source_file`, `source_size`, `uid`, `commands`, `duration` ) VALUES ( ?, ?, ?, ?, ?, ?, ?, ? );",
					Statement.RETURN_GENERATED_KEYS
				);
				s.setString(1, id);
				s.setString(2, data.toString());
				s.setString(3, fps);
				s.setString(4, sourceFile);
				s.setLong(5, sourceSize);
				s.setLong(6, account.getId());
				s.setString(7, VideoConvertModule.objectToString(commands));
				s.setDouble(8, duration);
				s.execute();
				ResultSet keys = s.getGeneratedKeys();
				if(keys.next()){
					vid = keys.getLong(1);
				}
				keys.close();
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
		try{
			PreparedStatement s = SynloadFramework.sql.prepareStatement(
				"UPDATE `videos` SET `source_size`=? WHERE `vid`=?"
			);
			s.setLong(1, sourceSize);
			s.setLong(2, this.getVid());
			s.execute();
			s.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		this.sourceSize = sourceSize;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public String getSourceFile() {
		return sourceFile;
	}

	public void setSourceFile(String sourceFile) {
		this.sourceFile = sourceFile;
	}
	
	
	public double getDuration(){
		return duration;
	}

	public JsonNode getData(){
		return data;
	}
	
	public void setFPS(String fps) {
		try{
			PreparedStatement s = SynloadFramework.sql.prepareStatement(
				"UPDATE `videos` SET `fps`=? WHERE `vid`=?"
			);
			s.setString(1, fps);
			s.setLong(2, this.getVid());
			s.execute();
			s.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		this.fps = fps;
	}

	public Long getVid() {
		return vid;
	}

	public void setVid(Long vid) {
		this.vid = vid;
	}

	public void setDuration(Double duration) {
		try{
			PreparedStatement s = SynloadFramework.sql.prepareStatement(
				"UPDATE `videos` SET `duration`=? WHERE `vid`=?"
			);
			s.setDouble(1, duration);
			s.setLong(2, this.getVid());
			s.execute();
			s.close();
		}catch(Exception e){
			e.printStackTrace();
		}
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
		try {
			this.prepVideo();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void prepVideo() throws JsonProcessingException, IOException{
		ObjectMapper m = new ObjectMapper();
		String dataString = ConverterProcessing.cmdExecOut(VideoConvertModule.prop.getProperty("ffprobe")+" -v quiet -print_format json -show_format -show_streams "+VideoConvertModule.prop.getProperty("uploadPath")+this.getSourceFile());
		this.data = m.readTree( dataString );
		this.video = this.randomString();
		this.duration = ConverterProcessing.getDuration(this);
		this.fps = ConverterProcessing.getFPS(this);
		this.sourceSize = (new File(VideoConvertModule.prop.getProperty("uploadPath")+this.getSourceFile())).length();
	}
	
	@JsonIgnore
	public static Video getByVId(long videoId){
		try{
			PreparedStatement s = SynloadFramework.sql.prepareStatement(
				"SELECT `id`, `data`, `fps`, `source_file`, `source_size`, `uid`, `commands`, `vid`, `duration` FROM `videos` WHERE `vid`=?"
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
	public static Video getById(String videoId){
		try{
			PreparedStatement s = SynloadFramework.sql.prepareStatement(
				"SELECT `id`, `data`, `fps`, `source_file`, `source_size`, `uid`, `commands`, `vid`, `duration` FROM `videos` WHERE `id`=?"
			);
			s.setString(1, videoId);
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
	public Task newTask(long uid, long sid, String preset, String statusURL, String uploadURL, String cancelURL){
		return new Task( uid, this.getVid(), sid, preset, statusURL, uploadURL, cancelURL);
	}
	
	@JsonIgnore
	public static Video getByUUID(String videoUUID){
		try{
			PreparedStatement s = SynloadFramework.sql.prepareStatement(
				"SELECT `id`, `data`, `fps`, `source_file`, `source_size`, `uid`, `commands`, `vid`, `duration` FROM `tasks` WHERE `id`=?"
			);
			s.setString(1, videoUUID);
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
		try{
			PreparedStatement s = SynloadFramework.sql.prepareStatement(
					"SELECT COUNT(`id`) FROM `videos`"
			);
			ResultSet rs = s.executeQuery();
			while(rs.next()){
				long total = rs.getLong("COUNT(`id`)");
				rs.close();
				s.close();
				return total;
			}
			rs.close();
			s.close();
		}catch(Exception e){
			e.printStackTrace();
			return 0;
		}
		return 0;
	}
	
	@JsonIgnore
	public static long length(long uid){
		int i = 0;
		try{
			PreparedStatement s = SynloadFramework.sql.prepareStatement(
					"SELECT COUNT(`id`) FROM `videos` WHERE `uid`=?"
			);
			s.setLong(1, uid);
			ResultSet rs = s.executeQuery();
			while(rs.next()){
				long total = rs.getLong("COUNT(`id`)");
				rs.close();
				s.close();
				return total;
			}
			rs.close();
			s.close();
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
				"SELECT `id`, `data`, `fps`, `source_file`, `source_size`, `uid`, `commands`, `vid`, `duration` FROM `videos` LIMIT "+(page*25)+", 25"
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
				"SELECT `id`, `data`, `fps`, `source_file`, `source_size`, `uid`, `commands`, `vid`, `duration` FROM `videos` WHERE `uid`=? LIMIT "+(page*25)+", 25"
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