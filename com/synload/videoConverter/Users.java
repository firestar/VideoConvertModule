package com.synload.videoConverter;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.synload.framework.SynloadFramework;
import com.synload.framework.users.Authentication;
import com.synload.framework.users.User;

@JsonTypeInfo(
		use = JsonTypeInfo.Id.NAME,
	    include = JsonTypeInfo.As.PROPERTY,
	    property = "class"
	)
public class Users {
	public static HashMap<String, HashMap<String, Integer>> limits = new HashMap<String,HashMap<String,Integer>>();
	public static void addAccount(String user, String password, String email, List<String> flags, int maxQueue, int maxFileSize){
		User account = User.findUser(user.toLowerCase());
		if(account==null){
			Authentication.create( user.toLowerCase(), password, email, flags);
			HashMap<String,Integer> tmp = new HashMap<String,Integer>();
			tmp.put("queue", maxQueue);
			tmp.put("filesize", maxFileSize);
			limits.put( user.toLowerCase(), tmp);
			store();
		}
	}
	public static void changePassword(String user, String password, String newPassword){
		User account = User.findUser(user.toLowerCase());
		if(account.passwordMatch(password)){
			account.setPassword(newPassword);
		}
	}
	public static void changeAccount(String user, int maxQueue, int maxFileSize){
		User account = User.findUser(user.toLowerCase());
		if(account!=null){
			HashMap<String,Integer> tmp = limits.get(account.getUsername().toLowerCase());
			tmp.put("queue", maxQueue);
			tmp.put("filesize", maxFileSize);
			limits.put(account.getUsername().toLowerCase(), tmp);
			store();
		}
	}
	@JsonIgnore 
	private static void store(){
		ObjectMapper mapper = new ObjectMapper();
		try {
			mapper.writeValue(new File("limits.json"), limits);
		} catch (IOException e) {
			if(SynloadFramework.debug){
				e.printStackTrace();
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	@JsonIgnore 
	public static void load(){
		ObjectMapper mapper = new ObjectMapper();
		try {
			limits = mapper.readValue(new File("limits.json"), HashMap.class);
		} catch (IOException e) {
			if(SynloadFramework.debug){
				e.printStackTrace();
			}
		}
	}
	public static HashMap<String, HashMap<String, Integer>> getLimits() {
		return limits;
	}
	public static void setLimits(HashMap<String, HashMap<String, Integer>> ls) {
		limits = ls;
	}
}
