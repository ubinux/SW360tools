/*------------------------------------------------------------------------*/
// All Rights Reserved, Copyright(C) FUJITSU LIMITED 2019
/*------------------------------------------------------------------------*/

package com.spdx.model;

import lombok.Data;

@Data
public class HeaderFileNumber {
	private int sizeOfFile;
	private int fileInformation;
	private int licenseInformation;
	private String downLoadUrl;
	private int packageLicenseComments;
}
