package com.dbtools.jsonsql.jsonparser;

/**
 * Created by abhinit on 11/23/15.
 */
public class Variable implements Expression {
    private String value;

    public Variable(String value){
        this.value = value;
    }

    public String getName(){
        return this.value;
    }

   
    public Expression getValue() {
        return null;
    }
}
