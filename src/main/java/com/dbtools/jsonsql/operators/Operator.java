package com.dbtools.jsonsql.operators;

import java.util.Map;

public interface Operator {
	
	public Map<String,Object> getTuple(Map<String,Object> jsonDocument);
}
