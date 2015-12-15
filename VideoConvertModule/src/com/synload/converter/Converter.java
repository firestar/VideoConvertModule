package com.synload.converter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ConcurrentNavigableMap;
import org.mapdb20.DB;
import org.mapdb20.DBMaker;
import com.synload.converter.models.ConvertFile;
import com.synload.converter.models.FileData;
import com.synload.converter.thread.ConverterThread;
import com.synload.framework.Log;
import com.synload.framework.SynloadFramework;
import com.synload.framework.modules.ModuleClass;
import com.synload.framework.modules.ModuleLoader;
import com.synload.framework.modules.ModuleRegistry;
import com.synload.framework.modules.annotations.Event;
import com.synload.framework.modules.annotations.Module;
import com.synload.framework.modules.annotations.Event.Type;
import com.synload.talksystem.Client;
import com.synload.talksystem.commands.CommandDocument;
import com.synload.talksystem.commands.ServerTalkCommandEvent;
import com.synload.talksystem.filetransfer.FileControl;
import com.synload.talksystem.info.InformationDocument;

import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;

@Module(name="Converter")
public class Converter extends ModuleClass {
	private DB db = null;
	private ConcurrentNavigableMap<UUID,FileData> fileStorage = null;
	private static Properties prop = new Properties();
	private String key = "";
	private String ffmpeg = "ffmpeg";
	public static String videoPath = "videoStorage/";
	@Override
	public void initialize() {
		if(new File(SynloadFramework.configPath+"videoConvert.conf").exists()){
			try {
				prop.load(new FileInputStream(new File(SynloadFramework.configPath+"videoConvert.conf")));
			} catch (IOException e) {
				e.printStackTrace();
			}
			videoPath = prop.getProperty("videoPath");
			ffmpeg = prop.getProperty("ffmpeg");
			key = prop.getProperty("encryptPassword");
		}else{
			prop.setProperty("ffmpeg", ffmpeg);
			key = SynloadFramework.randomString(10);
			prop.setProperty("encryptPassword", key);
			prop.setProperty("videoPath", videoPath);
			try {
				prop.store(new FileOutputStream(new File(SynloadFramework.configPath+"videoConvert.conf")), "Generated Configuration");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		SynloadFramework.createFolder(videoPath);
		
		db = DBMaker.fileDB(new File(SynloadFramework.dbPath+"convertdb"))
				.closeOnJvmShutdown()
				.encryptionEnable(key)
				.serializerClassLoader(new ModuleLoader(Thread.currentThread().getContextClassLoader()))
				.make();
		
		fileStorage = this.getDB().treeMap("files");
		
		new Thread(
			new ConverterThread()
		).start();
	}
	@Event(name="command execution", description="command execution", type=Type.OTHER)
	public void commandExec(ServerTalkCommandEvent e){
		System.out.println("Command Received: "+e.getCommandDocument().getCommand());
		if(e.getCommandDocument().getCommand().equalsIgnoreCase("convert")){
			Converter cServer = (Converter) ModuleRegistry.get("Converter");
			if(cServer!=null){
				String file = e.getCommandDocument().getChain().toString();
				UUID uid = UUID.fromString(e.getCommandDocument().getArgs()[1]);
				if(cServer.getFileStorage().containsKey(uid)){
					FileData fd = cServer.getFileStorage().get(uid);
					ConvertFile cF = new ConvertFile( 
						cServer.getVideoPath()+file, 
						e.getCommandDocument().getArgs()[0], 
						e.getCommandDocument().getChain(),
						uid
					);
					InformationDocument iD = new InformationDocument("c_task", cF.getVideoUUID());
					iD.getObjects().put("format", e.getCommandDocument().getArgs()[0]);
					try {
						e.getClient().write(iD);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					ConverterThread.queue.add(cF);
				}
			}
		}else if(e.getCommandDocument().getCommand().equalsIgnoreCase("transfer")){
			Converter cServer = (Converter) ModuleRegistry.get("Converter");
			if(cServer!=null){
				FileControl fc = new FileControl();
				if(e.getCommandDocument().getArgs()[0].equalsIgnoreCase("subtitle")){
					UUID uuid = UUID.fromString(e.getCommandDocument().getArgs()[1]);
					Log.debug("Sending "+uuid.toString()+" subtitle", this.getClass());
					if(cServer.getFileStorage().containsKey(uuid)){
						FileData fd = cServer.getFileStorage().get(uuid);
						try {
							Log.debug("Sending "+fd.getFileName()+".ass", this.getClass());
							fc.sendFile(
								e.getClient(), 
								new File(fd.getFileName()+".ass"), 
								uuid
							);
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
				}else{
					UUID uuid = UUID.fromString(e.getCommandDocument().getArgs()[0]);
					if(cServer.getFileStorage().containsKey(uuid)){
						FileData fd = cServer.getFileStorage().get(uuid);
						try {
							fc.sendFile(
								e.getClient(), 
								new File(fd.getFileName()), 
								uuid
							);
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
				}
			}
		}else if(e.getCommandDocument().getCommand().equalsIgnoreCase("info")){
			Converter cServer = (Converter) ModuleRegistry.get("Converter");
			if(cServer!=null){
				UUID uuid = UUID.fromString(e.getCommandDocument().getArgs()[0]);
				if(cServer.getFileStorage().containsKey(uuid)){
					FileData fd = cServer.getFileStorage().get(uuid);
					FFprobe ffprobe = new FFprobe("ffprobe");
					try {
						FFmpegProbeResult result = ffprobe.probe(fd.getFileName());
						InformationDocument iD = new InformationDocument(
							"c_probe",
							e.getCommandDocument().getChain()
						);
						iD.getObjects().put("probe", result);
						iD.getObjects().put("uuid", fd.getId());
						iD.getObjects().put("size", new File(fd.getFileName()).length());
						e.getClient().write(iD);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		}
	}
	@Override
	public void crossTalk(Object... obj){}

	public DB getDB(){
		return db;
	}
	public void storeFile(UUID uuid, FileData fd){
		fileStorage.put(uuid, fd);
		db.commit();
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getFfmpeg() {
		return ffmpeg;
	}
	public void setFfmpeg(String ffmpeg) {
		this.ffmpeg = ffmpeg;
	}
	public String getVideoPath() {
		return videoPath;
	}
	public void setVideoPath(String videoPath) {
		this.videoPath = videoPath;
	}
	public DB getDb() {
		return db;
	}
	public void setDb(DB db) {
		this.db = db;
	}
	public ConcurrentNavigableMap<UUID, FileData> getFileStorage() {
		return fileStorage;
	}
	public void setFileStorage(ConcurrentNavigableMap<UUID, FileData> fileStorage) {
		this.fileStorage = fileStorage;
	}
	public static Properties getProp() {
		return prop;
	}
	public static void setProp(Properties prop) {
		Converter.prop = prop;
	}
}
