package com.dbtools.jsonsql.queryprocessor;

import java.util.List;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.FromItem;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectBody;
import net.sf.jsqlparser.statement.select.SelectItem;

public class QueryParser {
private Select select = null;
private PlainSelect ps = null;
	
	public void parseQuery(String query) throws JSQLParserException {
		this.select = (Select)CCJSqlParserUtil.parse(query);	
		SelectBody selectBody = (SelectBody)select.getSelectBody();
		ps = (PlainSelect)selectBody;
	}
	
	public List<SelectItem> getSelectItems() {
		
		

		return ps.getSelectItems();
	}
	
	public List<FromItem> getFromItems() {
		return (List<FromItem>) ps.getFromItem();
	}
	
	public Expression getWhereClause() {
		return ps.getWhere();
	}
}
