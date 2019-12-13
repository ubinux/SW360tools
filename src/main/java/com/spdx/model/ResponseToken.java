package com.spdx.model;

import lombok.Data;

@Data
public class ResponseToken {
	private String access_token;
	private String token_type;
	private String expires_in;
	private String scope;
	private String jti;

}
