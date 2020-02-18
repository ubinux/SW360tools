/*------------------------------------------------------------------------*/
// All Rights Reserved, Copyright(C) FUJITSU LIMITED 2019
/*------------------------------------------------------------------------*/

package com.spdx.controller;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.spdx.model.OutputModel;
import com.spdx.model.SpdxExcel;
import com.spdx.service.ExportExcelFileImpl;
import com.spdx.service.FileStoreService;

@Controller
public class ImportFileController {
	
	@Autowired
	FileStoreService fileStorage;
	
	@Autowired
	ServletContext context;
	
	@Autowired
	ExportExcelFileImpl exportExcelFile;

	final static Logger logger = Logger.getLogger(ImportFileController.class);
	
	@RequestMapping(value = "/import", method = RequestMethod.GET)
	public ModelAndView home(HttpServletRequest request, HttpServletResponse response) {
		fileStorage.init();
		ModelAndView mav = new ModelAndView("home");
		mav.addObject("home");
		return mav;
	}
	
	@RequestMapping(value = "/export", method = RequestMethod.GET)
	public String export(HttpServletRequest request, HttpServletResponse response, Model model) {
		List<SpdxExcel> mandatory;
		List<SpdxExcel> optional;
		try {
			mandatory = exportExcelFile.initMandatoryFields();
			optional = exportExcelFile.initExtendFields();
			model.addAttribute("mandatory", mandatory);
			model.addAttribute("optional", optional);
		} catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
		}
		return "export";
	}

	@RequestMapping(value = "/upload", method = RequestMethod.POST, headers = "Content-Type=multipart/form-data")
	public String uploadMultipartFile(@RequestParam MultipartFile[] files, Model model) {
		List<String> fileNames = null;
		List<OutputModel> outputs = new ArrayList<OutputModel>();
		try {
			fileNames = Arrays.asList(files).stream().map(file -> {
				try {
					fileStorage.store(file);
				} catch (IOException e) {
					logger.error(e);
				}
				return file.getOriginalFilename();
			}).collect(Collectors.toList());
			if(fileNames.size()==0 || fileNames == null) {
				model.addAttribute("messages", "No file upload");
			} else {
				for (String item : fileNames) {
					outputs.add(fileStorage.readContentFile(item));
				}
				model.addAttribute("response", outputs);
			}
		} catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
		}
		return "home";
	}
	
	@RequestMapping(value = "/download", method = RequestMethod.POST, consumes=MediaType.APPLICATION_JSON_VALUE)
	@Qualifier("lstParam")
	public void downloadFile(HttpServletRequest request, HttpServletResponse response,@RequestParam String spdxFields) {
		String pathFile = null;
		try {
			pathFile = exportExcelFile.export(spdxFields);
			File file = new File(pathFile);
			byte[] data = FileUtils.readFileToByteArray(file);
			response.setContentType("application/octet-stream");
			response.setHeader("Content-disposition", "attachment; filename=" + file.getName());
			response.setContentLength(data.length);
			InputStream inputStream = new BufferedInputStream(new ByteArrayInputStream(data));
			FileCopyUtils.copy(inputStream, response.getOutputStream());
		} catch (Exception e) {
			logger.error(e);
		}
	}

}
