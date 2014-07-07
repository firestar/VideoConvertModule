package com.synload.videoConverter.converter;

import java.sql.PreparedStatement;
import java.util.ArrayList;

import com.synload.framework.SynloadFramework;
import com.synload.framework.users.User;
import com.synload.videoConverter.converter.models.Video;


public class VideoModel {
	public String sourceFile,video,videofile,data,mData,fps,id;
	public User account= null;
	public Long sourceSize = (long) 0;
	public Float duration = (float) 0;
	
	public Boolean h264 = false;
	public VideoModel(Video v){
		video = v.video;
		data = v.data;
		mData = v.mData;
		fps = v.fps;
		id = v.id;
		sourceSize = v.sourceSize;
		account = v.account;
		duration = v.duration;
	}
}
