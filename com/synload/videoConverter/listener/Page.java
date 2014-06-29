package com.synload.videoConverter.listener;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.synload.framework.handlers.Request;
import com.synload.framework.ws.WSHandler;
import com.synload.videoConverter.elements.History;
import com.synload.videoConverter.elements.Panel;
import com.synload.videoConverter.elements.Queue;
import com.synload.videoConverter.elements.Status;
import com.synload.videoConverter.elements.Upload;

public class Page {
	public void getPanel(WSHandler user, Request request) throws JsonProcessingException, IOException{
		user.send(user.ow.writeValueAsString(new Panel(request.getTemplateCache())));
	}
	public void getStatus(WSHandler user, Request request) throws JsonProcessingException, IOException{
		user.send(user.ow.writeValueAsString(new Status(user, request.getData().get("status"), request.getTemplateCache())));
	}
	public void getHistory(WSHandler user, Request request) throws JsonProcessingException, IOException{
		user.send(
			user.ow.writeValueAsString(
				new History(
					user, 
					request.getTemplateCache(), 
					Integer.parseInt(request.getData().get("historyPage")),
					request.getData().get("listType")
				)
			)
		);
	}
	public void getQueue(WSHandler user, Request request) throws JsonProcessingException, IOException{
		user.send(
			user.ow.writeValueAsString(
				new Queue(
					user, 
					request.getTemplateCache(), 
					Integer.parseInt(request.getData().get("queuePage")),
					request.getData().get("listType")
				)
			)
		);
	}
	public void getUpload(WSHandler user, Request request) throws JsonProcessingException, IOException{
		user.send(user.ow.writeValueAsString(new Upload( request.getTemplateCache())));
	}
}
