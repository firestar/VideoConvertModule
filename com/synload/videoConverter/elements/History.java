package com.synload.videoConverter.elements;

import java.util.ArrayList;
import java.util.List;

import com.synload.framework.handlers.Request;
import com.synload.framework.handlers.Response;
import com.synload.framework.ws.WSHandler;
import com.synload.videoConverter.converter.models.Task;

public class History extends Response {
	public List<Task> history = new ArrayList<Task>();
	public int limit = 25;
	public int historyPage = 0;
	public long historyTotal = 0;
	public String listType = "";
	public History(WSHandler user, List<String> templateCache, Integer historyPage, String listType){
		this.listType = listType;
		this.setTemplateId("hist");
		if(!templateCache.contains(this.getTemplateId())){
			this.setTemplate(this.getTemplate("./elements/vc/history.html"));
		}
		if(user.getUser()!=null){
			
			
			
			this.historyPage = historyPage;
			historyPage -= 1;
			history = Task.getByUserAndStatus(0, user.getUser().getId(), historyPage);
			historyTotal = Task.lengthByUserAndStatus( 0, user.getUser().getId());
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
