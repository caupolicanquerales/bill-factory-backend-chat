package com.capo.facturas_sinteticas.utils;

import java.util.HashMap;
import java.util.Map;

import org.springframework.util.MimeType;

public class ExtensionUtils {
		
	public static MimeType getMapMimeType(String fileName) {
		int dot= fileName.lastIndexOf(".");
		String ext= fileName.substring(dot);
		Map<String,MimeType> mapExt= new HashMap<String, MimeType>();
		mapExt.put(".html", MimeType.valueOf("text/html"));
		mapExt.put(".css", MimeType.valueOf("text/css"));
		mapExt.put(".scss", MimeType.valueOf("text/scss"));
	    return mapExt.get(ext);
	}

}
