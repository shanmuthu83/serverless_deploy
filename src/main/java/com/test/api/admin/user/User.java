package com.test.api.admin.user;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.test.api.admin.user.req.data.UserRequest;

public class User implements RequestStreamHandler {

	@Override
	public void handleRequest(InputStream input, OutputStream output, Context context) throws IOException {
		LambdaLogger logger = context.getLogger();
		ObjectMapper mapper = new ObjectMapper();

		UserRequest request = mapper.readValue(input, UserRequest.class);

		Object data = null;

		if (request.getField().equalsIgnoreCase("eid")) {
			data = getAllUsers();
		} else if (request.getField().equalsIgnoreCase("all")) {
			data = getAllUsers();
		}

		mapper.writeValue(output, data);
	}

	private Map<String, Object> getAllUsers() {
		Map<String, Object> users = new HashMap<String, Object>();
		users.put("id", "123");
		users.put("firstName", "MyUser");
		users.put("lastName", "SystemAdmin");
		users.put("userName", "sysadmin@test.com");
		return users;
	}
}
