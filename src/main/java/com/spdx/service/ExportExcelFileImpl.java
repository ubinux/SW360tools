/*------------------------------------------------------------------------*/
// All Rights Reserved, Copyright(C) FUJITSU LIMITED 2019
/*------------------------------------------------------------------------*/

package com.spdx.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import com.spdx.api.ApiInterface;
import com.spdx.api.CalloutApi;
import com.spdx.enums.ExcelFieldEnums;
import com.spdx.model.ApiResult;
import com.spdx.model.ResponseToken;
import com.spdx.model.SpdxExcel;
import com.spdx.model.SpdxLicense;
import com.spdx.model.SpdxRelease;
import com.spdx.utils.FieldConfig;

@Service
public class ExportExcelFileImpl {

	private static ApiInterface callout = new CalloutApi();
	final static Logger logger = Logger.getLogger(ExportExcelFileImpl.class);
	private static final String PACKAGE_COMMENT_HEADER = "Package Comment";
	private static final String MODIFICATION_RECORD_HEADER = "ModificationRecord:";
	private static final String COMPILE_OPTIONS_HEADER = "CompileOptions:";
	
	
	public List<SpdxExcel> initMandatoryFields() throws Exception {
		List<SpdxExcel> spdx = new ArrayList<SpdxExcel>();
		List<String> spdxFields = new ArrayList<String>();
		List<String> headers = new ArrayList<String>();
		Properties prop = new Properties();
		InputStream input = ExportExcelFileImpl.class.getClassLoader().getResourceAsStream("excel.properties");
		prop.load(input);
		spdxFields.addAll(Arrays.asList(prop.getProperty("spdxfield").split(",")));
		headers.addAll(Arrays.asList(prop.getProperty("spdxfield").split(",")));
		for (int i = 0; i < spdxFields.size(); i++)
			spdx.add(new SpdxExcel(spdxFields.get(i), headers.get(i)));
		return spdx;
	}
	
	public List<SpdxExcel> initExtendFields() throws Exception {
		List<SpdxExcel> spdx = new ArrayList<SpdxExcel>();
		List<String> spdxFields = new ArrayList<String>();
		List<String> headers = new ArrayList<String>();
		Properties prop = new Properties();
		InputStream input = ExportExcelFileImpl.class.getClassLoader().getResourceAsStream("excel.properties");
		prop.load(input);
		spdxFields.addAll(Arrays.asList(prop.getProperty("spdx_field_extends").split(",")));
		headers.addAll(Arrays.asList(prop.getProperty("spdx_field_extends").split(",")));
		for (int i = 0; i < spdxFields.size(); i++)
			spdx.add(new SpdxExcel(spdxFields.get(i), headers.get(i)));
		return spdx;
	}

	public String export(String spdxFields) throws Exception {
		
		// Get data from sw360 website
		ResponseToken token = callout.getToken();
		List<SpdxLicense> lstLicenses = getLicenses(token);
		List<SpdxRelease> lstRelease = lstLicenses.size() == 0 ? new ArrayList<SpdxRelease>() : getListRelease(token);
		
		// Package comment extends header
		Set<String> packageCommentExtendsHeader = new HashSet<String>();
		for (SpdxRelease spdxRelease : lstRelease) {
			Map<String, String> packageCommentExtends = spdxRelease.getPackageCommentExtends();
			if (packageCommentExtends != null) {
				for (Map.Entry<String,String> entry : packageCommentExtends.entrySet()) {
					packageCommentExtendsHeader.add(entry.getKey());
				}
			}
		}
		
		List<String> headers = new ArrayList<String>();
		headers.add("No.");
		headers.addAll(Arrays.asList(spdxFields.split(",", -1)));
		
		List<String> lstSpdxFields = new ArrayList<String>();
		lstSpdxFields.add("No.");
		lstSpdxFields.addAll(Arrays.asList(spdxFields.split(",", -1)));
		
		for (String item : headers) {
			if(!"No.".equals(item))
				headers.set(headers.indexOf(item), FieldConfig.getFieldsNameHeader(item));
		}
		
		// Set package comment extends header
		if (headers.contains("PackageComment")) {
			int indexPackageComment = headers.indexOf("PackageComment");
			headers.remove(indexPackageComment);
			lstSpdxFields.remove(indexPackageComment);
			
			lstSpdxFields.add(indexPackageComment, "modificationRecord");
			headers.add(indexPackageComment, PACKAGE_COMMENT_HEADER + ": " + MODIFICATION_RECORD_HEADER);
			
			indexPackageComment++;
			headers.add(indexPackageComment, PACKAGE_COMMENT_HEADER + ": " + COMPILE_OPTIONS_HEADER);
			lstSpdxFields.add(indexPackageComment, "compileOptions");
			
			// Add dynamic package comment extend fields
			indexPackageComment++;
			for (String packageCommentExtend : packageCommentExtendsHeader) {
				headers.add(indexPackageComment, PACKAGE_COMMENT_HEADER + ": " + packageCommentExtend);
				lstSpdxFields.add(indexPackageComment, packageCommentExtend);
				
				indexPackageComment++;
			}
		}
		
		Workbook workbook = new XSSFWorkbook();
		Sheet sheet = workbook.createSheet("spdx-lite");
		Row headerRow = sheet.createRow(0);
		Font font = workbook.createFont();
		font.setFontName("Calibri");
		font.setFontHeightInPoints((short) 9);
		CellStyle headerCellStyle = workbook.createCellStyle();
		
		headerCellStyle.setFont(font);
		headerCellStyle.setFillBackgroundColor(IndexedColors.AQUA.getIndex());
		headerCellStyle.setAlignment(HorizontalAlignment.CENTER);
		headerCellStyle.setBorderBottom(BorderStyle.THIN);
		headerCellStyle.setBorderTop(BorderStyle.THIN);
		headerCellStyle.setBorderRight(BorderStyle.THIN);
		headerCellStyle.setBorderLeft(BorderStyle.THIN);
		headerCellStyle.setFillForegroundColor(IndexedColors.PALE_BLUE.getIndex());
		headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		
		CellStyle dataCellStyle = workbook.createCellStyle();
		dataCellStyle.setFont(font);
		dataCellStyle.setBorderBottom(BorderStyle.THIN);
		dataCellStyle.setBorderTop(BorderStyle.THIN);
		dataCellStyle.setBorderRight(BorderStyle.THIN);
		dataCellStyle.setBorderLeft(BorderStyle.THIN);
		dataCellStyle.setWrapText(true);
		
		int rowNum = 1;
		for (int i = 0; i < headers.size(); i++) {
			Cell cell = headerRow.createCell(i);
			cell.setCellValue(headers.get(i));
			cell.setCellStyle(headerCellStyle);
		}
		for (SpdxRelease release : lstRelease) {
			// Check have export license id, Extracted Text, License Name
			List<String> licenses = new ArrayList<String>();
			licenses.add(ExcelFieldEnums.LicenseID.value());
			licenses.add(ExcelFieldEnums.ExtractedText.value());
			licenses.add(ExcelFieldEnums.LicenseName.value());
			
			List<SpdxLicense> licensesofComponent = new ArrayList<SpdxLicense>();
			
			boolean existLicense = false;
			for (String field : lstSpdxFields) {
				if (licenses.contains(field)) {
					existLicense = true;
					break;
				}
			}
			
			if (existLicense && release.getLicenseIds() != null) {
				// Get license of component
				for(String licenseId : release.getLicenseIds()) {
					for (SpdxLicense license : lstLicenses) {
						if (licenseId.contentEquals(license.getShortName())) {
							licensesofComponent.add(license);
						}
					}
				}
			}
			
			// New license when no have license
			if (licensesofComponent.size() == 0) {
				licensesofComponent.add(new SpdxLicense());
			}
			
			// Get creators
			Set<String> creators = release.getCreators() ==  null ? new HashSet<String>() : release.getCreators();
			// Check size of creators with size of licenses
			int rowTmp;
			if (lstSpdxFields.contains("Creator")) {
				rowTmp = creators.size() > licensesofComponent.size() ? creators.size() : licensesofComponent.size();
			} else {
				rowTmp = licensesofComponent.size();
			}
			 
			Iterator<String> creatorsIterator = creators.iterator();
			
			for (int i = 0; i < rowTmp; i++) {
				Row row = sheet.createRow(rowNum++);
				Cell cell = row.createCell(0);
				cell.setCellStyle(dataCellStyle);
				cell.setCellValue(lstRelease.indexOf(release)+1);
				
				
				for (int j = 1; j < lstSpdxFields.size(); j++) {
					Cell cellData = row.createCell(j);
					cellData.setCellStyle(dataCellStyle);
					
					switch (lstSpdxFields.get(j)) {
					case "LicenseID":
						if (i >= licensesofComponent.size())
							break;
						cellData.setCellValue(licensesofComponent.get(i).getShortName());
						break;

					case "ExtractedText":
						if (i >= licensesofComponent.size())
							break;
						cellData.setCellValue(licensesofComponent.get(i).getText());
						break;
					case "LicenseName":
						if (i >= licensesofComponent.size())
							break;
						cellData.setCellValue(licensesofComponent.get(i).getFullName());
						break;
					case "Creator":
						if (!creatorsIterator.hasNext())
							break;
						cellData.setCellValue(creatorsIterator.next());
						break;
					default:
						cellData.setCellValue(FieldConfig.getFieldsByHeader(lstSpdxFields.get(j), release, lstSpdxFields));
						break;
					}
					
					
				}
			}
		}
		for (int i = 0; i < lstSpdxFields.size(); i++) {
			sheet.autoSizeColumn(i);
		}
		
		DateFormat format = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
		String date = format.format(new Date()).replace("-", "").replace(" ", "").replace(":", "");

		InputStream input = ExportExcelFileImpl.class.getClassLoader().getResourceAsStream("application.properties");
		Properties prop = new Properties();
		prop.load(input);
		String downloadPath = prop.getProperty("download.path");
		File drictory = new File(downloadPath);
                if (!drictory.exists())
                        Files.createDirectory(Paths.get(downloadPath));
                else if (drictory.exists() && !drictory.isDirectory()) {
                        drictory.delete();
                        Files.createDirectory(Paths.get(downloadPath));
                }

		String pathFile = downloadPath + "SPDX-lite-"+ date + ".xlsx";
		FileOutputStream fileOut = new FileOutputStream(pathFile);
		workbook.write(fileOut);
		fileOut.close();
		workbook.close();
		return pathFile;
	}

	private List<String> getListApiComponent(ResponseToken token) throws Exception {
		List<String> lstApiComponent = new ArrayList<String>();
		ApiResult result = callout.getListComponent(token);
		JSONArray lstComponent = new JSONArray();
		if ((result.getResponseCode() == 200 || result.getResponseCode() == 201) && result.getBody().contains("sw360:components")) {
			lstComponent = (new JSONObject(result.getBody())).getJSONObject("_embedded")
					.getJSONArray("sw360:components");
			for (Object object : lstComponent) {
				if (object instanceof JSONObject)
					lstApiComponent
							.add(((JSONObject) object).getJSONObject("_links").getJSONObject("self").getString("href"));
			}
		}
		return lstApiComponent;
	}

	private List<String> getListApiReleaseByComponent(String apiComponent, ResponseToken token) throws Exception {
		List<String> lstApiRelease = new ArrayList<String>();
		ApiResult result = callout.getReleaseByComponent(apiComponent, token);
		JSONArray lstComponent = new JSONArray();
		if ((result.getResponseCode() == 200 || result.getResponseCode() == 201) && result.getBody().contains("sw360:releases")) {
			lstComponent = (new JSONObject(result.getBody())).getJSONObject("_embedded")
					.getJSONArray("sw360:releases");
			for (Object object : lstComponent) {
				if (object instanceof JSONObject)
					lstApiRelease.add(((JSONObject) object).getJSONObject("_links").getJSONObject("self").getString("href"));
			}
		}
		return lstApiRelease;
	}
	
	private List<SpdxRelease> getListRelease(ResponseToken token) throws Exception {
		List<SpdxRelease> lstRelease = new ArrayList<SpdxRelease>();
		List<String> lstApi = getListApiComponent(token);
		for (String url : lstApi) {
			// Get release
			List<String> lstApiRelease = getListApiReleaseByComponent(url, token);
			for (String apiRelease : lstApiRelease) {
				lstRelease.add(callout.getRelease(apiRelease, token));
			}
		}
		
		return lstRelease;
		
	}
	
	private List<SpdxLicense> getLicenses(ResponseToken token) throws Exception {
		List<SpdxLicense> lstLicenses = new ArrayList<SpdxLicense>();
		
		ApiResult result = callout.getListLicense(token);
		
		// Get all url licenses
		List<String> lstApiLicenses = new ArrayList<String>();
		
		JSONArray lstComponent = new JSONArray();
		if ((result.getResponseCode() == 200 || result.getResponseCode() == 201) && result.getBody().contains("sw360:licenses")) {
			lstComponent = (new JSONObject(result.getBody())).getJSONObject("_embedded")
					.getJSONArray("sw360:licenses");
			for (Object object : lstComponent) {
				if (object instanceof JSONObject)
					lstApiLicenses
							.add(((JSONObject) object).getJSONObject("_links").getJSONObject("self").getString("href"));
			}
		}
		
		// Get all licenses information
		for (String url : lstApiLicenses)
			lstLicenses.add(callout.getLicense(url, token));
		return lstLicenses;
	}

}
