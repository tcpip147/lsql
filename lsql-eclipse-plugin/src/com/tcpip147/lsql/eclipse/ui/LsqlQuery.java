package com.tcpip147.lsql.eclipse.ui;

import com.tcpip147.lsql.eclipse.ui.control.SqlEditor;

public class LsqlQuery {

	private String id;
	private String description;
	private String sql;
	private SqlEditor editor;

	protected LsqlQuery clone() {
		LsqlQuery query = new LsqlQuery();
		query.id = id;
		query.description = description;
		query.sql = sql;
		return query;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public SqlEditor getEditor() {
		return editor;
	}

	public void setEditor(SqlEditor editor) {
		this.editor = editor;
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	@Override
	public String toString() {
		return "LsqlQuery [id=" + id + "]";
	}
}
