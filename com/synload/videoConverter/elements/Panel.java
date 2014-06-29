package com.synload.videoConverter.elements;

import java.util.List;

import com.synload.framework.handlers.Request;
import com.synload.framework.handlers.Response;

public class Panel extends Response {
	public Panel(List<String> templateCache){
		this.setTemplateId("pnl");
		if(!templateCache.contains(this.getTemplateId())){
			this.setTemplate(this.getTemplate("./elements/vc/panel.html"));
		}
		this.setAction("alone");
		this.setPageId("panel");
		this.setParent(".content[page='wrapper']");
		this.setParentTemplate("wrapper");
		this.setRequest(new Request("get","panel"));
		this.setPageTitle(" .::. My Panel");
	}
}
