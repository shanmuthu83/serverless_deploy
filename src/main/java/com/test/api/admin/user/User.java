package com.test.api.admin.user;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;

public class User implements RequestStreamHandler {

	@Override
	public void handleRequest(InputStream input, OutputStream output, Context context) throws IOException {

		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> data = getAllUsers();

		mapper.writeValue(output, data);
	}

	private Map<String, Object> getAllUsers() {
		Map<String, Object> users = new HashMap<String, Object>();
		users.put("id", "123");
		users.put("firstName", "My User");
		users.put("lastName", "My Admin");
		users.put("userName", "myadmin@test.com");
		return users;
	}
}
