package com.tcpip147.lsql.springboot.starter;

import java.util.LinkedHashMap;

public class RowMap extends LinkedHashMap<String, Object> {

	private static final long serialVersionUID = -7217723871139509438L;

	private boolean isLowerCase;

	public RowMap(boolean isLowerCase) {
		this.isLowerCase = isLowerCase;
	}

	@Override
	public Object get(Object key) {
		if (key instanceof String str) {
			if (isLowerCase) {
				return super.get(str.toLowerCase());
			} else {
				return super.get(str.toUpperCase());
			}
		}
		return super.get(key);
	}

	public Integer getIntOrNull(String key) {
		Object value = get(key);
		if (value == null) {
			return null;
		} else if (value instanceof Number number) {
			return number.intValue();
		}
		return null;
	}

	public int getInt(String key) {
		Object value = get(key);
		if (value == null) {
			return 0;
		} else if (value instanceof Number number) {
			return number.intValue();
		}
		return 0;
	}

	public Double getDoubleOrNull(String key) {
		Object value = get(key);
		if (value == null) {
			return null;
		} else if (value instanceof Number number) {
			return number.doubleValue();
		}
		return null;
	}

	public double getDouble(String key) {
		Object value = get(key);
		if (value == null) {
			return 0d;
		} else if (value instanceof Number number) {
			return number.doubleValue();
		}
		return 0d;
	}

	public Object getStringOrNull(String key) {
		Object value = get(key);
		if (value == null) {
			return null;
		} else if (value instanceof String str) {
			return str;
		}
		return null;
	}

	public Object getString(String key) {
		Object value = get(key);
		if (value == null) {
			return "";
		} else if (value instanceof String str) {
			return str;
		}
		return "";
	}
}
