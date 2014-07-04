package com.synload.videoConverter.elements;

import java.util.ArrayList;
import java.util.List;

import com.synload.framework.handlers.Request;
import com.synload.framework.handlers.Response;
import com.synload.framework.ws.WSHandler;
import com.synload.videoConverter.SynloadConverter;
import com.synload.videoConverter.converter.VideoModel;
import com.synload.videoConverter.converter.models.Video;

public class History extends Response {
	public List<VideoModel> history = new ArrayList<VideoModel>();
	public int limit = 25;
	public int historyPage = 0;
	public int historyTotal = 0;
	public String listType = "";
	public History(WSHandler user, List<String> templateCache, Integer historyPage, String listType){
		this.listType = listType;
		this.setTemplateId("hist");
		if(!templateCache.contains(this.getTemplateId())){
			this.setTemplate(this.getTemplate("./elements/vc/history.html"));
		}
		if(user.getUser()!=null){
			
			for( Video v: SynloadConverter.history ){
				if(v.getAccount().getId()==user.getUser().getId()){
					history.add(new VideoModel(v));
				}
			}
			
			this.historyPage = historyPage;
			historyPage -= 1;
			
			historyTotal = history.size();
			if(history.size()>0){
				if(history.size()>(limit*historyPage)+limit && history.size()>(limit*historyPage)){
					history = history.subList( (int) (limit*historyPage), (int)(limit*historyPage)+limit );
				}else if(history.size()<(limit*historyPage)+limit && history.size()>(limit*historyPage)){
					history = history.subList( (int) (limit*historyPage), history.size() );
				}else{
					history = new ArrayList<VideoModel>();
				}
			}
			
		}
		this.setParent("#actionId");
		this.setParentTemplate("panel");
		this.setAction("alone");
		this.setPageId("history");
		Request r = new Request("get","history");
			r.data.put("historyPage", String.valueOf(this.historyPage));
			r.data.put("listType", this.listType);
		this.setRequest(r);
		this.setPageTitle(" .::. History");
	}
}
