package com.spdx.enums;

public enum SpdxFieldEnums {
	SPDXVersion("SPDXVersion:"),
	DataLicense("DataLicense:"),
	SPDXID("SPDXID:"),
	DocumentName("DocumentName:"),
	DocumentNamespace("DocumentNamespace:"),
	Creator("Creator:"),
	Created("Created:"),
	PackageName("PackageName:"),
	PackageVersion("PackageVersion:"),
	PackageDownloadLocation("PackageDownloadLocation:"),
	PackageVerificationCode("PackageVerificationCode:"),
	PackageLicenseConcluded("PackageLicenseConcluded:"),
	PackageLicenseInfoFromFiles("PackageLicenseInfoFromFiles:"),
	PackageLicenseDeclared("PackageLicenseDeclared:"),
	PackageCopyrightText("PackageCopyrightText:"),
	PackageHomePage("PackageHomePage:"),
	FilesAnalyzed("FilesAnalyzed:"),
	PackageFileName("PackageFileName:"),
	PackageLicenseComments("PackageLicenseComments: <text>"),
	PackageComment("PackageComment: <text>"),
	FileName("FileName:"),
	FileChecksum("FileChecksum:"),
	LicenseConcluded("LicenseConcluded:"),
	LicenseInfoInFile("LicenseInfoInFile:"),
	FileCopyrightText("FileCopyrightText:"),
	SnippetSPDXID("SnippetSPDXID:"),
	SnippetFromFileSPDXID("SnippetFromFileSPDXID:"),
	SnippetByteRange("SnippetByteRange:"),
	SnippetLicenseConcluded("SnippetLicenseConcluded:"),
	SnippetCopyrightText("SnippetCopyrightText:"),	
	LicenseID("LicenseID:"),
	LicenseName("LicenseName:"),
	ExtractedText("ExtractedText: <text>");

	private final String value;

	SpdxFieldEnums(String value) {
		this.value = value;
	}

	public String value() {
		return this.value;
	}

	public static SpdxFieldEnums from(String code) {
		for (SpdxFieldEnums fields : SpdxFieldEnums.values()) {
			if (fields.value.equals(code)) {
				return fields;
			}
		}
		return null;
	}

}
