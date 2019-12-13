package com.spdx.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ResponseApiComponent {
	private Boolean responseFlg;
	private String componentId;
	private String componentName;
}
