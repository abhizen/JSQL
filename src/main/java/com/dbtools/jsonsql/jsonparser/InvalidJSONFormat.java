package com.dbtools.jsonsql.jsonparser;

/**
 * Created by abhinit on 11/25/15.
 */
public class InvalidJSONFormat extends Exception{
    private static final String errorMessage = "Invalid JSON format";

    InvalidJSONFormat(){
        super(errorMessage);
    }
}
