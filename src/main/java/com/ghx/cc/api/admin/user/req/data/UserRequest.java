package com.ghx.cc.api.admin.user.req.data;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class UserRequest {

	private String stage;
	private String orgId;
	private String field;
	private String searchText;
	private int pageNum;
	private int pageSize;
}
