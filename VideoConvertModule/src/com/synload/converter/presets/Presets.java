package com.synload.converter.presets;

import java.lang.reflect.Field;

import com.synload.converter.models.Preset;

public class Presets {
	public Class<?> h264 = H264.class;
	public Class<?> h264_480 = H264480.class;
	public Class<?> h265 = H265.class;
	public Class<?> h265_4k = H2654K.class;
	public Class<?> vp8 = VP8.class;
	public Class<?> vp8_480 = VP8480.class;
	public Class<?> vp8_4k = VP84K.class;
	public Class<?> vp9 = VP9.class;
	public Class<?> vp9_4k = VP94K.class;
	public Class<?> i = null;
	public Presets(String key){
		for(Field f:this.getClass().getDeclaredFields()){
			if(f.getName().equalsIgnoreCase(key)){
				try {
					i=(Class<?>) f.get(this);
				} catch (IllegalArgumentException | IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
	public Class<?> get(){
		return this.i;
	}
}
