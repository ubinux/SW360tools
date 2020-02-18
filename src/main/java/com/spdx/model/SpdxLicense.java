/*------------------------------------------------------------------------*/
// All Rights Reserved, Copyright(C) FUJITSU LIMITED 2019
/*------------------------------------------------------------------------*/

package com.spdx.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SpdxLicense {
	private String shortName;
	private String fullName;
	private String text;
}
