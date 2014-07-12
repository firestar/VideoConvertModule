package com.synload.videoConverter.listener;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.synload.framework.handlers.Request;
import com.synload.framework.ws.WSHandler;
import com.synload.videoConverter.elements.CreateTask;
import com.synload.videoConverter.elements.CreateTaskPost;
import com.synload.videoConverter.elements.History;
import com.synload.videoConverter.elements.Panel;
import com.synload.videoConverter.elements.Queue;
import com.synload.videoConverter.elements.Status;
import com.synload.videoConverter.elements.Upload;
import com.synload.videoConverter.elements.VideoE;
import com.synload.videoConverter.elements.Videos;

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
	public void getVideos(WSHandler user, Request request) throws JsonProcessingException, IOException{
		user.send(
			user.ow.writeValueAsString(
				new Videos(
					user, 
					request.getTemplateCache(), 
					Integer.parseInt(request.getData().get("videosPage")),
					request.getData().get("listType")
				)
			)
		);
	}
	public void getVideo(WSHandler user, Request request) throws JsonProcessingException, IOException{
		user.send(
			user.ow.writeValueAsString(
				new VideoE(
					user, 
					request.getTemplateCache(), 
					request.getData().get("videoId")
				)
			)
		);
	}
	public void createTask(WSHandler user, Request request) throws JsonProcessingException, IOException{
		user.send(
			user.ow.writeValueAsString(
				new CreateTaskPost(
					user,
					request.getData().get("videoId"),
					request.getData().get("preset"),
					request.getData().get("subtitle"),
					request.getData().get("status"),
					request.getData().get("cancel"),
					request.getData().get("upload")
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
	public void getCreateTask(WSHandler user, Request request) throws JsonProcessingException, IOException{
		user.send(
			user.ow.writeValueAsString(
				new CreateTask(
					user, 
					request.getTemplateCache(), 
					request.getData().get("videoId")
				)
			)
		);
	}
	public void getUpload(WSHandler user, Request request) throws JsonProcessingException, IOException{
		user.send(user.ow.writeValueAsString(new Upload( request.getTemplateCache())));
	}
}
