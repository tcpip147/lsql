package com.tcpip147.lsql.eclipse.ui.lsp;

import java.util.List;

public class TokenizeSqlMessage {
	private int requestId;
	private List<RawToken> tokens;

	public int getRequestId() {
		return requestId;
	}

	public void setRequestId(int requestId) {
		this.requestId = requestId;
	}

	public List<RawToken> getTokens() {
		return tokens;
	}

	public void setTokens(List<RawToken> tokens) {
		this.tokens = tokens;
	}

	public class RawToken {
		public int type;
		public String text;
		public int start;
		public int end;
	}
}
