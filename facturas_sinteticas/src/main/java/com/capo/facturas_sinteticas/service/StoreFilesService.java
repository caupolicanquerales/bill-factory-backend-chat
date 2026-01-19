package com.capo.facturas_sinteticas.service;

import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class StoreFilesService {
	
	private Map<String,byte[]> fileParts;

	public Map<String, byte[]> getFileParts() {
		return fileParts;
	}

	public void setFileParts(Map<String, byte[]> fileParts) {
		this.fileParts = fileParts;
	}
}
