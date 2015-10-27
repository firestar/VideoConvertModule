package com.synload.converter.types;

import com.synload.converter.controllers.ConvertControl;
import com.synload.converter.controllers.TransferControl;
import com.synload.talksystem.Client;
import com.synload.talksystem.ConnectionDocument;
import com.synload.talksystem.ConnectionType;

public class FileTransfer extends ConnectionType {
	public <T> FileTransfer(){
		this.clazz = ConvertControl.class;
		try {
			this.func = TransferControl.class.getDeclaredMethod("transferFile", Client.class, ConnectionDocument.class);
		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
		this.setName("queryServer");
	}
}
