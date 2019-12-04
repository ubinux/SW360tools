package com.spdx.enums;

public enum HeaderEnums {
	DocumentInformation("## Document Information"), CreationInformation("## Creation Information"), PackageInformation("## Package Information"),
	FileInformation("## File Information"), File("##File"), LicenseInformation("## License Information");
	
	private final String value;

	HeaderEnums(String value) {
		this.value = value;
	}

	public String value() {
		return this.value;
	}

	public static HeaderEnums from(String code) {
		for (HeaderEnums fields : HeaderEnums.values()) {
			if (fields.value.equals(code)) {
				return fields;
			}
		}
		return null;
	}
}
