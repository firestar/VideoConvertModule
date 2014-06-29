package com.synload.videoConverter.elements;

import com.synload.framework.handlers.Request;
import com.synload.framework.handlers.Response;

public class Update extends Response {
	public String update="";
	public Request page = null;
	public Update(String update, Request page){
		this.update = update;
		this.page = page;
	}
}
