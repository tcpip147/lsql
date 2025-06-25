package com.tcpip147.lsql.eclipse.ui.lsp;

import java.util.List;

public class ParseLsqlMessage {
	private int requestId;
	private List<RawQuery> queryList;
	private List<RawError> errors;

	public int getRequestId() {
		return requestId;
	}

	public void setRequestId(int requestId) {
		this.requestId = requestId;
	}

	public List<RawQuery> getQueryList() {
		return queryList;
	}

	public void setQueryList(List<RawQuery> queryList) {
		this.queryList = queryList;
	}

	public List<RawError> getErrors() {
		return errors;
	}

	public void setErrors(List<RawError> errors) {
		this.errors = errors;
	}

	public class RawQuery {
		public String id;
		public String description;
		public String sql;
		public int start;
	}

	public class RawError {
		public String message;
		public int pos;
	}
}
