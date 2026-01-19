package com.capo.facturas_sinteticas.utils;

import java.util.HashMap;
import java.util.Map;

import org.springframework.util.MimeType;

public class ExtensionUtils {
		
	public static MimeType getMapMimeType(String fileName) {
		int dot = fileName.lastIndexOf(".");
		if (dot < 0) {
			return null;
		}
		String ext = fileName.substring(dot).toLowerCase();
		Map<String, MimeType> mapExt = new HashMap<String, MimeType>();
		// Text types
		mapExt.put(".html", MimeType.valueOf("text/html"));
		mapExt.put(".css", MimeType.valueOf("text/css"));
		mapExt.put(".scss", MimeType.valueOf("text/scss"));
		mapExt.put(".txt", MimeType.valueOf("text/plain"));
		mapExt.put(".md", MimeType.valueOf("text/markdown"));
		mapExt.put(".json", MimeType.valueOf("application/json"));
		// Image types
		mapExt.put(".png", MimeType.valueOf("image/png"));
		mapExt.put(".jpg", MimeType.valueOf("image/jpeg"));
		mapExt.put(".jpeg", MimeType.valueOf("image/jpeg"));
		mapExt.put(".gif", MimeType.valueOf("image/gif"));
		// Binary docs
		mapExt.put(".pdf", MimeType.valueOf("application/pdf"));
	    return mapExt.get(ext);
	}

}
