package com.tcpip147.lsql.springboot.starter;

public class LsqlQuery {

	private String id;
	private String description;
	private String sql;

	public LsqlQuery(String id, String description, String sql) {
		this.id = id;
		this.description = description;
		this.sql = sql;
	}

	public String getId() {
		return id;
	}

	public String getDescription() {
		return description;
	}

	public String getSql() {
		return sql;
	}

	@Override
	public String toString() {
		return "@id " + id + "\n" + "@description " + description + "\n" + "\n" + sql;
	}
}
