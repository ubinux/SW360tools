package com.spdx.model;

import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SpdxRelease {
	private String componentId;
	private String version;
	private String name;
	
	private String spdxVersion;
	private String packageSpdxId;
	private String packageVersion;
	private String packageDownloadLocation;
	private String packageVerificationCode;
	private String packageLicenseConcluded;
	private String packageLicenseInfoFromFiles;
	private String packageLicenseDeclared;
	private String packageCopyrightText;
	private String packageHomePage;
	private String packageFileName;
	private String filesAnalyzed;
	private String packageLicenseComments;
	
	private Set<String> licenseIds;
	private String modificationRecord;
	private String compileOptions;
	private Map<String, String> packageCommentExtends;
	
	private String dataLicense;
	private String spdxId;
	private String documentName;
	private String documentNamespace;
	private Set<String> creators;
	private String created;
}
