package com.synload.videoConverter.elements;

import java.util.ArrayList;
import java.util.List;

import com.synload.framework.handlers.Request;
import com.synload.framework.handlers.Response;
import com.synload.framework.ws.WSHandler;
import com.synload.videoConverter.converter.models.Task;
import com.synload.videoConverter.converter.models.Video;

public class VideoE extends Response {
	public Video video = null;
	public List<Task> tasks = new ArrayList<Task>(); 
	public VideoE(WSHandler user, List<String> templateCache, String videoId){
		this.setTemplateId("vidp");
		if(!templateCache.contains(this.getTemplateId())){
			this.setTemplate(this.getTemplate("./elements/vc/video.html"));
		}
		if(user.getUser()!=null){
			this.video = Video.getById(videoId);
			this.tasks = this.video.getTasks();
		}
		this.setParent("#actionId");
		this.setParentTemplate("panel");
		this.setAction("alone");
		this.setPageId("video");
		Request r = new Request("get","video");
			r.data.put("videoId", videoId);
		this.setRequest(r);
		this.setPageTitle(" .::. Video");
	}
}
