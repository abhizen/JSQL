package com.dbtools.jsonsql.operators;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NestOperator implements Operator {
	private List<String> toBeNested;
	private String nestedKey;
	private Operator operator;
	public NestOperator(List<String> toBeNested, String nestedKey, Operator operator) {
		this.toBeNested = toBeNested;
		this.nestedKey = nestedKey;
		this.operator = operator;
	}

	public Map<String, Object> getTuple() {
		Map<String, Object> tuple = this.operator.getTuple();
		boolean foundAll = true;
		for(String key : toBeNested) {
			if (!tuple.containsKey(key)) { 
				foundAll = false;
				break;
			}
		}
		
		if (foundAll) {
			nestKeys(tuple);
		}
		
		return tuple;
	}
	
	public void nestKeys(Map<String, Object> tuple) {
		Map<String, Object> map = new HashMap<String, Object>();
		for(String key : toBeNested) {
			Object value = tuple.remove(key);
			map.put(key, value);
		}
		
		tuple.put(this.nestedKey, map);
	}

}
