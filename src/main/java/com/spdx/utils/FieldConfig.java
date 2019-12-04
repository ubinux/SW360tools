package com.spdx.utils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.spdx.enums.ExcelFieldEnums;
import com.spdx.model.SpdxRelease;

public class FieldConfig {
	
	final static Logger logger = Logger.getLogger(FieldConfig.class);

	
	public static String getFieldsByHeader(String field, SpdxRelease release, List<String> lstSpdxFields) {
		if(ExcelFieldEnums.from(field.trim()) == ExcelFieldEnums.PackageName)
			return release.getName();
		if(ExcelFieldEnums.from(field.trim()) == ExcelFieldEnums.SPDXVersion)
			return release.getSpdxVersion();
		if(ExcelFieldEnums.from(field.trim()) == ExcelFieldEnums.PackageSPDXID)
			return release.getPackageSpdxId();
		if(ExcelFieldEnums.from(field.trim()) == ExcelFieldEnums.PackageVersion)
			return release.getPackageVersion();
		if(ExcelFieldEnums.from(field.trim()) == ExcelFieldEnums.PackageFileName)
			return release.getPackageFileName();
		if(ExcelFieldEnums.from(field.trim()) == ExcelFieldEnums.PackageDownloadLocation)
			return release.getPackageDownloadLocation();
		if(ExcelFieldEnums.from(field.trim()) == ExcelFieldEnums.FilesAnalyzed)
			return release.getFilesAnalyzed();
		if(ExcelFieldEnums.from(field.trim()) == ExcelFieldEnums.PackageHomepage)
			return release.getPackageHomePage();
		if(ExcelFieldEnums.from(field.trim()) == ExcelFieldEnums.PackageLicenseConcluded)
			return release.getPackageLicenseConcluded();
		if(ExcelFieldEnums.from(field.trim()) == ExcelFieldEnums.PackageLicenseDeclared)
			return release.getPackageLicenseDeclared();
		if(ExcelFieldEnums.from(field.trim()) == ExcelFieldEnums.PackageLicenseComments)
			return release.getPackageLicenseComments();
		if(ExcelFieldEnums.from(field.trim()) == ExcelFieldEnums.PackageCopyrightText)
			return release.getPackageCopyrightText();
		if(ExcelFieldEnums.from(field.trim()) == ExcelFieldEnums.Created)
			return release.getCreated();
		if(ExcelFieldEnums.from(field.trim()) == ExcelFieldEnums.DataLicense)
			return release.getDataLicense();
		if(ExcelFieldEnums.from(field.trim()) == ExcelFieldEnums.SPDXID)
			return release.getSpdxId();
		if(ExcelFieldEnums.from(field.trim()) == ExcelFieldEnums.DocumentName)
			return release.getDocumentName();
		if(ExcelFieldEnums.from(field.trim()) == ExcelFieldEnums.DocumentNamespace)
			return release.getDocumentNamespace();
		if(ExcelFieldEnums.from(field.trim()) == ExcelFieldEnums.PackageVerificationCode)
			return release.getPackageVerificationCode();
		if(ExcelFieldEnums.from(field.trim()) == ExcelFieldEnums.PackageLicenseInfoFromFiles)
			return release.getPackageLicenseInfoFromFiles();
		if(ExcelFieldEnums.from(field.trim()) == ExcelFieldEnums.PackageCopyrightText)
			return release.getPackageCopyrightText();
		if(ExcelFieldEnums.from(field.trim()) == ExcelFieldEnums.ModificationRecord)
			return release.getModificationRecord();
		if(ExcelFieldEnums.from(field.trim()) == ExcelFieldEnums.CompileOptions)
			return release.getCompileOptions();
		
		// Check exist package comment extend
		if(lstSpdxFields.contains(field) && release.getPackageCommentExtends() != null) {
			return release.getPackageCommentExtends().get(field);
		}
		return null;
	}
	
	public static String getFieldsNameHeader(String spdxField) throws Exception {
		Map<String, String> spdx = new HashMap<String, String>();
		List<String> spdxFields = new ArrayList<String>();
		List<String> headers = new ArrayList<String>();
		Properties prop = new Properties();
		InputStream input = FieldConfig.class.getClassLoader().getResourceAsStream("excel.properties");
		prop.load(input);
		spdxFields.addAll(Arrays.asList(prop.getProperty("spdxfield").split(",")));
		spdxFields.addAll(Arrays.asList(prop.getProperty("spdx_field_extends").split(",")));
		headers.addAll(Arrays.asList(prop.getProperty("spdxHeader").split(",")));
		headers.addAll(Arrays.asList(prop.getProperty("spdx_header_extends").split(",")));
		for (String item : spdxFields)
			spdx.put(item, headers.get(spdxFields.indexOf(item)));
		input.close();
		for (Map.Entry<String,String> entry : spdx.entrySet()) {
			if(entry.getKey().equals(spdxField))
				return entry.getValue();
		}
		return null;
	}

}
