package com.synload.videoConverter.elements;

import java.util.ArrayList;
import java.util.List;

import com.synload.framework.handlers.Request;
import com.synload.framework.handlers.Response;
import com.synload.framework.ws.WSHandler;
import com.synload.videoConverter.converter.models.Video;

public class Videos extends Response {
	public List<Video> videos = new ArrayList<Video>();
	public int limit = 25;
	public int videosPage = 0;
	public long videosTotal = 0;
	public String listType = "";
	public Videos(WSHandler user, List<String> templateCache, Integer videosPage, String listType){
		this.listType = listType;
		this.setTemplateId("vid");
		if(!templateCache.contains(this.getTemplateId())){
			this.setTemplate(this.getTemplate("./elements/vc/videos.html"));
		}
		if(user.getUser()!=null){
			
			
			
			this.videosPage = videosPage;
			videosPage -= 1;
			videos = Video.getAll(videosPage, user.getUser().getId());
			videosTotal = Video.length(user.getUser().getId());
		}
		this.setParent("#actionId");
		this.setParentTemplate("panel");
		this.setAction("alone");
		this.setPageId("videos");
		Request r = new Request("get","videos");
			r.data.put("videosPage", String.valueOf(this.videosPage));
			r.data.put("listType", this.listType);
		this.setRequest(r);
		this.setPageTitle(" .::. Videos");
	}
}
