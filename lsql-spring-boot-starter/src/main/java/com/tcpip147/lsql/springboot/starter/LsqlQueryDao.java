package com.tcpip147.lsql.springboot.starter;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

public class LsqlQueryDao {

	@Autowired
	private LsqlQueryProvider queryProvider;

	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	@Autowired
	private LsqlRowMapper rowMapper;

	@SuppressWarnings("unchecked")
	private SqlParameterSource castParameters(Object params) {
		if (params instanceof Map) {
			return new MapSqlParameterSource((Map<String, ?>) params);
		}
		return new BeanPropertySqlParameterSource(params);
	}

	public List<RowMap> selectList(String id) {
		return namedParameterJdbcTemplate.query(queryProvider.getQuery(id).getSql(), rowMapper);
	}

	public List<RowMap> selectList(String id, Object params) {
		return namedParameterJdbcTemplate.query(queryProvider.getQuery(id).getSql(), castParameters(params), rowMapper);
	}

	public <T> List<T> selectList(String id, Class<T> t) {
		return namedParameterJdbcTemplate.query(queryProvider.getQuery(id).getSql(), BeanPropertyRowMapper.newInstance(t));
	}

	public <T> List<T> selectList(String id, Object params, Class<T> t) {
		return namedParameterJdbcTemplate.query(queryProvider.getQuery(id).getSql(), castParameters(params), BeanPropertyRowMapper.newInstance(t));
	}

	public List<RowMap> selectListByStatement(String sql) {
		return namedParameterJdbcTemplate.query(sql, rowMapper);
	}

	public List<RowMap> selectListByStatement(String sql, Object params) {
		return namedParameterJdbcTemplate.query(sql, castParameters(params), rowMapper);
	}

	public <T> List<T> selectListByStatement(String sql, Class<T> t) {
		return namedParameterJdbcTemplate.query(sql, BeanPropertyRowMapper.newInstance(t));
	}

	public <T> List<T> selectListByStatement(String sql, Object params, Class<T> t) {
		return namedParameterJdbcTemplate.query(sql, castParameters(params), BeanPropertyRowMapper.newInstance(t));
	}

	public RowMap selectOne(String id) {
		List<RowMap> result = namedParameterJdbcTemplate.query(queryProvider.getQuery(id).getSql(), rowMapper);
		return result.size() > 0 ? result.get(0) : null;
	}

	public RowMap selectOne(String id, Object params) {
		List<RowMap> result = namedParameterJdbcTemplate.query(queryProvider.getQuery(id).getSql(), castParameters(params), rowMapper);
		return result.size() > 0 ? result.get(0) : null;
	}

	public <T> T selectOne(String id, Class<T> t) {
		List<T> result = namedParameterJdbcTemplate.query(queryProvider.getQuery(id).getSql(), BeanPropertyRowMapper.newInstance(t));
		return result.size() > 0 ? result.get(0) : null;
	}

	public <T> T selectOne(String id, Object params, Class<T> t) {
		List<T> result = namedParameterJdbcTemplate.query(queryProvider.getQuery(id).getSql(), castParameters(params), BeanPropertyRowMapper.newInstance(t));
		return result.size() > 0 ? result.get(0) : null;
	}

	public RowMap selectOneByStatement(String sql) {
		List<RowMap> result = namedParameterJdbcTemplate.query(sql, rowMapper);
		return result.size() > 0 ? result.get(0) : null;
	}

	public RowMap selectOneByStatement(String sql, Object params) {
		List<RowMap> result = namedParameterJdbcTemplate.query(sql, castParameters(params), rowMapper);
		return result.size() > 0 ? result.get(0) : null;
	}

	public <T> T selectOneByStatement(String sql, Class<T> t) {
		List<T> result = namedParameterJdbcTemplate.query(sql, BeanPropertyRowMapper.newInstance(t));
		return result.size() > 0 ? result.get(0) : null;
	}

	public <T> T selectOneByStatement(String sql, Object params, Class<T> t) {
		List<T> result = namedParameterJdbcTemplate.query(sql, castParameters(params), BeanPropertyRowMapper.newInstance(t));
		return result.size() > 0 ? result.get(0) : null;
	}
}
