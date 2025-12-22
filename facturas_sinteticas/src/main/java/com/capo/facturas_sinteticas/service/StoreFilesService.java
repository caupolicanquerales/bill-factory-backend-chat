package com.capo.facturas_sinteticas.service;

import java.util.List;

import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;

@Service
public class StoreFilesService {
	
	private List<FilePart> fileParts;
	
	public void setFileParts(List<FilePart> fileParts) {
		this.fileParts=fileParts;
	}
	
	public List<FilePart> getFileParts(){
		return fileParts;
	}
}
