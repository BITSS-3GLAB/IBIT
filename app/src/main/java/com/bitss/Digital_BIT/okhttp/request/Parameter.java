package com.bitss.Digital_BIT.okhttp.request;

import java.io.File;

import okhttp3.MediaType;

public class Parameter {

	public static final MediaType PNG = MediaType.parse("image/png");
	public static final MediaType JPG = MediaType.parse("image/jpeg");

	private String name;
	private Object value;
	private boolean isFile = false;
	private String filename;
	private MediaType type;

	public Parameter(String name, Object value) {
		this.name = name;
		this.value = value;
		if (this.value instanceof File) {
			isFile = true;
			filename = ((File) this.value).getName();
			if (filename.endsWith(".png") || filename.endsWith(".PNG")) {
				type = PNG;
			} else if (filename.endsWith(".jpg") || filename.endsWith(".jpeg")
					|| filename.endsWith(".JPG") || filename.endsWith(".JPEG")) {
				type = JPG;
			} else {
				type = null;
			}
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public String getFilename() {
		return filename;
	}

	public boolean isFile() {
		return isFile;
	}
	
	public MediaType getType() {
		return type;
	}
}
