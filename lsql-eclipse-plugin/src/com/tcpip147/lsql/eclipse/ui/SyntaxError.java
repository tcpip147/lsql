package com.tcpip147.lsql.eclipse.ui;

public class SyntaxError {
	private String message;
	private int pos;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getPos() {
		return pos;
	}

	public void setPos(int pos) {
		this.pos = pos;
	}

}
