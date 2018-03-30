package com.dbtools.jsonsql.operators;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dbtools.jsonsql.jsonparser.Node;

import net.sf.jsqlparser.expression.Expression;

public class UnNestOperator implements Operator {

	public Map<String, Object> getTuple(Map<String, Object> jsonDocument) {
		Map<String, Object> flattenedJson = new HashMap<String, Object>();
		unnest(jsonDocument, flattenedJson, "");
		return flattenedJson;
	}
	
	private void unnest(Map<String, Object> jsonDocument, Map<String,Object> outputResults, String newKey) {
		
		for(String key : jsonDocument.keySet()) {
			Object value = jsonDocument.get(key);
			String documentKey = newKey.length()>0?newKey.concat(".").concat(key):key;
			if (value instanceof List) {
				for(Integer index=0;index<((List)value).size();index++) {
					if (((List) value).get(index) instanceof Map) {
						Map<String, Object> document = (Map<String, Object>) ((List) value).get(index);
						String objectKey = documentKey.concat(".").concat(index.toString());
						unnest(document, outputResults, objectKey);
					}
					else {
						outputResults.put(documentKey, value);
					}
				}
		   }
			else if (value instanceof Map) {
				Map<String, Object> document = (Map<String, Object>)value;
				unnest(document, outputResults, documentKey);
			}
			else {
				outputResults.put(documentKey, value);
			}
		}
	}
}
