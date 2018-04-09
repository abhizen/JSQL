package com.dbtools.jsonsql.operators;

import java.util.Map;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.GreaterThan;
import net.sf.jsqlparser.expression.operators.relational.GreaterThanEquals;
import net.sf.jsqlparser.expression.operators.relational.LikeExpression;
import net.sf.jsqlparser.expression.operators.relational.MinorThan;
import net.sf.jsqlparser.expression.operators.relational.MinorThanEquals;
import net.sf.jsqlparser.expression.operators.relational.NotEqualsTo;
import net.sf.jsqlparser.expression.BinaryExpression;

public class SelectionOperator implements Operator{
	Operator operator = null;
	Expression expression = null;
	
	
	public SelectionOperator(Expression expression, Operator operator) {
		this.operator = operator;
		this.expression = expression;
	}
	
	public Map<String,Object> getTuple() {
		Map<String,Object> resultTuple =operator.getTuple(); 
		
		if (getLeafValue(expression, resultTuple))
			return resultTuple;
		else 
			return null;
	}
	

	private boolean getLeafValue(Expression expression, Map<String,Object> jsonDocument) {
		Expression leftExpression = ((BinaryExpression)expression).getLeftExpression();
		Expression rightExpression = ((BinaryExpression)expression).getRightExpression();
		Object value = null;
		boolean result = false;
		
		for(String key : jsonDocument.keySet()) {
			value = getValueFromJsonDocument(key, leftExpression, jsonDocument);
			if (value!=null) {
				result = result || evaluateExpression(expression, value.toString(), rightExpression.toString());
			}
		}
		
		return result;
	}
	
	private Object getValueFromJsonDocument(String jsonKey, Expression expression, Map<String,Object> jsonDocument) {
		String[] keySet = jsonKey.split("[\\.]");
		if (keySet[keySet.length-1].equals(expression.toString())) {
			return jsonDocument.get(jsonKey);
		}
		
		return null;
			
	}
	
	private boolean evaluateExpression(Expression expression, String leftValue, String rightValue) {
		
		if (expression instanceof  EqualsTo) {
			if (!Boolean.parseBoolean(leftValue) ) {
				Double left = Double.parseDouble(leftValue);
				Double right = Double.parseDouble(rightValue);
			
				return left.equals(right);
			}
			else {
				Boolean left = Boolean.parseBoolean(leftValue);
				Boolean right = Boolean.parseBoolean(rightValue);
				
				return left.equals(right);
			}
		}
		else if (expression instanceof GreaterThan) {
			Double left = Double.parseDouble(leftValue);
			Double right = Double.parseDouble(rightValue);
			
			return left > right;
		}
		else if (expression instanceof  GreaterThanEquals) {
			Double left = Double.parseDouble(leftValue);
			Double right = Double.parseDouble(rightValue);
			
			return left >= right;
		}
		else if (expression instanceof LikeExpression) {
			
		}
		else if (expression instanceof MinorThan) {
			Double left = Double.parseDouble(leftValue);
			Double right = Double.parseDouble(rightValue);
			
			return left < right;
		}
		else if (expression instanceof MinorThanEquals) {
			Double left = Double.parseDouble(leftValue);
			Double right = Double.parseDouble(rightValue);
			
			return left <= right;
		}
		else if (expression instanceof NotEqualsTo) {
			if (!Boolean.parseBoolean(leftValue) ) {
				Double left = Double.parseDouble(leftValue);
				Double right = Double.parseDouble(rightValue);
			
				return !left.equals(right);
			}
			else {
				Boolean left = Boolean.parseBoolean(leftValue);
				Boolean right = Boolean.parseBoolean(rightValue);
				
				return !left.equals(right);
			}
		}
		
		return false;
	}
}
