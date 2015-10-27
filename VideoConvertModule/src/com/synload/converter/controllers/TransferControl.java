package com.synload.converter.controllers;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import com.synload.converter.Converter;
import com.synload.converter.models.FileData;
import com.synload.framework.Log;
import com.synload.framework.SynloadFramework;
import com.synload.framework.modules.ModuleRegistry;
import com.synload.framework.modules.annotations.Event;
import com.synload.framework.modules.annotations.Event.Type;
import com.synload.talksystem.commands.CommandDocument;
import com.synload.talksystem.filetransfer.FileReceiveEvent;

public class TransferControl {
	@Event(name="receive File",description="Receive File From The File Bridge",type=Type.OTHER)
	public void receiveFile(FileReceiveEvent e){
		
		Log.info("Received file "+e.getFileName(), TransferControl.class);
		Log.info("File Size: "+(new File(e.getTempFilePath()).length()),TransferControl.class);
		try {
			Log.info(SynloadFramework.getPlugins().toString(), TransferControl.class);
			Converter cServer = (Converter) ModuleRegistry.get("Converter");
			if(cServer!=null){
				
				String storedFile = cServer.getVideoPath()+e.getChain().toString()+".vid";
				new File(storedFile).delete();
				FileUtils.moveFile(new File(e.getTempFilePath()), new File(storedFile));
				FileData file = new FileData(e.getChain(), storedFile);
				cServer.storeFile(e.getChain(), file);
				
				e.getClient().write(new CommandDocument("fileReceived", new String[]{ file.getId().toString() }));
				
			}
			//ConvertFile cF = new ConvertFile(e.getFileName(), e.getFileName()+".out", "h265");
			//ConverterThread.queue.add(cF);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
}