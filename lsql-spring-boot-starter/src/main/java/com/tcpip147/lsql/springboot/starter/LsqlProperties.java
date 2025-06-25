package com.tcpip147.lsql.springboot.starter;

import java.util.Arrays;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "lsql")
public class LsqlProperties {

	private boolean watch;
	private List<String> paths = Arrays.asList(new String[] { "/" });
	private String mapCaseSensitive = "uppercase";

	public boolean isWatch() {
		return watch;
	}

	public void setWatch(boolean watch) {
		this.watch = watch;
	}

	public List<String> getPaths() {
		return paths;
	}

	public void setPaths(List<String> paths) {
		this.paths = paths;
	}

	public String getMapCaseSensitive() {
		return mapCaseSensitive;
	}

	public void setMapCaseSensitive(String mapCaseSensitive) {
		this.mapCaseSensitive = mapCaseSensitive;
	}

}
