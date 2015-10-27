package com.synload.dev;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.mapdb20.DB;
import org.mapdb20.DBMaker;

import com.synload.framework.Log;
import com.synload.framework.SynloadFramework;
import com.synload.framework.modules.ModuleClass;
import com.synload.framework.modules.ModuleLoader;
import com.synload.framework.modules.annotations.Event;
import com.synload.framework.modules.annotations.Module;
import com.synload.framework.modules.annotations.Event.Type;
import com.synload.talksystem.Client;
import com.synload.talksystem.commands.CommandDocument;
import com.synload.talksystem.commands.ServerTalkCommandEvent;
import com.synload.talksystem.filetransfer.FileControl;
import com.synload.talksystem.filetransfer.FileReceiveEvent;

@Module(name="DevModule", author="Nathaniel Davidson")
public class DevModule extends ModuleClass {
	public static Client c=null;
	private DB db; 
	@Override
	public void initialize() {
		db = DBMaker.fileDB(new File(SynloadFramework.dbPath+"convertdb"))
		.closeOnJvmShutdown()
		.encryptionEnable("defaultPass")
		.serializerClassLoader(new ModuleLoader(Thread.currentThread().getContextClassLoader()))
		.make();
		try {
			c = Client.createConnection("192.168.137.106", 8081, false, "17lkajfajiosa234u1oi");
			(new Thread(c)).start();
			FileControl fsend = new FileControl();
			fsend.sendFile(c, new File("vid.mkv"), UUID.randomUUID());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void crossTalk(Object... obj) {
		
	}
	@Event(name="command execution", description="command execution", type=Type.OTHER)
	public void commandExec(ServerTalkCommandEvent e){
		if(e.getCommandDocument().getCommand().equalsIgnoreCase("fileReceived")){
			try {
				e.client.write(
					new CommandDocument(
						"convert",
						new String[]{
							"h264_480",
							e.getCommandDocument().getArgs()[0]
						}
					)
				);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

}
