package com.synload.videoConverter.elements;

import java.util.ArrayList;
import java.util.List;

import com.synload.framework.handlers.Request;
import com.synload.framework.handlers.Response;
import com.synload.framework.ws.WSHandler;
import com.synload.videoConverter.converter.Converter;
import com.synload.videoConverter.converter.VideoModel;
import com.synload.videoConverter.converter.models.Task;
import com.synload.videoConverter.converter.models.Video;

public class Queue extends Response {
	public List<Task> queue = new ArrayList<Task>();
	public int limit = 25;
	public int queuePage;
	public long queueTotal = 0;
	public String listType = "";
	public Queue(WSHandler user, List<String> templateCache, Integer queuePage, String listType){
		this.listType = listType;
		this.setTemplateId("que");
		if(!templateCache.contains(this.getTemplateId())){
			this.setTemplate(this.getTemplate("./elements/vc/queue.html"));
		}
		if(user.getUser()!=null){
			
			this.queuePage = queuePage;
			queuePage -= 1;
			
			queue = Task.getByUserAndStatus(3, user.getUser().getId(), queuePage);
			queueTotal = Task.lengthByUserAndStatus( 3, user.getUser().getId());
		}
		this.setParent("#actionId");
		this.setParentTemplate("panel");
		this.setAction("alone");
		this.setPageId("queue");
		Request r = new Request("get","queue");
			r.data.put("queuePage", String.valueOf(this.queuePage));
			r.data.put("listType", this.listType);
		this.setRequest(r);
		this.setPageTitle(" .::. Conversion Queue");
	}
}
