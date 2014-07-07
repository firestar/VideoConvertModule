package com.synload.videoConverter.converter.models;

import java.io.IOException;
import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.synload.framework.SynloadFramework;
import com.synload.framework.users.User;
import com.synload.videoConverter.VideoConvertModule;

@JsonTypeInfo(use=JsonTypeInfo.Id.NAME, include=JsonTypeInfo.As.PROPERTY, property="class")
public class Task implements Serializable{
	public String statusURL, uploadURL, cancelURL, preset = "";
	public HashMap<String, String> commands = new HashMap<String, String>();
	public Long vid, sid, uid, id, ovid = (long) 0;
	public int complete, status = 0;
	public Task(Long uid, Long vid, Long sid, Long id, String preset, String statusURL, String uploadURL, String cancelURL){
		this.uid = uid;
		this.vid = vid;
		this.sid = sid;
		this.id = id;
		this.preset = preset;
		this.statusURL = statusURL;
		this.uploadURL = uploadURL;
		this.cancelURL = cancelURL;
		this.create();
	}
	public Task(ResultSet rs){
		try {
			statusURL = rs.getString("statusURL");
			cancelURL = rs.getString("cancelURL");
			uploadURL = rs.getString("uploadURL");
			vid = rs.getLong("vid");
			ovid = rs.getLong("ovid");
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
				"INSERT INTO `tasks` ( `vid`, `sid`, `uid`, `preset`, `uploadURL`, `cancelURL`, `statusURL`, `complete`, `commands` ) VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ? );"
			);
			s.setLong(1, vid);
			s.setLong(2, sid);
			s.setLong(3, uid);
			s.setString(4, preset);
			s.setString(5, uploadURL);
			s.setString(6, cancelURL);
			s.setString(7, statusURL);
			s.setLong(8, complete);
			s.setString(9, VideoConvertModule.objectToString(commands));
			s.execute();
			ResultSet keys = s.getGeneratedKeys();
			if(keys.next()){
				id = keys.getLong(1);
			}
			s.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public String getStatusURL() {
		return statusURL;
	}
	public void setStatusURL(String statusURL) {
		try{
			PreparedStatement s = SynloadFramework.sql.prepareStatement(
				"UPDATE `tasks` SET `statusURL`=? WHERE `id`=?"
			);
			s.setString(1, statusURL);
			s.setLong(2, id);
			s.execute();
			s.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		this.statusURL = statusURL;
	}
	public String getUploadURL() {
		return uploadURL;
	}
	public void setUploadURL(String uploadURL) {
		try{
			PreparedStatement s = SynloadFramework.sql.prepareStatement(
				"UPDATE `tasks` SET `uploadURL`=? WHERE `id`=?"
			);
			s.setString(1, uploadURL);
			s.setLong(2, id);
			s.execute();
			s.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		this.uploadURL = uploadURL;
	}
	public String getCancelURL() {
		return cancelURL;
	}
	public void setCancelURL(String cancelURL) {
		try{
			PreparedStatement s = SynloadFramework.sql.prepareStatement(
				"UPDATE `tasks` SET `cancelURL`=? WHERE `id`=?"
			);
			s.setString(1, cancelURL);
			s.setLong(2, id);
			s.execute();
			s.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		this.cancelURL = cancelURL;
	}
	public String getPreset() {
		return preset;
	}
	public void setPreset(String preset) {
		try{
			PreparedStatement s = SynloadFramework.sql.prepareStatement(
				"UPDATE `tasks` SET `preset`=? WHERE `id`=?"
			);
			s.setString(1, preset);
			s.setLong(2, id);
			s.execute();
			s.close();
		}catch(Exception e){
			e.printStackTrace();
		}
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
	public HashMap<String, String> getCommands() {
		return commands;
	}
	public void setCommands(HashMap<String, String> commands) {
		this.commands = commands;
	}
	public Long getUid() {
		return uid;
	}
	public void setUid(Long uid) {
		this.uid = uid;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public int getComplete() {
		return complete;
	}
	public int getStatus() {
		return status;
	}
	public void addCommand( String key, String lines){
		commands.put(key, lines);
		try{
			PreparedStatement s = SynloadFramework.sql.prepareStatement(
				"UPDATE `tasks` SET `commands`=? WHERE `id`=?"
			);
			s.setString(1, VideoConvertModule.objectToString(commands));
			s.setLong(2, id);
			s.execute();
			s.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void setStatus(int status) {
		try{
			PreparedStatement s = SynloadFramework.sql.prepareStatement(
				"UPDATE `tasks` SET `status`=? WHERE `id`=?"
			);
			s.setInt(1, status);
			s.setLong(2, id);
			s.execute();
			s.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		this.status = status;
	}
	public Long getOvid() {
		return ovid;
	}
	public void setOvid(Long ovid) {
		try{
			PreparedStatement s = SynloadFramework.sql.prepareStatement(
				"UPDATE `tasks` SET `ovid`=? WHERE `id`=?"
			);
			s.setLong(1, ovid);
			s.setLong(2, id);
			s.execute();
			s.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		this.ovid = ovid;
	}
	
	public void setComplete(int complete) {
		try{
			PreparedStatement s = SynloadFramework.sql.prepareStatement(
				"UPDATE `tasks` SET `complete`=? WHERE `id`=?"
			);
			s.setInt(1, complete);
			s.setLong(2, id);
			s.execute();
			s.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		this.complete = complete;
	}
	
	public Video getVideo(){
		return Video.getById(this.vid);
	}
	public Subtitle getSubtitle(){
		return Subtitle.getById(this.sid);
	}
	public User getUser(){
		return User.findUser(this.uid);
	}
	public Video getOutput(){
		return Video.getById(ovid);
	}
	
	public static long lengthNew(){
		int i = 0;
		try{
			PreparedStatement s = SynloadFramework.sql.prepareStatement(
					"SELECT `id` FROM `tasks` WHERE `status`='3'"
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
	public static List<Task> getByVideoId(long videoId){
		List<Task> tasks = new ArrayList<Task>();
		try{
			PreparedStatement s = SynloadFramework.sql.prepareStatement(
				"SELECT `id`, `uid`, `vid`, `sid`, `ovid`, `uploadURL`, `cancelURL`, `statusURL`, `complate`, `commands`, `preset`, `status` FROM `tasks` WHERE `vid`=?"
			);
			s.setLong(1, videoId);
			ResultSet rs = s.executeQuery();
			while(rs.next()){
				Task tk = new Task(rs);
				tasks.add(tk);
			}
			rs.close();
			s.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		return tasks;
	}
	public static List<Task> getByStatus(int status){
		List<Task> tasks = new ArrayList<Task>();
		try{
			PreparedStatement s = SynloadFramework.sql.prepareStatement(
				"SELECT `id`, `uid`, `vid`, `sid`, `ovid`, `uploadURL`, `cancelURL`, `statusURL`, `complate`, `commands`, `preset`, `status` FROM `tasks` WHERE `status`=?"
			);
			s.setInt(1, status);
			ResultSet rs = s.executeQuery();
			while(rs.next()){
				Task tk = new Task(rs);
				tasks.add(tk);
			}
			rs.close();
			s.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		return tasks;
	}
	public static List<Task> getByVideoIdStatus(long videoId, int status){
		List<Task> tasks = new ArrayList<Task>();
		try{
			PreparedStatement s = SynloadFramework.sql.prepareStatement(
				"SELECT `id`, `uid`, `vid`, `sid`, `ovid`, `uploadURL`, `cancelURL`, `statusURL`, `complate`, `commands`, `preset`, `status` FROM `tasks` WHERE `vid`=? AND `status`=?"
			);
			s.setLong(1, videoId);
			s.setInt(2, status);
			ResultSet rs = s.executeQuery();
			while(rs.next()){
				Task tk = new Task(rs);
				tasks.add(tk);
			}
			rs.close();
			s.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		return tasks;
	}
	public static List<Task> getNew(){
		List<Task> tasks = new ArrayList<Task>();
		try{
			PreparedStatement s = SynloadFramework.sql.prepareStatement(
				"SELECT `id`, `uid`, `vid`, `sid`, `ovid`, `uploadURL`, `cancelURL`, `statusURL`, `complate`, `commands`, `preset`, `status` FROM `tasks` WHERE `status`='3'"
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
		}
		return tasks;
	}
	public static Task getById(long tid){
		try{
			PreparedStatement s = SynloadFramework.sql.prepareStatement(
				"SELECT `id`, `uid`, `vid`, `sid`, `ovid`, `uploadURL`, `cancelURL`, `statusURL`, `complate`, `commands`, `preset`, `status` FROM `tasks` WHERE `id`=?"
			);
			s.setLong(1, tid);
			ResultSet rs = s.executeQuery();
			while(rs.next()){
				Task tk = new Task(rs);
				rs.close();
				s.close();
				return tk;
			}
			rs.close();
			s.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	public static List<Task> getTasksComplete(){
		return Task.getByStatus(0);
	}
	public static List<Task> getTasksWaitlist(){
		return Task.getByStatus(3);
	}
	public static List<Task> getTasksConverting(){
		return Task.getByStatus(2);
	}
	public static List<Task> getTasksConvertComplete(){
		return Task.getByStatus(1);
	}
	public static Task getLatest(){
		try{
			PreparedStatement s = SynloadFramework.sql.prepareStatement(
				"SELECT `id`, `uid`, `vid`, `sid`, `ovid`, `uploadURL`, `cancelURL`, `statusURL`, `complate`, `commands`, `preset`, `status` FROM `tasks` WHERE `status`='3'"
			);
			ResultSet rs = s.executeQuery();
			while(rs.next()){
				Task tk = new Task(rs);
				rs.close();
				s.close();
				s = SynloadFramework.sql.prepareStatement(
					"UPDATE `tasks` SET `status`='2' WHERE `status`='3' AND `id`=?"
				);
				s.setLong(1, tk.id);
				s.executeQuery().close();
				s.close();
				return tk;
			}
			rs.close();
			s.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
}
