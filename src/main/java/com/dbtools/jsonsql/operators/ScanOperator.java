package com.dbtools.jsonsql.operators;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;

public class ScanOperator implements Operator{
	private String filePath = "";
	
	public ScanOperator(String filePath) {
		this.filePath = filePath;
	}

	public Map<String, Object> getTuple() {
		List<String> tuples = scanRecords(filePath);
		Map<String, Object> map = new HashMap<String, Object>();
		Gson gson = new Gson();
		map = (Map<String, Object>) gson.fromJson(tuples.get(0),  map.getClass());
		return map;
	}
	
	private List<String> scanRecords(String filePath) {
		List<String> documents = new ArrayList<String>();
		BufferedReader bufferedReader = null;

		try {
			FileReader reader = new FileReader(filePath);
			bufferedReader = new BufferedReader(reader);
			String document = null;

			while ((document = bufferedReader.readLine()) != null) {
				documents.add(document);
			}
			bufferedReader.close();
		} catch (IOException exception) {
			exception.printStackTrace();

		}
		return documents;
	}

}
