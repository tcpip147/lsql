package com.tcpip147.lsql.springboot.example.model;

public class User {

	private int id;
	private String name;
	private int departmentId;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(int departmentId) {
		this.departmentId = departmentId;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", name=" + name + ", departmentId=" + departmentId + "]";
	}

}
