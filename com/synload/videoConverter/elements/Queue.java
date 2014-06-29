package com.synload.videoConverter.elements;

import java.util.ArrayList;
import java.util.List;

import com.synload.framework.handlers.Request;
import com.synload.framework.handlers.Response;
import com.synload.framework.ws.WSHandler;
import com.synload.videoConverter.converter.Converter;
import com.synload.videoConverter.converter.Video;
import com.synload.videoConverter.converter.VideoModel;

public class Queue extends Response {
	public List<Object> queue = new ArrayList<Object>();
	public int limit = 25;
	public int queuePage;
	public int queueTotal = 0;
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
			
			for( Video v: Converter.queue ){
				if(v.getAccount().getId()==user.getUser().getId()){
					queue.add(new VideoModel(v));
				}else{
					queue.add(v.getId());
				}
			}
			queueTotal = queue.size();
			if(queue.size()>0){
				if(queue.size()>(limit*queuePage)+limit && queue.size()>(limit*queuePage)){
					queue = queue.subList( (int) (limit*queuePage), (int)(limit*queuePage)+limit );
				}else if(queue.size()<(limit*queuePage)+limit && queue.size()>(limit*queuePage)){
					queue = queue.subList( (int) (limit*queuePage), queue.size() );
				}else{
					queue = new ArrayList<Object>();
				}
			}
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
