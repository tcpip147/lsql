package com.tcpip147.lsql.springboot.example;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tcpip147.lsql.springboot.example.model.Department;
import com.tcpip147.lsql.springboot.starter.LsqlQueryDao;
import com.tcpip147.lsql.springboot.starter.RowMap;

@RestController
public class ExampleController {

	@Autowired
	private LsqlQueryDao dao;

	@GetMapping("/")
	public String index() {
		Department department = dao.selectOne("department.selectById", Map.of("ID", 3), Department.class);
		List<RowMap> users = dao.selectList("user.selectInDepartment");
		return department.toString() + "<br>" + users.toString();
	}
}
