package com.synload.videoConverter.elements;

import com.synload.framework.handlers.Response;
import com.synload.framework.ws.WSHandler;
import com.synload.videoConverter.converter.models.Video;

public class CreateTaskPost extends Response {
	public CreateTaskPost(WSHandler user, String videoId, String preset, String subtitle, String status, String cancel, String upload){
		if(user.getUser()!=null){
			Video v = Video.getById(videoId);
			if(v!=null){
				if(user.getUser().getId()==v.getAccount().getId()){
					v.newTask(user.getUser().getId(), Integer.valueOf(subtitle), preset, status, upload, cancel);
				}
			}
		}
	}
}
