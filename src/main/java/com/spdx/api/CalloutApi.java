/*------------------------------------------------------------------------*/
// All Rights Reserved, Copyright(C) FUJITSU LIMITED 2019
/*------------------------------------------------------------------------*/

package com.spdx.api;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spdx.enums.ContentType;
import com.spdx.model.ApiResult;
import com.spdx.model.ResponseToken;
import com.spdx.model.SpdxComponent;
import com.spdx.model.SpdxLicense;
import com.spdx.model.SpdxRelease;
import com.spdx.utils.HttpClientUtils;

public class CalloutApi implements ApiInterface {
	private static HttpClientUtils httpClient;
	private static Properties prop = new Properties();
	private static ObjectMapper mapper = new ObjectMapper();
	private static ApiResult result;
	private static String url;
	private static InputStream input;
	final static Logger logger = Logger.getLogger(CalloutApi.class);

	@Override
	public ResponseToken getToken() throws IOException {
		input = CalloutApi.class.getClassLoader().getResourceAsStream("application.properties");
		httpClient = new HttpClientUtils(null, null, true);
		prop.load(input);
		String url = prop.getProperty("host.sw360") + prop.getProperty("request.authen");
		String request = "grant_type=" + prop.getProperty("trust.grant") + "&username="
				+ prop.getProperty("sw360.user") + "&password=" + prop.getProperty("sw360.pass");
		result = httpClient.postRequest(url, request, ContentType.FORM_URLENCODED);
		input.close();
		if (result.getResponseCode()!=200)
			return new ResponseToken();
		else {
			return mapper.readValue(result.getBody(), ResponseToken.class);
		}
	}

	@Override
	public ApiResult postComponent(String requestBody, ResponseToken token) throws Exception {
		url = initRequest("request.import.component", token);
		return httpClient.postRequest(url, requestBody, ContentType.JSON);
	}

	@Override
	public ApiResult postRelease(String requestBody, ResponseToken token)  throws Exception {
		url = initRequest("request.import.release", token);
		return httpClient.postRequest(url, requestBody, ContentType.JSON);
	}
	
	@Override
	public ApiResult getListLicense(ResponseToken token) throws Exception {
		url = initRequest("request.import.license", token);
		return httpClient.getRequest(url);
	}

	@Override
	public ApiResult postLicense(String requestBody, ResponseToken token)  throws Exception {
		url = initRequest("request.import.license", token);
		return httpClient.postRequest(url, requestBody, ContentType.JSON);
	}
	
	@Override
	public ApiResult getComponentIdByName(String componentName, ResponseToken token) throws Exception {
		url = initRequest("request.import.component", token) + "?name="+componentName;
		return httpClient.getRequest(url);
	}
	
	@Override
	public ApiResult updateComponent(String compId, String requestBody, ResponseToken token) throws Exception {
		url = initRequest("request.import.component", token) + compId;
		return httpClient.updateRequest(url, requestBody);
	}
	
	@Override
	public ApiResult getListComponent(ResponseToken token) throws Exception {
		url = initRequest("request.import.component", token);
		return httpClient.getRequest(url);
	}
	
	@Override
	public SpdxComponent getComponent(String urlRequest, ResponseToken token) throws Exception {
		SpdxComponent component = new SpdxComponent();
		httpClient = new HttpClientUtils(token.getToken_type(), token.getAccess_token(), false);
		ApiResult result = httpClient.getRequest(urlRequest);
		if(result.getResponseCode() == 200 || result.getResponseCode() == 201)
			component =  mapper.readValue(result.getBody(), SpdxComponent.class);
		return component;
	}
	
	@Override
	public ApiResult deleteRelease(String request, ResponseToken token) throws Exception {
		httpClient = new HttpClientUtils(token.getToken_type(), token.getAccess_token(), false);
		return  httpClient.deleteRequest(request);
	}
	
	private String initRequest(String request, ResponseToken token) throws IOException {
		input = CalloutApi.class.getClassLoader().getResourceAsStream("application.properties");
		prop.load(input);
		httpClient = new HttpClientUtils(token.getToken_type(), token.getAccess_token(), false);
		input.close();
		return prop.getProperty("host.sw360") + prop.getProperty(request);
	}
	
	@Override
	public SpdxLicense getLicense(String urlRequest, ResponseToken token) throws Exception {
		SpdxLicense license = new SpdxLicense();
		httpClient = new HttpClientUtils(token.getToken_type(), token.getAccess_token(), false);
		ApiResult result = httpClient.getRequest(urlRequest);
		if(result.getResponseCode() == 200 || result.getResponseCode() == 201)
			license =  mapper.readValue(result.getBody(), SpdxLicense.class);
		return license;
	}
	
	@Override
	public ApiResult getReleaseByNameAndVersion(String componentId, String name, String version, ResponseToken token) throws Exception {
		url = initRequest("request.import.component", token) + "/"+componentId;
		return httpClient.getRequest(url);
	}

	@Override
	public ApiResult updateRelease(String releaseId, String requestBody, ResponseToken token) throws Exception {
		url = initRequest("request.import.release", token) + releaseId;
		return httpClient.updateRequest(url, requestBody);
	}

	@Override
	public ApiResult getReleaseByComponent(String apiComponent, ResponseToken token) throws Exception {
		httpClient = new HttpClientUtils(token.getToken_type(), token.getAccess_token(), false);
		return httpClient.getRequest(apiComponent);
	}

	@Override
	public SpdxRelease getRelease(String urlRequest, ResponseToken token) throws Exception {
		SpdxRelease release = new SpdxRelease();
		httpClient = new HttpClientUtils(token.getToken_type(), token.getAccess_token(), false);
		ApiResult result = httpClient.getRequest(urlRequest);
		if(result.getResponseCode() == 200 || result.getResponseCode() == 201)
			release =  mapper.readValue(result.getBody(), SpdxRelease.class);
		return release;
	}

}
