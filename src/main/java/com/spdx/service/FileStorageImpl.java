package com.spdx.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spdx.api.ApiInterface;
import com.spdx.api.CalloutApi;
import com.spdx.model.ApiResult;
import com.spdx.model.HeaderFileNumber;
import com.spdx.model.OutputModel;
import com.spdx.model.ResponseApiComponent;
import com.spdx.model.ResponseToken;
import com.spdx.model.SpdxComponent;
import com.spdx.model.SpdxLicense;
import com.spdx.model.SpdxRelease;
import com.spdx.utils.SpdxUtils;

@Service
public class FileStorageImpl implements FileStoreService {

	private final String rootPathFile = "D:\\file\\";
	private final Path rootLocation = Paths.get(rootPathFile);
	private ObjectMapper mapper = new ObjectMapper();
	private SpdxUtils utils = new SpdxUtils();

	private static ApiInterface callout = new CalloutApi();
	final static Logger logger = Logger.getLogger(FileStorageImpl.class);

	@Override
	public void store(MultipartFile file) throws IOException {
		InputStream stream = file.getInputStream();
		File drictory = new File(rootPathFile);
		if (!drictory.exists())
			Files.createDirectory(rootLocation);
		else if (drictory.exists() && !drictory.isDirectory()) {
			drictory.delete();
			Files.createDirectory(rootLocation);
		}
		try {
			Files.copy(stream, this.rootLocation.resolve(file.getOriginalFilename()),
					StandardCopyOption.REPLACE_EXISTING);
			stream.close();
		} catch (Exception e) {
			stream.close();
			logger.error(e);
			throw new RuntimeException("FAIL! -> message = " + e.getMessage());
		}
	}

	@Override
	public void init() {
		try {
			File drictory = new File(rootPathFile);
			if (!drictory.exists())
				Files.createDirectory(rootLocation);
			else if (drictory.exists() && !drictory.isDirectory()) {
				drictory.delete();
				Files.createDirectory(rootLocation);
			}
		} catch (IOException e) {
			logger.error(e);
			throw new RuntimeException("Could not initialize storage!");
		}
	}

	@Override
	public OutputModel readContentFile(String fileName) throws Exception {
		File file = new File(rootPathFile + fileName);
		List<String> messages = new ArrayList<String>();
		if (!fileName.toLowerCase().contains(".spdx")) {
			messages.add("File import not SPDX file, please choose other files");
			if (file.exists())
				file.delete();
			return new OutputModel(fileName, messages);
		}
		
		ResponseToken token = callout.getToken();
		if (token.getAccess_token() == null)
			messages.add("Authentication errors");
		
		else {
			HeaderFileNumber header = new HeaderFileNumber();
			header = utils.getHeaderLineIndex(rootPathFile + fileName);
			
			// Get license
			List<SpdxLicense> lstLicense = utils.importLicense(rootPathFile + fileName, header.getSizeOfFile(), header.getLicenseInformation());
			
			// Get component & release
			SpdxComponent component = new SpdxComponent();
			SpdxRelease release = new SpdxRelease();
			
			utils.importComponentAddReLease(rootPathFile + fileName, component, release);
			
			// Set licenseIds for component
			Set<String> licenseIds = new HashSet<String>();
			for (SpdxLicense license : lstLicense) {
				licenseIds.add(license.getShortName()); 
			}
			
			release.setLicenseIds(licenseIds);
			
			// Post component
			ResponseApiComponent componentResponse = postComponentApi(component, fileName, messages, token);
			
			// Post release
			postReleaseApi(release, fileName, messages, componentResponse, token);
			
			// Post license
			postLicenseApi(fileName, messages, componentResponse, token, lstLicense);
			
			if (file.exists())
				file.delete();
		}
		return new OutputModel(fileName, messages);
	}

	private ResponseApiComponent postComponentApi(SpdxComponent component, String fileName, List<String> messages, ResponseToken token)
			throws Exception {
		
		String compId = getComponentIdByComponentName(component.getName(), token);
		ApiResult result = new ApiResult();
		try {
			
			if (compId != null) {
				// Not update package license concluded
				component.setPackageLicenseConcluded(null);
				
				String requestBody = mapper.writeValueAsString(component);
				result = callout.updateComponent(compId, requestBody, token);
				if (result.getResponseCode() == 200 || result.getResponseCode() == 201) {
					messages.add("Update component " + component.getName() + " success");
					return new ResponseApiComponent(true, compId, component.getName());
				} else {
					messages.add((new JSONObject(result.getBody())).getString("message"));
					return new ResponseApiComponent(false, null, component.getName());
				}
			} else {
				String requestBody = mapper.writeValueAsString(component);
				result = callout.postComponent(requestBody, token);
				if (result.getResponseCode() == 200 || result.getResponseCode() == 201) {
					messages.add("Import new component success");
					String componentId = (new JSONObject(result.getBody())).getJSONObject("_links")
							.getJSONObject("self").getString("href");
					return new ResponseApiComponent(true, componentId.substring(componentId.lastIndexOf("/") + 1),
							component.getName());
				} else {
					messages.add((new JSONObject(result.getBody())).getString("message"));
					return new ResponseApiComponent(false, null, component.getName());
				}
			}

		} catch (Exception e) {
			messages.add("Import component error");
			logger.error(e);
			return new ResponseApiComponent(false, null, component.getName());
		}
	}

	private void postLicenseApi(String fileName, List<String> messages, ResponseApiComponent responseComp,
			ResponseToken token, List<SpdxLicense> lstLicense) throws Exception {
		int countLicense = 0;
		if (!responseComp.getResponseFlg())
			messages.add("Not import License because Component import errors");
		else {
			if (lstLicense.size() == 0 || lstLicense == null)
				messages.add("Not License is imported because License is empty");
			else {
				for (SpdxLicense license : lstLicense) {
					String requestBody = mapper.writeValueAsString(license);
					try {
						ApiResult res = callout.postLicense(requestBody, token);
						if (res.getResponseCode() == 200 || res.getResponseCode() == 201)
							countLicense++;
					} catch (Exception e) {
						logger.error(e);
						e.printStackTrace();
					}
				}
				messages.add("Import success " + countLicense + " licenses");
			}
		}
	}

	private void postReleaseApi(SpdxRelease release, String fileName, List<String> messages, ResponseApiComponent responseComp,
			ResponseToken token) throws Exception {
		if (!responseComp.getResponseFlg())
			messages.add("Not import Release because Component import errors");
		else {
			// Get component info
			String releaseId = getReleaseByNameAndVersion(responseComp.getComponentId(), responseComp.getComponentName(), release.getPackageVersion().trim(), token);
			ApiResult res;
			if (releaseId != null) {
				// Update release
				String requestBody = mapper.writeValueAsString(release);
				res = callout.updateRelease(releaseId, requestBody, token);
				
			} else {
				// Create release
				release.setComponentId(responseComp.getComponentId());
				release.setVersion(release.getPackageVersion().trim());
				release.setName(responseComp.getComponentName());
				String requestBody = mapper.writeValueAsString(release);
				res = callout.postRelease(requestBody, token);
			}
			
			if (res != null && (res.getResponseCode() == 200 || res.getResponseCode() == 201))
				messages.add("Import success releases");
		}
	}

	private String getComponentIdByComponentName(String name, ResponseToken token) throws Exception {
		ApiResult res = callout.getComponentIdByName(name, token);
		if (res.getResponseCode() != 200 && res.getResponseCode() != 201)
			return null;
		if (res.getBody().equals("{ }"))
			return null;
		JSONArray component = (new JSONObject(res.getBody())).getJSONObject("_embedded")
				.getJSONArray("sw360:components");
		for (Object object : component) {
			if (object instanceof JSONObject) {
				if (!((JSONObject) object).getString("name").equals(name))
					return null;
				else {
					String link = ((JSONObject) object).getJSONObject("_links").getJSONObject("self").getString("href");
					return link.substring(link.lastIndexOf("/") + 1);
				}
			}
		}
		return null;
	}
	
	private String getReleaseByNameAndVersion(String componentId, String name, String version, ResponseToken token) throws Exception {
		ApiResult res = callout.getReleaseByNameAndVersion(componentId, name, version, token);
		
		if (res.getResponseCode() != 200 && res.getResponseCode() != 201)
			return null;
		if (res.getBody().equals("{ }"))
			return null;
		
		JSONArray release = new JSONArray();
		if (res.getBody().contains("sw360:releases")) {
			release = (new JSONObject(res.getBody())).getJSONObject("_embedded")
					.getJSONArray("sw360:releases");
		}
		
		for (Object object : release) {
			if (object instanceof JSONObject) {
				if (((JSONObject) object).getString("name").equals(name) && ((JSONObject) object).getString("version").equals(version)) {
					String link = ((JSONObject) object).getJSONObject("_links").getJSONObject("self").getString("href");
					return link.substring(link.lastIndexOf("/") + 1);
				}
			}
		}
		return null;
	}
}
