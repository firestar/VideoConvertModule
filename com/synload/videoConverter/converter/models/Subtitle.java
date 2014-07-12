package com.synload.videoConverter.converter.models;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.mysql.jdbc.Statement;
import com.synload.framework.SynloadFramework;
import com.synload.videoConverter.VideoConvertModule;

@JsonTypeInfo(use=JsonTypeInfo.Id.NAME, include=JsonTypeInfo.As.PROPERTY, property="class")
public class Subtitle {
	public String fileName, title, language = "";
	public Long sid, vid = (long) 0;
	
	@JsonIgnore
	public HashMap<String, String> commands = new HashMap<String, String>();
	
	public Subtitle(ResultSet rs){
		try {
			fileName = rs.getString("filename");
			sid = rs.getLong("sid");
			vid = rs.getLong("vid");
			language = rs.getString("language");
			title = rs.getString("title");
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
	public Subtitle( String fileName, String title, String language, Long vid, HashMap<String, String> commands ){
		this.fileName = fileName;
		this.title = title;
		this.language = language;
		this.vid = vid;
		this.commands = commands;
		this.create();
	}
	public void create(){
		try{
			PreparedStatement s = SynloadFramework.sql.prepareStatement(
				"INSERT INTO `subtitles` ( `filename`, `vid`, `language`, `title`, `commands` ) VALUES ( ?, ?, ?, ?, ? );",
				Statement.RETURN_GENERATED_KEYS
			);
			s.setString(1, fileName);
			s.setLong(2, vid);
			s.setString(3, language);
			s.setString(4, title);
			s.setString(5, VideoConvertModule.objectToString(commands));
			s.execute();
			ResultSet keys = s.getGeneratedKeys();
			if(keys.next()){
				sid = keys.getLong(1);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	public Long getSid() {
		return sid;
	}
	public void setSid(Long sid) {
		this.sid = sid;
	}
	public Long getVid() {
		return vid;
	}
	public void setVid(Long vid) {
		this.vid = vid;
	}
	public static Subtitle getById(long subtitleId){
		try{
			PreparedStatement s = SynloadFramework.sql.prepareStatement(
				"SELECT `sid`, `filename`, `vid`, `language`, `title`, `commands` FROM `subtitles` WHERE `sid`=?"
			);
			s.setLong(1, subtitleId);
			ResultSet rs = s.executeQuery();
			while(rs.next()){
				Subtitle sub = new Subtitle(rs);
				rs.close();
				s.close();
				return sub;
			}
			rs.close();
			s.close();
			return null;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	public static int length(long videoId){
		int i = 0;
		try{
			PreparedStatement s = SynloadFramework.sql.prepareStatement(
				"SELECT `sid` FROM `subtitles` WHERE `vid`=?"
			);
			s.setLong(1, videoId);
			ResultSet rs = s.executeQuery();
			while(rs.next()){
				i++;
			}
			rs.close();
			s.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		return i;
	}
	public static List<Subtitle> getByVid(long videoId){
		List<Subtitle> subs = new ArrayList<Subtitle>();
		try{
			PreparedStatement s = SynloadFramework.sql.prepareStatement(
				"SELECT `sid`, `filename`, `vid`, `language`, `title`, `commands` FROM `subtitles` WHERE `vid`=? ORDER BY `language` DESC"
			);
			s.setLong(1, videoId);
			ResultSet rs = s.executeQuery();
			while(rs.next()){
				Subtitle sub = new Subtitle(rs);
				subs.add(sub);
			}
			rs.close();
			s.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		return subs;
	}
}
