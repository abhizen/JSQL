package com.dbtools.jsonsql;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dbtools.jsonsql.operators.SelectionOperator;
import com.dbtools.jsonsql.operators.UnNestOperator;
import com.dbtools.jsonsql.queryprocessor.QueryParser;
import com.google.gson.Gson;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;

public class QueryExecutor {

	public void execute() {
		String jsonDocument = "{a : [{b : 1000,c : 2 }, {b : 100,c : 2 }]}";

		List<String> jsonDocuments = scanRecords("/Users/abhinisinha/Documents/PersonalProjects/input.txt");
		String query = "Select * from json where zip =  80017";
		QueryParser parser = new QueryParser();
		try {
			parser.parseQuery(query);
		} catch (JSQLParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		UnNestOperator unnest = new UnNestOperator();
		Gson gson = new Gson();
		Map<String, Object> map = new HashMap<String, Object>();

		for (String document : jsonDocuments) {
			map = (Map<String, Object>) gson.fromJson(document, map.getClass());
			SelectionOperator selectionOperator = new SelectionOperator(parser.getWhereClause(), unnest);
			map = selectionOperator.getTuple(map);
			System.out.println(map);
		}
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

	public static void main(String[] args) {
		QueryExecutor executor = new QueryExecutor();
		executor.execute();
	}
}
