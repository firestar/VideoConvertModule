package com.synload.videoConverter.elements;

import java.util.List;

import com.synload.framework.handlers.Request;
import com.synload.framework.handlers.Response;

public class Upload extends Response {
	public Upload(List<String> templateCache){
		this.setTemplateId("upld");
		if(!templateCache.contains(this.getTemplateId())){
			this.setTemplate(this.getTemplate("./elements/vc/upload.html"));
		}
		this.setAction("alone");
		this.setParent("#actionId");
		this.setParentTemplate("panel");
		this.setPageId("upload");
		this.setRequest(new Request("get","upload"));
	}
}
