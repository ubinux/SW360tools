/*------------------------------------------------------------------------*/
// All Rights Reserved, Copyright(C) FUJITSU LIMITED 2019
/*------------------------------------------------------------------------*/

package com.spdx.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResult {
	private int responseCode;
	private String body;
}
