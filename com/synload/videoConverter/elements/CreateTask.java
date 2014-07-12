package com.synload.videoConverter.elements;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.synload.framework.handlers.Request;
import com.synload.framework.handlers.Response;
import com.synload.framework.ws.WSHandler;
import com.synload.videoConverter.VideoConvertModule;
import com.synload.videoConverter.converter.models.Subtitle;
import com.synload.videoConverter.converter.models.Video;

public class CreateTask extends Response {
	public Video video = null;
	public List<Subtitle> subtitles = new ArrayList<Subtitle>();
	public List<String> presets = new ArrayList<String>();
	public CreateTask(WSHandler user, List<String> templateCache, String videoId){
		this.setTemplateId("createtask");
		if(!templateCache.contains(this.getTemplateId())){
			this.setTemplate(this.getTemplate("./elements/vc/createtask.html"));
		}
		if(user.getUser()!=null){
			this.video = Video.getById(videoId);
			this.subtitles = this.video.getSubtitles();
		}
		this.setParent("#actionId");
		this.setParentTemplate("panel");
		this.setAction("alone");
		this.setPageId("createtask");
		Request r = new Request("get","createtask");
			r.data.put("videoId", videoId);
		presets.add("vp8_4k");
		presets.add("vp8_480");
		presets.add("vp8_default");
		File folder = new File(VideoConvertModule.prop.getProperty("customPath"));
		File[] listOfFiles = folder.listFiles();
		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				presets.add(listOfFiles[i].getName());
			}
		}
		this.setRequest(r);
		this.setPageTitle(" .::. Convert Video");
	}
}
