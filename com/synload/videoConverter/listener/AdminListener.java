package com.synload.videoConverter.listener;

import java.io.IOException;
import java.util.Arrays;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.Request;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.synload.framework.users.Authentication;
import com.synload.framework.users.User;
import com.synload.videoConverter.Users;
import com.synload.videoConverter.converter.models.Video;

public class AdminListener {
	public void sendAdmin(String target, Request baseRequest, HttpServletRequest request, 
			HttpServletResponse response, String[] uRI) throws IOException{
		response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        baseRequest.setHandled(true);
        if(!baseRequest.getParameterMap().containsKey("user")){
        	response.getWriter().println("{\"error\":\"no user specified\"}");
        	return;
		}
        User myaccount = Authentication.login(request.getParameter("admin_user"), request.getParameter("admin_password"));
		if(myaccount.isAdmin()){
			if(uRI.length>=3){
				if(uRI[2].equalsIgnoreCase("createAccount")){
					Users.addAccount(
						request.getParameter("user"), 
						request.getParameter("password"), 
						request.getParameter("email"),
						Arrays.asList(request.getParameter("flags").split(",")),
						Integer.valueOf(request.getParameter("maxQueue")), 
						Integer.valueOf(request.getParameter("maxFileSize"))
					);
				}else if(uRI[2].equalsIgnoreCase("changePassword")){
					User account = Authentication.login(request.getParameter("user"), request.getParameter("password"));
					account.setPassword(request.getParameter("new_password"));
				}else if(uRI[2].equalsIgnoreCase("changeLimits")){
					Users.changeAccount(
						request.getParameter("user"),
						Integer.valueOf(request.getParameter("maxQueue")), 
						Integer.valueOf(request.getParameter("maxFileSize"))
					);
				}else if(uRI[2].equalsIgnoreCase("accounts")){
					ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
					String json = ow.writeValueAsString(User.all());
					response.getWriter().println(json);
				}
			}else{
				response.getWriter().println("{\"error\":\"none\"}");
			}
		}else{
			response.getWriter().println("{\"error\":\"auth failure\"}");
		}
	}
	public void sendDelete(String target, Request baseRequest, HttpServletRequest request, 
			HttpServletResponse response, String[] uRI) throws IOException{
		response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        baseRequest.setHandled(true);
        if(!baseRequest.getParameterMap().containsKey("user")){
        	response.getWriter().println("{\"error\":\"no user specified\"}");
        	return;
		}
		User account = Authentication.login(request.getParameter("admin_user"), request.getParameter("admin_password"));
		if(account!=null){
			Video v = this.getVideoById(request.getParameter("id"));
			if(v!=null){
				if(v.getAccount().getId() == account.getId() || account.isAdmin()){
					/*for(Video vT : Converter.queue){
						if(v.getId().equals(vT.getId())){
							Converter.queue.remove(vT);
							v.delete();
						}
					}*/
					response.getWriter().println("{\"success\":\"deleted\"}");
				}else{
					response.getWriter().println("{\"error\":\"not authorized\"}");
				}
			}else{
				response.getWriter().println("{\"error\":\"video not found\"}");
			}
		}else{
			response.getWriter().println("{\"error\":\"auth failure\"}");
		}
	}
	public Video getVideoById(String r){
		/*List<Video> queueTemp = new ArrayList<Video>(Converter.queue);
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
		}*/
		return null;
	}
}