package com.ghx.cc.api.admin.user;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.codehaus.jackson.map.ObjectMapper;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.ghx.cc.api.admin.user.dao.UserDao;
import com.ghx.cc.api.admin.user.req.data.UserRequest;

public class User implements RequestStreamHandler {

	@Override
	public void handleRequest(InputStream input, OutputStream output, Context context) throws IOException {
		LambdaLogger logger = context.getLogger();
		ObjectMapper mapper = new ObjectMapper();

		UserRequest request = mapper.readValue(input, UserRequest.class);

		UserDao dao = new UserDao(context, request.getStage());
		Object data = null;
		
		if (request.getField().equalsIgnoreCase("eid")) {
			data = dao.getUserEid(request.getOrgId());
		} else if (request.getField().equalsIgnoreCase("all")) {
			data = dao.getAllUsers(request.getSearchText(), request.getPageNum(), request.getPageSize());
		}
		
		mapper.writeValue(output, data);
	}
}
