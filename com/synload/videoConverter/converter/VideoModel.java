package com.synload.videoConverter.converter;

import java.util.ArrayList;

import com.synload.framework.users.User;


public class VideoModel {
	public String fileName,pathToVideo,video,videofile,uploadURL,data,mData,fps,statusURL,cancelURL,id;
	public User account= null;
	//public List<String> convertString = new ArrayList<String>();
	public Long size,sourceSize = (long) 0;
	public Float duration = (float) 0;
	
	public Boolean h264 = false;
	public ArrayList<String> subs = new ArrayList<String>();
	public VideoModel(Video v){
		fileName = v.fileName;
		pathToVideo = v.pathToVideo;
		video = v.video;
		videofile = v.videofile;
		uploadURL = v.uploadURL;
		data = v.data;
		mData = v.mData;
		fps = v.fps;
		statusURL = v.statusURL;
		cancelURL = v.cancelURL;
		id = v.id;
		size = v.size;
		sourceSize = v.sourceSize;
		account = v.account;
		duration = v.duration;
		h264 = v.h264;
		subs = v.subs;
	}
}
