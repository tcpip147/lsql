package com.tcpip147.lsql.springboot.starter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

@AutoConfiguration
@ConditionalOnClass({ JdbcTemplate.class, NamedParameterJdbcTemplate.class })
@EnableConfigurationProperties(LsqlProperties.class)
public class LsqlAutoConfiguration {

	@Autowired
	private LsqlProperties properties;

	@Bean
	@ConditionalOnMissingBean
	public LsqlQueryProvider lsqlQueryProvider() {
		return new LsqlQueryProvider(properties);
	}

	@Bean
	@ConditionalOnMissingBean
	public LsqlQueryDao lsqlQueryDao() {
		return new LsqlQueryDao();
	}
	
	@Bean
	@ConditionalOnMissingBean
	public LsqlRowMapper lsqlRowMapper() {
		return new LsqlRowMapper(properties);
	}
}