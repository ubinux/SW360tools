package com.spdx.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Base64;
import java.util.LinkedHashSet;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;

import com.spdx.enums.ContentType;
import com.spdx.enums.HttpMethodEnums;
import com.spdx.model.ApiResult;

public class HttpClientUtils implements Closeable {
	private HttpURLConnection conn = null;
	private URL url = null;
	private String token_type = null;
	private String token = null;
	private Boolean basicAuthen = false;
	final static Logger logger = Logger.getLogger(HttpClientUtils.class);

	public HttpClientUtils() {

	}
	
	public HttpClientUtils(String token_type, String token, Boolean basicAuthen) {
		this.token_type = token_type;
		this.token = token;
		this.basicAuthen = basicAuthen;
	}


	public ApiResult postRequest(String urlRequest, String requestBody, ContentType contentType) throws IOException {
		url = new URL(urlRequest);
		conn = initHttpURLConnection(urlRequest, HttpMethodEnums.POST, contentType);
		if (this.token != null && this.token_type != null)
			conn = setToken(conn, this.token_type, this.token);
		if (basicAuthen)
			conn = setAuthenication(conn);
		DataOutputStream os = new DataOutputStream(conn.getOutputStream());
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
		writer.write(requestBody);
		writer.flush();
		writer.close();
		return processApiResult(conn);
	}
	
	public ApiResult getRequest(String urlRequest) throws IOException {
		url = new URL(urlRequest);
		conn = initHttpURLConnection(urlRequest, HttpMethodEnums.GET, ContentType.NOTSET);
		if (this.token != null && this.token_type != null)
			conn = setToken(conn, this.token_type, this.token);
		if (basicAuthen)
			conn = setAuthenication(conn);
		return processApiResult(conn);
	}
	
	public ApiResult updateRequest(String urlRequest, String requestBody) throws IOException {
		url = new URL(urlRequest);
		conn = initHttpURLConnection(urlRequest, HttpMethodEnums.PATCH, ContentType.JSON);
		if (this.token != null && this.token_type != null)
			conn = setToken(conn, this.token_type, this.token);
		if (basicAuthen)
			conn = setAuthenication(conn);
		OutputStream os = conn.getOutputStream();
		os.write(requestBody.getBytes());
		os.flush();
		os.close();
		return processApiResult(conn);
	}
	
	public ApiResult deleteRequest(String urlRequest) throws IOException {
		url = new URL(urlRequest);
		conn = initHttpURLConnection(urlRequest, HttpMethodEnums.DELETE, ContentType.NOTSET);
		if (this.token != null && this.token_type != null)
			conn = setToken(conn, this.token_type, this.token);
		if (basicAuthen)
			conn = setAuthenication(conn);
		return processApiResult(conn);
	}

	private HttpURLConnection initHttpURLConnection(String subURL, HttpMethodEnums method, ContentType contentType) throws MalformedURLException {

		try {
			allowMethods("PATCH");
			url = consolidateURL(subURL);
			conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod(method.value());
			conn.setRequestProperty("Accept-Charset", "utf-8");
			if(contentType != ContentType.NOTSET)
				conn.setRequestProperty("Content-Type", contentType.value());
			return conn;
		} catch (IOException e) {
			throw new MalformedURLException(e.getMessage());
		}

	}
	
	public HttpURLConnection setToken(HttpURLConnection conn,String token_type,String token) {
		String value = String.format("%s %s", token_type,token);
		conn.setRequestProperty("Authorization", value);
		return conn;
	}
	
	private HttpURLConnection setAuthenication(HttpURLConnection conn) throws IOException {
		InputStream input = HttpClientUtils.class.getClassLoader().getResourceAsStream("application.properties");
		Properties prop = new Properties();
		prop.load(input);

		if (input == null)
			throw new IOException();
		
		String usernameColonPassword = prop.getProperty("authen.user") + ":" + prop.getProperty("authen.pass");
		String basicAuthPayload = "Basic " + Base64.getEncoder().encodeToString(usernameColonPassword.getBytes());
		conn.addRequestProperty("Authorization", basicAuthPayload);
		input.close();
		return conn;
	}
	
	private static String[] processBodyContent(HttpURLConnection conn) throws IOException{
		String[] result = new String[2];
		BufferedReader br;
		try {
			long start = System.nanoTime();
			try {
				br = new BufferedReader(new InputStreamReader(conn.getInputStream(),"UTF-8"));
			} catch (Exception e) {
				br = new BufferedReader(new InputStreamReader(conn.getErrorStream(),"UTF-8"));
			}
			String output = "";
			String value;
			while ((value = br.readLine()) != null) {
				output += value;
			}
			br.close();
			
			long elapsed = System.nanoTime() - start;
			result[0] = output;
			result[1] = (elapsed / 1000) + "";
			return result;
		} catch (IOException e) {
			throw new IOException(e.getMessage());
		}
	}
	
	private static ApiResult processApiResult(HttpURLConnection conn) throws IOException{
		ApiResult result = new ApiResult();		   	  							
		try {
			result.setResponseCode(conn.getResponseCode());
			String[] responseData = processBodyContent(conn);
			result.setBody(responseData[0]);
			conn.disconnect();
			return result;
		} catch (IOException e) {
			throw new IOException(e.getMessage());
		}
		
	}

	private URL consolidateURL(String subURL) throws MalformedURLException {
		try {
			return new URL(subURL);
		} catch (MalformedURLException e) {
			throw new MalformedURLException(e.getMessage());
		}
	}
	
	 private static void allowMethods(String... methods) {
	        try {
	            Field methodsField = HttpURLConnection.class.getDeclaredField("methods");

	            Field modifiersField = Field.class.getDeclaredField("modifiers");
	            modifiersField.setAccessible(true);
	            modifiersField.setInt(methodsField, methodsField.getModifiers() & ~Modifier.FINAL);

	            methodsField.setAccessible(true);

	            String[] oldMethods = (String[]) methodsField.get(null);
	            Set<String> methodsSet = new LinkedHashSet<>(Arrays.asList(oldMethods));
	            methodsSet.addAll(Arrays.asList(methods));
	            String[] newMethods = methodsSet.toArray(new String[0]);

	            methodsField.set(null/*static field*/, newMethods);
	        } catch (NoSuchFieldException | IllegalAccessException e) {
	            throw new IllegalStateException(e);
	        }
	    }

	@Override
	public void close() {
		disconnect();
	}

	private void disconnect() {
		if (conn != null) {
			conn.disconnect();
			conn = null;
		}
	}

}