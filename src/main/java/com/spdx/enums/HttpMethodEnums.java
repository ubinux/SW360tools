package com.spdx.enums;

public enum HttpMethodEnums {
	POST("POST"), GET("GET"), PATCH("PATCH"), DELETE("DELETE");
	
	private final String value;

	HttpMethodEnums(String value) {
		this.value = value;
	}

	public String value() {
		return this.value;
	}

	public static HttpMethodEnums from(String code) {
		for (HttpMethodEnums fields : HttpMethodEnums.values()) {
			if (fields.value.equals(code)) {
				return fields;
			}
		}
		return null;
	}

}
