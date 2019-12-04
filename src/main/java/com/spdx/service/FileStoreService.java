package com.spdx.service;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import com.spdx.model.OutputModel;

public interface FileStoreService {
	
	public void store(MultipartFile file) throws IOException;

	public void init();
	
	public OutputModel readContentFile(String filename) throws Exception;
	
}
