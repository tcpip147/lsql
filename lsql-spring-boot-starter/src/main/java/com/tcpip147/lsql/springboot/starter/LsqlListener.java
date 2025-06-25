package com.tcpip147.lsql.springboot.starter;

import java.util.LinkedList;
import java.util.List;

import com.tcpip147.lsql.springboot.starter.LsqlParser.IdAttributeContext;
import com.tcpip147.lsql.springboot.starter.LsqlParser.OptionAttributeContext;
import com.tcpip147.lsql.springboot.starter.LsqlParser.SqlContext;
import com.tcpip147.lsql.springboot.starter.LsqlParser.StatementContext;

public class LsqlListener extends LsqlParserBaseListener {

	private List<LsqlQuery> queryList = new LinkedList<>();
	private String id;
	private String description;
	private String sql;

	@Override
	public void enterStatement(StatementContext ctx) {
		id = "";
		description = "";
		sql = "";
	}

	@Override
	public void enterIdAttribute(IdAttributeContext ctx) {
		id = ctx.Value().getText();
	}

	@Override
	public void enterOptionAttribute(OptionAttributeContext ctx) {
		if (ctx.Value() != null) {
			description = ctx.Value().getText();
		}
	}

	@Override
	public void enterSql(SqlContext ctx) {
		if (ctx.getText() != null) {
			sql = ctx.getText();
		}
	}

	@Override
	public void exitStatement(StatementContext ctx) {
		queryList.add(new LsqlQuery(id, description, sql));
	}

	public List<LsqlQuery> getQueryList() {
		return queryList;
	}
}
