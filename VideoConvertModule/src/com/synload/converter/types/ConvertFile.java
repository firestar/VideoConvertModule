package com.synload.converter.types;

import java.lang.reflect.Method;

import com.synload.converter.controllers.ConvertControl;
import com.synload.talksystem.Client;
import com.synload.talksystem.ConnectionDocument;
import com.synload.talksystem.ConnectionType;

public class ConvertFile extends ConnectionType {
	public <T> ConvertFile(){
		this.clazz = ConvertControl.class;
		try {
			this.func = ConvertControl.class.getDeclaredMethod("convertVideo", Client.class, ConnectionDocument.class);
		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
		this.setName("queryServer");
	}
}
