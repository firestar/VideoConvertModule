package com.synload.videoConverter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import com.synload.eventsystem.Addon;
import com.synload.framework.SynloadFramework;
import com.synload.framework.users.User;
import com.synload.framework.ws.WSRequest;
import com.synload.videoConverter.converter.Converter;
import com.synload.videoConverter.listener.AdminListener;
import com.synload.videoConverter.listener.HTTPListener;
import com.synload.videoConverter.listener.Page;
import com.synload.videoConverter.uploader.Uploader;

public class VideoConvertModule extends Addon {
	public static Properties prop = new Properties();
	public static List<Converter> workers = new ArrayList<Converter>();
	public static List<Uploader> uploaders = new ArrayList<Uploader>();
	
	private static String OS = System.getProperty("os.name").toLowerCase();
	public void init(){
		try {
			startup();
		} catch (IOException e1) {
			if(SynloadFramework.debug){
				e1.printStackTrace();
			}
			return;
		}
		for(int i=0;i<Integer.valueOf(prop.getProperty("converters"));i++){
			Converter startupWorker = new Converter();
			workers.add(startupWorker);
			( startupWorker ).start();
			System.out.println("[VC] Starting Converter "+ i);
		}
		
		for(int i=0;i<Integer.valueOf(prop.getProperty("uploaders"));i++){
			Uploader uploader = new Uploader();
			uploaders.add(uploader);
			(new Thread(uploader)).start();
			System.out.println("[VC] Starting Uploader "+ i);
		}
		
		List<String> flags = new ArrayList<String>();
		flags.add("r");
		
		SynloadFramework.registerHTTPPage("/status", HTTPListener.class, "sendStatus");
		SynloadFramework.registerHTTPPage("(?simx)/status/([a-zA-Z0-9\\ ]+)/(.*?)/", HTTPListener.class, "sendStatusMore");
		SynloadFramework.registerHTTPPage("/favicon.ico", HTTPListener.class, "sendFavIcon");
		SynloadFramework.registerHTTPPage("/upload", HTTPListener.class, "sendUpload");
		SynloadFramework.registerHTTPPage("/xmlupload", HTTPListener.class, "xmlUpload");
		SynloadFramework.registerHTTPPage("/upload_queue", HTTPListener.class, "sendUploadQueue");
		SynloadFramework.registerHTTPPage("(?simx)/thumb/([a-zA-Z0-9]+)/([a-zA-Z0-9-]+)/", HTTPListener.class, "sendThumbnail");
		SynloadFramework.registerHTTPPage("(?im)/play/([a-zA-Z0-9]+)/([a-zA-Z0-9-]+)/", HTTPListener.class, "sendMedia");
		SynloadFramework.registerHTTPPage("/history", HTTPListener.class, "sendHistory");
		SynloadFramework.registerHTTPPage("(?simx)/details/([a-zA-Z0-9]+)/([a-zA-Z0-9-]+)/", HTTPListener.class, "sendDetailItem");
		SynloadFramework.registerHTTPPage("/convert_queue", HTTPListener.class, "sendConvertQueue");
		SynloadFramework.registerHTTPPage("/admin", AdminListener.class, "sendAdmin");
		SynloadFramework.registerHTTPPage("/delete", AdminListener.class, "sendDelete");
		
		SynloadFramework.registerElement(new WSRequest("panel","get"), Page.class, "getPanel", flags);
		SynloadFramework.registerElement(new WSRequest("status","get"), Page.class, "getStatus", flags);
		SynloadFramework.registerElement(new WSRequest("history","get"), Page.class, "getHistory", flags);
		SynloadFramework.registerElement(new WSRequest("videos","get"), Page.class, "getVideos", flags);
		SynloadFramework.registerElement(new WSRequest("video","get"), Page.class, "getVideo", flags);
		SynloadFramework.registerElement(new WSRequest("createtask","get"), Page.class, "getCreateTask", flags);
		SynloadFramework.registerElement(new WSRequest("task","create"), Page.class, "createTask", flags);
		SynloadFramework.registerElement(new WSRequest("queue","get"), Page.class, "getQueue", flags);
		SynloadFramework.registerElement(new WSRequest("upload","get"), Page.class, "getUpload", flags);
		
	}
	public void startup() throws FileNotFoundException, IOException{
		
		if((new File("modules/videoconvert.ini")).exists()){
			prop.load(new FileInputStream("modules/videoconvert.ini"));
		}else{
			if(OS.indexOf("win") >= 0){
				prop.setProperty("os", "Windows");
				prop.setProperty("ffmpeg", "ffmpeg.exe");
				prop.setProperty("avconv", "avconv.exe");
				prop.setProperty("ffprobe", "ffprobe.exe");
				prop.setProperty("mkvextract", "mkvextract.exe");
				prop.setProperty("mkvmerge", "mkvmerge.exe");
			}else{
				prop.setProperty("os", "Linux");
				prop.setProperty("ffmpeg", "ffmpeg");
				prop.setProperty("avconv", "avconv");
				prop.setProperty("ffprobe", "ffprobe");
				prop.setProperty("mkvextract", "mkvextract");
				prop.setProperty("mkvmerge", "mkvmerge");
			}
			prop.setProperty("customPath", "./custom/");
			prop.setProperty("uploadPath", "./uploads/");
			prop.setProperty("multiUser", "1");
			prop.setProperty("converters", "1");
			prop.setProperty("thumbnailPath", "./thumbs/");
			prop.setProperty("uploaders", "1");
			prop.setProperty("videoPath", "./tmp/");
			prop.setProperty("userName", "root");
			prop.setProperty("userPass", "password123");
			prop.setProperty("default", "vp8");
			prop.store(new FileOutputStream("modules/videoconvert.ini"), null);
		}
		checkFolder(prop.getProperty("uploadPath"));
		checkFolder(prop.getProperty("videoPath"));
		checkFolder(prop.getProperty("thumbnailPath"));
		checkFolder(prop.getProperty("customPath"));
		if(User.all().size()==0){
			String password = UUID.randomUUID().toString().split("-")[0];
			Users.addAccount("root", password, "root@localhost", Arrays.asList(new String[]{"r","a"}), 20, 2147483647);
			User account = User.findUser("root");
			if(account!=null){
				account.setAdmin(true);
				System.out.println("[VC] Username: root");
				System.out.println("[VC] Password: \""+password+"\"");
			}else{
				System.out.println("[VC][ERROR] Failed to create default user!");
			}
		}else{
			Users.load();
			System.out.println("[VC] Users found!");
		}
	}
	public static String objectToString(Object obj) throws IOException{
		ByteArrayOutputStream bo = new ByteArrayOutputStream();
		ObjectOutputStream so = new ObjectOutputStream(bo);
		so.writeObject(obj);
		so.flush();
		return bo.toString();
	}
	public static Object stringToObject(String obj) throws IOException, ClassNotFoundException{
		byte b[] = obj.getBytes(); 
		ByteArrayInputStream bi = new ByteArrayInputStream(b);
		ObjectInputStream si = new ObjectInputStream(bi);
		return si.readObject();
	}
	public void verifyExists(String exec) throws Exception{
		if(!(new File(exec)).exists()){
			throw new Exception("[VC] Cannot find the executable "+exec);
		}
	}
	public void checkFolder(String folder){
		if(!(new File(folder)).exists()){
			(new File(folder)).mkdir();
			System.out.println("[VC] created folder \""+folder+"\"");
		}
	}
}
