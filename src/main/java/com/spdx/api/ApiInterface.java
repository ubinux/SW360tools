package com.spdx.api;

import com.spdx.model.ApiResult;
import com.spdx.model.ResponseToken;
import com.spdx.model.SpdxComponent;
import com.spdx.model.SpdxLicense;
import com.spdx.model.SpdxRelease;

public interface ApiInterface {
	ResponseToken getToken() throws Exception;
	ApiResult postComponent(String requestBody, ResponseToken token)  throws Exception;
	ApiResult updateComponent(String compId, String requestBody, ResponseToken token) throws Exception;
	ApiResult postRelease(String requestBody, ResponseToken token)  throws Exception;
	ApiResult postLicense(String requestBody, ResponseToken token)  throws Exception;
	ApiResult getListLicense(ResponseToken token) throws Exception;
	ApiResult getComponentIdByName(String componentName, ResponseToken token) throws Exception;
	ApiResult getListComponent(ResponseToken token) throws Exception;
	SpdxComponent getComponent(String urlRequest, ResponseToken token) throws Exception;
	SpdxLicense getLicense(String urlRequest, ResponseToken token) throws Exception;
	ApiResult getComponentById(String componentId, ResponseToken token) throws Exception;
	ApiResult deleteRelease(String request, ResponseToken token) throws Exception;
	ApiResult getReleaseByNameAndVersion(String componentId, String name, String version, ResponseToken token) throws Exception;
	ApiResult getReleaseByComponent(String apiComponent, ResponseToken token) throws Exception;
	ApiResult updateRelease(String releaseId, String requestBody, ResponseToken token) throws Exception;
	SpdxRelease getRelease(String urlRequest, ResponseToken token) throws Exception;
}
