package com.tcpip147.lsql.springboot.starter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class LsqlQueryDaoTests {

	@Autowired
	private LsqlQueryDao dao;

	@Test
	void testSelectListToRowMap() {
		List<RowMap> result = dao.selectList("test.selectUser", Map.of("ID", "1"));
		assertEquals("Alice", result.get(0).getString("NAME"));
	}

	@Test
	void testSelectListToUser() {
		List<User> result = dao.selectList("test.selectUser", Map.of("ID", "2"), User.class);
		assertEquals("Bob", result.get(0).getName());
	}
}
