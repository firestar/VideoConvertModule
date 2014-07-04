package com.synload.videoConverter.converter.models;

import java.io.IOException;
import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.synload.framework.SynloadFramework;
import com.synload.videoConverter.VideoConvertModule;

@JsonTypeInfo(use=JsonTypeInfo.Id.NAME, include=JsonTypeInfo.As.PROPERTY, property="class")
public class Task implements Serializable{
	public String fileName, statusURL, uploadURL, cancelURL, preset = "";
	public HashMap<String, String> commands = new HashMap<String, String>();
	public Long vid, sid, uid, id = (long) 0;
	public int complete = 0;
	public Task(Long uid, Long vid, Long sid, Long id){
		
	}
	public Task(ResultSet rs){
		try {
			fileName = rs.getString("filename");
			statusURL = rs.getString("statusURL");
			cancelURL = rs.getString("cancelURL");
			uploadURL = rs.getString("uploadURL");
			vid = rs.getLong("vid");
			sid = rs.getLong("sid");
			uid = rs.getLong("uid");
			id = rs.getLong("id");
			preset = rs.getString("preset");
			complete = rs.getInt("complate");
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
				"INSERT INTO `tasks` ( `vid`, `sid`, `uid`, `preset`, `filename`, `uploadURL`, `cancelURL`, `statusURL`, `complete`, `commands` ) VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ? );"
			);
			s.setLong(1, vid);
			s.setLong(2, sid);
			s.setLong(3, uid);
			s.setString(4, preset);
			s.setString(5, fileName);
			s.setString(6, uploadURL);
			s.setString(7, cancelURL);
			s.setString(8, statusURL);
			s.setLong(9, complete);
			s.setString(10, VideoConvertModule.objectToString(commands));
			s.execute();
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
	public String getStatusURL() {
		return statusURL;
	}
	public void setStatusURL(String statusURL) {
		this.statusURL = statusURL;
	}
	public String getUploadURL() {
		return uploadURL;
	}
	public void setUploadURL(String uploadURL) {
		this.uploadURL = uploadURL;
	}
	public String getCancelURL() {
		return cancelURL;
	}
	public void setCancelURL(String cancelURL) {
		this.cancelURL = cancelURL;
	}
	public String getPreset() {
		return preset;
	}
	public void setPreset(String preset) {
		this.preset = preset;
	}
	public Long getVid() {
		return vid;
	}
	public void setVid(Long vid) {
		this.vid = vid;
	}
	public Long getSid() {
		return sid;
	}
	public void setSid(Long sid) {
		this.sid = sid;
	}
	public static long lengthNew(){
		int i = 0;
		try{
			PreparedStatement s = SynloadFramework.sql.prepareStatement(
					"SELECT `id` FROM `tasks` WHERE `complete`='0'"
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
	public static List<Task> getNew(){
		List<Task> tasks = new ArrayList<Task>();
		try{
			PreparedStatement s = SynloadFramework.sql.prepareStatement(
				"SELECT `id`, `uid`, `vid`, `sid`, `filename`, `uploadURL`, `cancelURL`, `statusURL`, `complate`, `commands` FROM `tasks` WHERE `complete`='0'"
			);
			ResultSet rs = s.executeQuery();
			while(rs.next()){
				Task tk = new Task(rs);
				tasks.add(tk);
			}
			rs.close();
			s.close();
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
		return tasks;
	}
}
