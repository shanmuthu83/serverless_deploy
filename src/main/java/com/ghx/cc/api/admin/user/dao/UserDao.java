package com.ghx.cc.api.admin.user.dao;

import java.util.List;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.HttpClientBuilder;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ghx.cc.api.config.PropertyLoader;
import com.google.common.collect.Maps;

public class UserDao {

	static final int HTTP_SOCKET_TIMEOUT = 60000;
	static final String BASIC = "Basic ";
	static final String SEARCH = "search";
	static final String PAGE = "page";
    static final String ITEMS = "items";
    
	static final String ENDPOINT_EID = "/rest/cm/organizations/info/";
    static final String ENDPOINT_ALL_USERS = "/rest/parties/agents/lookups";
    

	private PropertyLoader propertyLoader;
	private Context context;
	private LambdaLogger logger;
	private String stage;

	public UserDao(Context context, String stage) {
		this.context = context;
		this.stage = stage;
		this.logger = this.context.getLogger();
		this.propertyLoader = new PropertyLoader();
	}

	public Map getUserEid(String orgId) {
		String eidUrl = propertyLoader.getProperty(stage + ".idp.corex.endpoint") + ENDPOINT_EID;
		String username = propertyLoader.getProperty(stage + ".idp.username");
		String password = propertyLoader.getProperty(stage + ".idp.password");
		RestTemplate restTemplate = getRestTemplate(stage);
		HttpHeaders headers = getHttpHeaders(username, password);

		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(eidUrl).path(orgId);

		try {
			ResponseEntity<String> responseMap = restTemplate.exchange(builder.build().encode().toUri(), HttpMethod.GET,
					new HttpEntity<>(headers), String.class);
			if (responseMap.getStatusCode() == HttpStatus.OK) {
				 ObjectMapper mapper = new ObjectMapper();
				 mapper.setSerializationInclusion(Include.NON_NULL);
				return mapper.readValue(responseMap.getBody(), Map.class);
			}
		} catch (Exception e) {
		}
		return null;
	}

	public Map getAllUsers(String searchText, int pageNum, int pageSize) {

		String allUsersUrl = propertyLoader.getProperty(stage + ".idp.corex.endpoint") + ENDPOINT_ALL_USERS;
		String username = propertyLoader.getProperty(stage + ".idp.username");
		String password = propertyLoader.getProperty(stage + ".idp.password");
		RestTemplate restTemplate = getRestTemplate(stage);
		HttpHeaders headers = getHttpHeaders(username, password);

		  UriComponentsBuilder builder =
	                UriComponentsBuilder.fromHttpUrl(allUsersUrl)
	                .queryParam(SEARCH, searchText).queryParam(PAGE,pageNum);

		try {
			ResponseEntity<String> responseMap = restTemplate.exchange(builder.build().encode().toUri(), HttpMethod.GET,
					new HttpEntity<>(headers), String.class);
			if (responseMap.getStatusCode() == HttpStatus.OK) {
				 Map<String,Object> users = Maps.newHashMap();				 
				 ObjectMapper mapper = new ObjectMapper();
				 mapper.setSerializationInclusion(Include.NON_NULL);
				 users.put(ITEMS, mapper.readValue(responseMap.getBody(), List.class));	            
				return users;
			}
		} catch (Exception e) {
		}
		return null;
	}

	private HttpHeaders getHttpHeaders(String username, String password) {
		HttpHeaders headers = new HttpHeaders();

		StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
		encryptor.setPassword("ClinicalConneXion");
		String decryptedPassword = encryptor.decrypt(password);

		String userPassword = username + ":" + decryptedPassword;
		String encodedBytes = new String(Base64.encodeBase64(userPassword.getBytes()));

		headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
		headers.set(HttpHeaders.AUTHORIZATION, BASIC + encodedBytes);

		return headers;
	}

	private RestTemplate getRestTemplate(String stage) {
		if (stage.equalsIgnoreCase("local")) {
			stage = "dev";
		}
		String certs = stage.toUpperCase() + "_cacerts";
		System.setProperty("javax.net.ssl.trustStore", certs);
		System.setProperty("javax.net.ssl.trustStoreType", "JKS");
		System.setProperty("javax.net.ssl.trustStorePassword", "changeit");
		HttpClient httpClient = HttpClientBuilder.create().useSystemProperties()
				.setSSLHostnameVerifier(HOSTNAME_VERIFIER)
				.setDefaultRequestConfig(RequestConfig.custom().setSocketTimeout(HTTP_SOCKET_TIMEOUT).build()).build();

		HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);
		return new RestTemplate(factory);
	}

	private HostnameVerifier HOSTNAME_VERIFIER = new HostnameVerifier() {
		public boolean verify(String urlHostName, SSLSession session) {
			return true;
		}
	};

}
