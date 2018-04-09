package com.dbtools.jsonsql.operators;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import net.sf.jsqlparser.expression.Expression;

public class UnNestOperator implements Operator {
	private String unnestKey = null;
	private Operator operator = null;

	public UnNestOperator(String unnestKey, Operator operator) {
		this.unnestKey = unnestKey;
		this.operator = operator;
	}

	public Map<String, Object> getTuple() {
		Map<String, Object> resultTuple = operator.getTuple();
		traverse(resultTuple, this.unnestKey);
		return resultTuple;
	}

	/** Change this to non recursive */
	// private void unnest(Map<String, Object> jsonDocument, Map<String, Object>
	// outputResults, String newKey) {
	//
	// for (String key : jsonDocument.keySet()) {
	// Object value = jsonDocument.get(key);
	// String documentKey = newKey.length() > 0 ? newKey.concat(".").concat(key)
	// : key;
	// if (value instanceof List) {
	// for (Integer index = 0; index < ((List) value).size(); index++) {
	// if (((List) value).get(index) instanceof Map) {
	// Map<String, Object> document = (Map<String, Object>) ((List)
	// value).get(index);
	// String objectKey = documentKey.concat(".").concat(index.toString());
	// unnest(document, outputResults, objectKey);
	// } else {
	// outputResults.put(documentKey, value);
	// }
	// }
	// } else if (value instanceof Map) {
	// Map<String, Object> document = (Map<String, Object>) value;
	// unnest(document, outputResults, documentKey);
	// } else {
	// outputResults.put(documentKey, value);
	// }
	// }
	// }

	private void traverse(Map<String, Object> jsonDocument, String unnestKey) {
		Queue<Object> queue = new LinkedList<Object>();
		Object value = null;

		queue.add(jsonDocument);
		while (!queue.isEmpty()) {
			value = queue.remove();
			if (value instanceof List) {
				List<Object> childNodes = new ArrayList<Object>();
				List<Integer> indexOfObjectsToBeRemoved = new ArrayList<Integer>();

				for (Object element : (List<Object>) value) {
					/*
					 * Check if child element is map or list. If map, if it
					 * contains unnestKey Eg: a: [{ b:1}, {b:2}] - > [{a :
					 * {b:1}}, {a : {b:2}}]
					 */

					if (element instanceof Map && ((Map<String, Object>) element).get(unnestKey) != null) {
						Object flattenedObject = unnest((Map<String, Object>) element, unnestKey);
						childNodes.add(flattenedObject);
						indexOfObjectsToBeRemoved.add(((List) value).indexOf(element));
					} else if (element instanceof List) {
						queue.add(element);
					}
				}

				for (int index = indexOfObjectsToBeRemoved.size() - 1; index >= 0; index--) {
					((List) value).remove((int) index);
				}

				if (childNodes.size() > 0) {
					((List) value).addAll(childNodes);
				}

			} else if (value instanceof Map) {

				if (((Map<String, Object>) value).get(unnestKey) != null) {
					Object nestedValue = ((Map<String, Object>) value).get(unnestKey);
					((Map<String, Object>) value).remove(unnestKey);
					if (nestedValue instanceof List) {
						
						((Map<String, Object>) value).put("*", nestedValue);
						
					} else if (nestedValue instanceof Map) {

						((Map<String, Object>) value).putAll((Map<String, Object>) nestedValue);
					}
				} else {
					for (String childKey : ((Map<String, Object>) value).keySet()) {
						/*
						 * Check if child element is map or list. If map, if it
						 * contains unnestKey
						 */
						Object element = ((Map<String, Object>) value).get(childKey);

						if (element instanceof Map && ((Map<String, Object>) element).get(unnestKey) != null) {

							Object flattenedObject = unnest((Map<String, Object>) element, unnestKey);

							// merge with remaining keys of map
							((Map<String, Object>) element).remove(unnestKey);

							((Map<String, Object>) value).put(childKey, flattenedObject);
						} else if (element instanceof Map || element instanceof List)
							queue.add(element);
					}
				}

			}
		}

	}

	private Object unnest(Map<String, Object> jsonDocument, String unnestKey) {

		if (jsonDocument.keySet().size() == 1)
			return jsonDocument.get(unnestKey);

		Object value = jsonDocument.get(unnestKey);

		if (value instanceof List) {
			Map<String, Object> map = null;
			List<Object> list = new ArrayList<Object>();

			for (Object element : (List<Object>) value) {
				if (element instanceof Map) {
					if (map == null) {
						map = new HashMap<String, Object>();

						for (String key : ((Map<String, Object>) jsonDocument).keySet()) {
							if (!key.equals(unnestKey))
								map.put(key, ((Map<String, Object>) jsonDocument).get(key));
						}

						map.putAll((Map<String, Object>) element);
						list.add(map);
						map = null;
					}
				} else {
					map = new HashMap<String, Object>();
					map.putAll(jsonDocument);
					return map;
				}
			}

			return list;
		} else if (value instanceof Map) {

			Map<String, Object> map = new HashMap<String, Object>();
			map.putAll((Map<String, Object>) value);

			for (String key : ((Map<String, Object>) jsonDocument).keySet()) {
				if (!key.equals(unnestKey))
					map.put(key, ((Map<String, Object>) jsonDocument).get(key));
			}

			return map;
		} else {
			Map<String, Object> map = new HashMap<String, Object>();
			map.putAll((Map<String, Object>) jsonDocument);
			return map;
		}

	}

}
