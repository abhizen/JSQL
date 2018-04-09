package com.dbtools.jsonsql;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dbtools.jsonsql.operators.ScanOperator;
import com.dbtools.jsonsql.operators.SelectionOperator;
import com.dbtools.jsonsql.operators.UnNestOperator;
import com.dbtools.jsonsql.queryprocessor.QueryParser;
import com.google.gson.Gson;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;

public class QueryExecutor {

	public void execute() {

		//List<String> jsonDocuments = scanRecords("/Users/abhinisinha/Documents/PersonalProjects/input.txt");
		List<String> jsonDocuments =  new ArrayList<String>();
		jsonDocuments.add("{a : {b :{d : [{p:{ a:10, b: 67}}, {q: 20}], e: 2}, c:2}}");
		
		String query = "Select * from json where e =  2";
		QueryParser parser = new QueryParser();
		try {
			parser.parseQuery(query);
		} catch (JSQLParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		ScanOperator scanOperator = new ScanOperator("/Users/abhinisinha/Documents/PersonalProjects/input.txt");
		UnNestOperator unnest1 = new UnNestOperator("b", scanOperator);
		UnNestOperator unnest = new UnNestOperator("d",unnest1);
		System.out.println(unnest.getTuple());
		UnNestOperator unnest2 = new UnNestOperator("a",unnest);
		System.out.println(unnest2.getTuple());
		SelectionOperator selectionOperator = new SelectionOperator(parser.getWhereClause(), unnest);
		System.out.println(selectionOperator.getTuple());
	}

	public static void main(String[] args) {
		QueryExecutor executor = new QueryExecutor();
		executor.execute();
	}
}
