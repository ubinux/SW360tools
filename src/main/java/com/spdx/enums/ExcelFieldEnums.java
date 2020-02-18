/*------------------------------------------------------------------------*/
// All Rights Reserved, Copyright(C) FUJITSU LIMITED 2019
/*------------------------------------------------------------------------*/

package com.spdx.enums;

public enum ExcelFieldEnums {
	SPDXVersion("SPDXVersion"),
	DataLicense("DataLicense"),
	SPDXID("SPDXID"),
	DocumentName("DocumentName"),
	DocumentNamespace("DocumentNamespace"),
	Creator("Creator"),
	Created("Created"),
	PackageSPDXID("PackageSPDXID"),
	PackageName("PackageName"),
	PackageVersion("PackageVersion"),
	PackageDownloadLocation("PackageDownloadLocation"),
	PackageVerificationCode("PackageVerificationCode"),
	PackageLicenseConcluded("PackageLicenseConcluded"),
	PackageLicenseInfoFromFiles("PackageLicenseInfoFromFiles"),
	PackageLicenseDeclared("PackageLicenseDeclared"),
	PackageCopyrightText("PackageCopyrightText"),
	PackageHomepage("PackageHomepage"),
	FilesAnalyzed("FilesAnalyzed"),
	PackageFileName("PackageFileName"),
	PackageLicenseComments("PackageLicenseComments"),
	LicenseID("LicenseID"),
	ExtractedText("ExtractedText"),
	LicenseName("LicenseName"),
	LicenseComment("LicenseComment"),
	ModificationRecord("modificationRecord"),
	CompileOptions("compileOptions");

	private final String value;

	ExcelFieldEnums(String value) {
		this.value = value;
	}

	public String value() {
		return this.value;
	}

	public static ExcelFieldEnums from(String code) {
		for (ExcelFieldEnums fields : ExcelFieldEnums.values()) {
			if (fields.value.equals(code)) {
				return fields;
			}
		}
		return null;
	}


}
