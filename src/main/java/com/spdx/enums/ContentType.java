package com.spdx.enums;

public enum ContentType {
	JSON("application/json"), FORM_URLENCODED("application/x-www-form-urlencoded"), NOTSET("");
	
	private final String value;

	ContentType(String value) {
		this.value = value;
	}

	public String value() {
		return this.value;
	}

	public static ContentType from(String code) {
		for (ContentType enums : ContentType.values()) {
			if (enums.value.equals(code)) {
				return enums;
			}
		}
		return null;
	}

}
