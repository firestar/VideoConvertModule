package com.synload.videoConverter.listener;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.PatternSyntaxException;

import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.apache.commons.io.IOUtils;
import org.eclipse.jetty.server.Request;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.synload.eventsystem.EventHandler;
import com.synload.framework.SynloadFramework;
import com.synload.framework.http.HTTPRouting;
import com.synload.framework.users.Authentication;
import com.synload.framework.users.User;
import com.synload.videoConverter.SynloadConverter;
import com.synload.videoConverter.Users;
import com.synload.videoConverter.VideoConvertModule;
import com.synload.videoConverter.converter.Converter;
import com.synload.videoConverter.converter.models.Video;

public class HTTPListener {
	private static final MultipartConfigElement MULTI_PART_CONFIG = 
			new MultipartConfigElement(
				"/tmp/", 
				943718400, 
				948718400, 
				948718400
			);
	@EventHandler
	public void sendUpload(String target, Request baseRequest, HttpServletRequest request, 
			HttpServletResponse response, String[] uRI) throws IOException, ServletException{
		request.setAttribute(Request.__MULTIPART_CONFIG_ELEMENT, MULTI_PART_CONFIG);
		response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        //request.setStopTimeout(600000);
        baseRequest.setHandled(true);
        if(!baseRequest.getParameterMap().containsKey("user")){
        	response.getWriter().println("{\"e\":\"no user specified\"}");
        	return;
		}
        ArrayList<String> ignoreKeys = new ArrayList<String>();
        ignoreKeys.add("user");
        ignoreKeys.add("password");
        try{
        	User account = User.findUser(request.getParameter("user").toLowerCase());
        	if(account!=null){
        		if(account.passwordMatch(request.getParameter("password"))){
        			HashMap<String, Integer> tmp = Users.getLimits().get(account.getUsername().toLowerCase());
					if(tmp.get("queue")>0){
						int i = 0;
						List<Video> queueTemp = new ArrayList<Video>(Converter.queue);
						for(Video v:queueTemp){
							if(v.getAccount().equals(account)){
								i++;
							}
						}
						if(tmp.get("queue")<=i){
							response.getWriter().println("{\"e\":\"Too many videos in queue!\"}");
							for(Part part :request.getParts()){
								part.delete();
							}
							return;
						}
					}
					List<HashMap<String,String>> entries = new ArrayList<HashMap<String,String>>();
					for(Part part :request.getParts()){
						HashMap<String,String> entry = new HashMap<String,String>();
						if(part.getSubmittedFileName()!=null){
							if(part.getSize()>0){
								System.out.println("recieved file!");
								if(tmp.get("filesize")<part.getSize() && tmp.get("filesize")>0){
									response.getWriter().println("{\"e\":\"File size too large for your account!\"}");
									for(Part p :request.getParts()){
										p.delete();
									}
									return;
								}else if(tmp.get("filesize")==0){
									response.getWriter().println("{\"e\":\"File smaller than zero!\"}");
									return;
								}
								HashMap<String, String> params = new HashMap<String, String>();
								for(Entry<String, String[]> s:request.getParameterMap().entrySet()){
									if(!ignoreKeys.contains(s.getKey())){
										params.put(s.getKey(), s.getValue()[0]);
									}
								}
								Video d = new Video (
									part.getSubmittedFileName(),
									part.getSize(),
									part,
									account
								);
								/*if(baseRequest.getParameterMap().containsKey("status"))
									d.setStatusURL(request.getParameter("status"));
								if(baseRequest.getParameterMap().containsKey("cancel"))
									d.setCancelURL(request.getParameter("cancel"));
								if(baseRequest.getParameterMap().containsKey("upload"))
									d.setUploadURL(request.getParameter("upload"));*/
								System.out.println("file building!");
								d.buildVideo();
								entry.put("file", part.getSubmittedFileName());
								entry.put("id", d.getId());
								entries.add(entry);
							}
						}
					}
					ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
					response.getWriter().println("Recieved "+ow.writeValueAsString(entries));
				}else{
					response.getWriter().println("{\"e\":\"Authentication failed!\"}");
					for(Part part :request.getParts()){
						part.delete();
					}
				}
			}else{
				response.getWriter().println("{\"e\":\"Authentication failed!\"}");
				for(Part part :request.getParts()){
					part.delete();
				}
			}
        } catch (NullPointerException e) {
        	if(SynloadFramework.debug){
        		e.printStackTrace();
        	}
        	response.getWriter().println("{\"e\":\"Authentication failed!\"}");
			for(Part part :request.getParts()){
				part.delete();
			}
		}
	}
	public void xmlUpload(String target, Request baseRequest, HttpServletRequest request, 
			HttpServletResponse response, String[] uRI) throws IOException, ServletException{
		request.setAttribute(Request.__MULTIPART_CONFIG_ELEMENT, MULTI_PART_CONFIG);
		response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        baseRequest.setHandled(true);
        if(!baseRequest.getParameterMap().containsKey("user")){
        	response.getWriter().println("{\"e\":\"no user specified\"}");
        	return;
		}
        ArrayList<String> ignoreKeys = new ArrayList<String>();
        ignoreKeys.add("user");
        ignoreKeys.add("password");
        try{
        	User account = User.findUser(request.getParameter("user").toLowerCase());
        	if(account!=null){
        		if(account.getId() == User.findVerifySession(request.getParameter("session")).getId()){
        			HashMap<String, Integer> tmp = Users.getLimits().get(account.getUsername().toLowerCase());
					if(tmp.get("queue")>0){
						int i = 0;
						List<Video> queueTemp = new ArrayList<Video>(Converter.queue);
						for(Video v:queueTemp){
							if(v.getAccount().equals(account)){
								i++;
							}
						}
						if(tmp.get("queue")<=i){
							response.getWriter().println("{\"e\":\"Too many videos in queue!\"}");
							for(Part part :request.getParts()){
								part.delete();
							}
							return;
						}
					}
					List<HashMap<String,String>> entries = new ArrayList<HashMap<String,String>>();
					for(Part part :request.getParts()){
						HashMap<String,String> entry = new HashMap<String,String>();
						if(part.getSubmittedFileName()!=null){
							if(part.getSize()>0){
								System.out.println("recieved file!");
								if(tmp.get("filesize")<part.getSize() && tmp.get("filesize")>0){
									response.getWriter().println("{\"e\":\"File size too large for your account!\"}");
									for(Part p :request.getParts()){
										p.delete();
									}
									return;
								}else if(tmp.get("filesize")==0){
									response.getWriter().println("{\"e\":\"File smaller than zero!\"}");
									return;
								}
								HashMap<String, String> params = new HashMap<String, String>();
								for(Entry<String, String[]> s:request.getParameterMap().entrySet()){
									if(!ignoreKeys.contains(s.getKey())){
										params.put(s.getKey(), s.getValue()[0]);
									}
								}
								Video d = new Video (
									part.getSubmittedFileName(),
									part.getSize(),
									part,
									account
								);
								/*if(baseRequest.getParameterMap().containsKey("status"))
									d.setStatusURL(request.getParameter("status"));
								if(baseRequest.getParameterMap().containsKey("cancel"))
									d.setCancelURL(request.getParameter("cancel"));
								if(baseRequest.getParameterMap().containsKey("upload"))
									d.setUploadURL(request.getParameter("upload"));*/
								System.out.println("file building!");
								d.buildVideo();
								entry.put("file", part.getSubmittedFileName());
								entry.put("id", d.getId());
								entries.add(entry);
							}
						}
					}
					ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
					response.getWriter().println(ow.writeValueAsString(entries));
				}else{
					response.getWriter().println("{\"e\":\"Authentication failed!\"}");
					for(Part part :request.getParts()){
						part.delete();
					}
				}
			}else{
				response.getWriter().println("{\"e\":\"Authentication failed!\"}");
				for(Part part :request.getParts()){
					part.delete();
				}
			}
        } catch (NullPointerException e) {
        	if(SynloadFramework.debug){
        		e.printStackTrace();
        	}
        	response.getWriter().println("{\"e\":\"Authentication failed!\"}");
			for(Part part :request.getParts()){
				part.delete();
			}
		}
	}
	public void sendMedia(String target, Request baseRequest, HttpServletRequest request, 
			HttpServletResponse response, String[] uRI) throws IOException{
		if(uRI.length>=4){
			User account = User.findVerifySession(uRI[3]);
			if(account!=null){
		        Video v = this.getVideoById(uRI[2]);
		        if(v.getAccount().getId()==account.getId()){
		        	HTTPRouting.openFile( v.getSourceFile(), response, baseRequest);
		        }
			}
		}
	}
	public void sendThumbnail(String target, Request baseRequest, HttpServletRequest request, 
			HttpServletResponse response, String[] uRI) throws IOException{
		if(uRI.length>=4){
			User account = User.findVerifySession(uRI[3]);
	        Video v = this.getVideoById(uRI[2]);
	        if(v!=null){
	        	if(v.getAccount().getId() != account.getId()){
	        		response.setContentType("text/html;charset=utf-8");
	    	        response.setStatus(HttpServletResponse.SC_OK);
	    	        baseRequest.setHandled(true);
	        		response.getWriter().println("{\"e\":\"not authorized\"}");
	        		return;
	        	}
	        	File thumbnail = (new File(VideoConvertModule.prop.getProperty("thumbnailPath")+v.getId()+".png"));
	        	if(!thumbnail.exists()){
		        	BufferedOutputStream thumbnailData = new BufferedOutputStream(new FileOutputStream(thumbnail));
			        Process p;
			        try {
			        	String time = "00:00:10.00";
			        	if(uRI.length>=4){
			        		boolean isInputInteger = false;
			        		if(uRI.length==5){
				        		try {
				        			isInputInteger = uRI[4].matches("(?sim)([0-9]+)");
				        		} catch (PatternSyntaxException ex) {
				        		}
			        		}
			        		if(isInputInteger){
			        			int hours = Integer.valueOf(uRI[3]) / 3600;
			        			int minutes = (Integer.valueOf(uRI[3]) % 3600) / 60;
			        			int seconds = Integer.valueOf(uRI[3]) % 60;
			        			time = hours+":"+minutes+":"+seconds;
			        		}
			        	}
			        	if((new File(VideoConvertModule.prop.getProperty("uploadPath")+v.getSourceFile())).exists()){
			        		p = Runtime.getRuntime().exec(VideoConvertModule.prop.getProperty("ffmpeg")+" -i "+VideoConvertModule.prop.getProperty("uploadPath")+v.getSourceFile()+" -threads 16 -vf scale=320:-1 -ss "+time+" -f image2 -vframes 1 -");
			        	}else{
			        		response.setContentType("text/html;charset=utf-8");
					        response.setStatus(HttpServletResponse.SC_OK);
					        baseRequest.setHandled(true);
					        response.getWriter().println("{\"e\":\"video not found\"}");
			        		return ;
			        	}
			        	p.waitFor();
						response.setContentType("image/png");
						response.setStatus(HttpServletResponse.SC_OK);
				        baseRequest.setHandled(true);
				        byte[] data = IOUtils.toByteArray(p.getInputStream());
				        thumbnailData.write(data);
				        thumbnailData.close();
				        response.getOutputStream().write(data);
				        return;
			        } catch (Exception e) {
			        	if(SynloadFramework.debug){
			        		e.printStackTrace();
			        	}
					}finally{
						thumbnailData.close();
					}
	        	}else{
	        		HTTPRouting.openFile(VideoConvertModule.prop.getProperty("thumbnailPath")+v.getId()+".png" , response, baseRequest);
	        	}
	        }else{
	        	response.setContentType("text/html;charset=utf-8");
		        response.setStatus(HttpServletResponse.SC_OK);
		        baseRequest.setHandled(true);
		        response.getWriter().println("{\"e\":\"video not found\"}");
			}
		}else{
			response.setContentType("text/html;charset=utf-8");
	        response.setStatus(HttpServletResponse.SC_OK);
	        baseRequest.setHandled(true);
	        response.getWriter().println("{\"e\":\"no id specified\"}");
		}
	}
	public void sendUploadQueue(String target, Request baseRequest, HttpServletRequest request, 
			HttpServletResponse response, String[] uRI) throws IOException{
		response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        baseRequest.setHandled(true);
		if(SynloadConverter.uploadQueue.size()==0){
			response.getWriter().println("{\"e\":\"none\"}");
			return;
		}
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		List<String> vlist = new ArrayList<String>();
		for(Video v :SynloadConverter.uploadQueue){
			vlist.add(v.getId());
		}
		String json = ow.writeValueAsString(vlist);
		response.getWriter().println(json);
	}
	public void sendHistory(String target, Request baseRequest, HttpServletRequest request, 
			HttpServletResponse response, String[] uRI) throws IOException{
		response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        baseRequest.setHandled(true);
		if(SynloadConverter.history.size()==0){
			response.getWriter().println("{\"e\":\"none\"}");
			return;
		}
		List<String> vlist = new ArrayList<String>();
		for(Video v :SynloadConverter.history){
			vlist.add(v.getId());
		}
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		String json = ow.writeValueAsString(vlist);
		response.getWriter().println(json);
	}
	public void sendDetailItem(String target, Request baseRequest, HttpServletRequest request, 
			HttpServletResponse response, String[] uRI) throws IOException{
		response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        baseRequest.setHandled(true);
		if(SynloadConverter.history.size()==0){
			response.getWriter().println("{\"e\":\"none\"}");
			return;
		}
		if(uRI.length<4){
			response.getWriter().println("{\"e\":\"improper request\"}");
			return;
		}
		User account = User.findVerifySession(uRI[3]);
		if(account!=null){
			for(Video v:SynloadConverter.history){
				if(v.getId().equals(uRI[2]) && v.getAccount().getId()==account.getId()){
					ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
					String json = ow.writeValueAsString(v);
					response.getWriter().println(json);
					return;
				}
			}
			for(Video v:SynloadConverter.uploadQueue){
				if(v.getId().equals(uRI[2]) && v.getAccount().getId()==account.getId()){
					ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
					String json = ow.writeValueAsString(v);
					response.getWriter().println(json);
					return;
				}
			}
			for(Video v:Converter.queue){
				if(v.getId().equals(uRI[2]) && v.getAccount().getId()==account.getId()){
					ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
					String json = ow.writeValueAsString(v);
					response.getWriter().println(json);
					return;
				}
			}
		}else{
			response.getWriter().println("{\"e\":\"Authentication failed!\"}");
		}
	}
	public void sendConvertQueue(String target, Request baseRequest, HttpServletRequest request, 
			HttpServletResponse response, String[] uRI) throws IOException{
		response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        baseRequest.setHandled(true);
		if(Converter.queue.size()==0){
			response.getWriter().println("{\"e\":\"none\"}");
			return;
		}
		List<String> vlist = new ArrayList<String>();
		for(Video v :Converter.queue){
			vlist.add(v.getId());
		}
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		String json = ow.writeValueAsString(vlist);
		response.getWriter().println(json);
	}
	@SuppressWarnings("unused")
	public void sendStatus(String target, Request baseRequest, HttpServletRequest request, 
			HttpServletResponse response, String[] uRI) throws IOException{
		response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        baseRequest.setHandled(true);
        String json = "";
        List<HashMap<String, Object>> currents = new ArrayList<HashMap<String, Object>>();
        for(Converter c: VideoConvertModule.workers){
			if(c.current.size()!=0){
				HashMap<String, Object> g = new HashMap<String, Object>();
					g.put("percent", c.current.get("percent"));
					g.put("timeLeft", c.current.get("timeLeft"));
					g.put("frames", c.current.get("frames"));
					g.put("fps", c.current.get("fps"));
					g.put("id", ((Video)c.current.get("video")).getId());
					g.put("username", ((Video)c.current.get("video")).getAccount().getUsername());
					//g.put("convertStringSize", ((Video)g.get("video")).getConvertStringSize() );
					//g.put("convertString", ((Video)g.get("video")).getConvertString().subList( 0, ((Video)g.get("video")).getConvertStringSize()-1) );
					currents.add(g);
			}
        }
        if(currents.size()==0){
        	response.getWriter().println("{\"e\":\"none\"}");
        	return;
        }
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        json = ow.writeValueAsString(currents);
		response.getWriter().println(json);
	}
	public void sendStatusMore(String target, Request baseRequest, HttpServletRequest request, 
			HttpServletResponse response, String[] uRI) throws IOException{
		response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        baseRequest.setHandled(true);
		User account = Authentication.login(uRI[3],uRI[4]);
		if(account!=null){
			List<HashMap<String, Object>> currents = new ArrayList<HashMap<String, Object>>();
			for(Converter c: VideoConvertModule.workers){
				HashMap<String, Object> tmp = new HashMap<String, Object>();
				if(c.current.size()>0){
					if(((Video)c.current.get("video")).getAccount().getId()==account.getId()){
						currents.add(c.current);
					}
				}
			}
			if(currents.size()==0){
	        	response.getWriter().println("{\"e\":\"none\"}");
	        	return;
	        }else{
	        	ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
				response.getWriter().println(ow.writeValueAsString(currents));
	        }
		}else{
			response.getWriter().println("{\"e\":\"Authentication failed!\"}");
		}
	}
	
	public void sendFavIcon(String target, Request baseRequest, HttpServletRequest request, 
			HttpServletResponse response, String[] uRI) throws IOException{
		response.setContentType("image/x-icon;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        baseRequest.setHandled(true);
        HTTPRouting.openFile("/resources/favicon.ico", response, baseRequest);
	}
	public Video getVideoById(String r){
		List<Video> queueTemp = new ArrayList<Video>(Converter.queue);
		for( Video s : queueTemp){
        	if(s.getId().equals(r)){
        		return s;
        	}
        }
		queueTemp = new ArrayList<Video>(SynloadConverter.history);
		for( Video s : queueTemp){
        	if(s.getId().equals(r)){
        		return s;
        	}
        }
		for(Converter c: VideoConvertModule.workers){
			if(c.current.containsKey("video")){
				Video d = (Video)c.current.get("video");
				if(d.getId().equals(r)){
	        		return d;
	        	}
			}
		}
		return null;
	}
}