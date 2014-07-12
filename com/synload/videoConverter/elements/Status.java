package com.synload.videoConverter.elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.synload.framework.SynloadFramework;
import com.synload.framework.handlers.Response;
import com.synload.framework.ws.WSHandler;
import com.synload.videoConverter.VideoConvertModule;
import com.synload.videoConverter.converter.Converter;
import com.synload.videoConverter.converter.models.Task;
import com.synload.videoConverter.converter.models.Video;

public class Status extends Response {
	public int history = 0;
	public int uploadQueue = 0;
	public int queue = 0;
	public int httpThreads,httpIdleThreads = 0;
	public Integer convertThreads, uploadThreads = 0;
	public List<HashMap<String, Object>> currents = new ArrayList<HashMap<String, Object>>();
	public Status(WSHandler user, String status, List<String> templateCache){
		if(status!=null){
			this.setCallEvent("status");
		}else{
			this.setTemplateId("stts");
			if(!templateCache.contains(this.getTemplateId())){
				this.setTemplate(this.getTemplate("./elements/vc/status.html"));
			}
			this.setForceParent(false);
			this.setAction("alone");
			this.setParent("#statusId");
		}
		this.convertThreads = VideoConvertModule.workers.size();
		this.uploadThreads = VideoConvertModule.uploaders.size();
		
		history = Task.getByStatusLength(0);
		uploadQueue = Task.getByStatusLength(1);
		queue = Task.getByStatusLength(3);
		
		httpThreads = SynloadFramework.server.getThreadPool().getThreads();
		httpIdleThreads = SynloadFramework.server.getThreadPool().getIdleThreads();
		
		for(Converter c: VideoConvertModule.workers){
			if(c.current.size()>0){
				HashMap<String, Object> current = new HashMap<String, Object>();
				current.put("percent", c.current.get("percent"));
				current.put("timeLeft", c.current.get("timeLeft"));
				current.put("frames", c.current.get("frames"));
				current.put("fps", c.current.get("fps"));
				current.put("size", ((Video)c.current.get("video")).getSourceSize());
				current.put("id", ((Video)c.current.get("video")).getId());
				current.put("username", ((Video)c.current.get("video")).getAccount().getUsername());
				if(((Video)c.current.get("video")).getAccount().getId() == user.getUser().getId()){
					current.put("sourcesize", ((Video)c.current.get("video")).getSourceSize());
				}
				currents.add(current);
			}
		}
	}
}
