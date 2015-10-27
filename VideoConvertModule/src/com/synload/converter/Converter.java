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
import com.synload.framework.SynloadFramework;
import com.synload.framework.modules.ModuleClass;
import com.synload.framework.modules.ModuleLoader;
import com.synload.framework.modules.ModuleRegistry;
import com.synload.framework.modules.annotations.Event;
import com.synload.framework.modules.annotations.Module;
import com.synload.framework.modules.annotations.Event.Type;
import com.synload.talksystem.commands.CommandDocument;
import com.synload.talksystem.commands.ServerTalkCommandEvent;

@Module(name="Converter")
public class Converter extends ModuleClass {
	private DB db = null;
	private ConcurrentNavigableMap<UUID,FileData> fileStorage = null;
	private static Properties prop = new Properties();
	private String key = "";
	private String ffmpeg = "ffmpeg";
	private String videoPath = "videoStorage/";
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
		if(e.getCommandDocument().getCommand().equalsIgnoreCase("convert")){
			Converter cServer = (Converter) ModuleRegistry.get("Converter");
			if(cServer!=null){
				String file = cServer.getVideoPath()+e.getCommandDocument().getChain().toString()+".vid";
				UUID uid = UUID.fromString(e.getCommandDocument().getArgs()[1]);
				if(cServer.getFileStorage().containsKey(uid)){
					FileData fd = cServer.getFileStorage().get(uid);
					ConvertFile cF = new ConvertFile( fd.getFileName(), file, e.getCommandDocument().getArgs()[0]);
					FileData fdN = new FileData(e.getCommandDocument().getChain(), file);
					cServer.storeFile(e.getCommandDocument().getChain(), fdN);
					ConverterThread.queue.add(cF);
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
