package com.dbtools.jsonsql.jsonparser;

import org.junit.Assert;

import com.dbtools.jsonsql.operators.UnNestOperator;
import com.dbtools.jsonsql.queryprocessor.QueryParser;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by abhinit on 11/23/15.
 */
public class JsonParser {
    private static final String paddedOpeningBrace = " { ";
    private static final String paddedClosingBrace = " } ";
    private static final String paddedColon = " : ";
    private static final String paddedComma = " , ";

    /**
     * Method appends space at left and right of each special character.
     * and devides string into tokens.
     * @param input
     * @return String with special characters appended with space.
     */
    private static String appendSpace(String input){
        StringBuilder paddedString = new StringBuilder();
        char letter = '\0';

        paddedString.append("{ root : ");
        for(int index = 0;index<input.length();index++){
                letter = input.charAt(index);
                if(letter==ParserUtil.openingBraceChar)
                    paddedString = paddedString.append(paddedOpeningBrace);
                else if(letter==ParserUtil.closingBraceChar)
                    paddedString = paddedString.append(paddedClosingBrace);
                else if(letter==ParserUtil.colonChar)
                    paddedString = paddedString.append(paddedColon);
                else if(letter==ParserUtil.commaChar)
                    paddedString = paddedString.append(paddedComma);
                else
                    paddedString = paddedString.append(letter);
        }

        paddedString.append(" }");

        return paddedString.toString();
    }

    /**
     * Method passes input into ParseTree
     * @param input
     * @return Returns JSON Map
     * @throws InvalidJSONFormat
     */
    public static Map<String,Object> parse(String input) throws InvalidJSONFormat{

            ParseTree instance = new ParseTree();
            input = appendSpace(input);

            List<String> jsonList = new ArrayList<String>();

            for(String token : input.split("[\\s]")){
                jsonList.add(token);
            }

            //jsonList = JsonParser.parseString(jsonList,'\"','\"');
            jsonList = JsonParser.parseString(jsonList,ParserUtil.doubleQuotes,
                    ParserUtil.doubleQuotes);
            jsonList = JsonParser.parseString(jsonList,ParserUtil.openingSquareBracket,
                    ParserUtil.closingSquareBracket);
            jsonList = JsonParser.parseString(jsonList,ParserUtil.openingRoundBracket,
                    ParserUtil.closingRoundBracket);

            Map<String, Object> jsonMap = instance.parse(jsonList);

        return jsonMap;
    }

    /**
     * Method takes list of tokens, and combines tokens enclosed by
     * comma or bracket into a sentence.
     * @param inputTokenList
     * @param openSymbol is opening quotes or bracket of a sentence.
     * @param closeSymbol is closing quotes or bracket of a sentence.
     * @return
     */
    private static List<String> parseString(List<String> inputTokenList,char openSymbol,char closeSymbol){
        StringBuilder elem = null;
        int quoteFlag = 0;
        List<String> tokenList = new ArrayList<String>();

        for(String token : inputTokenList){

            if((token.length()>1 && token.charAt(0)==openSymbol && token.charAt(token.length()-1)!=openSymbol
                    && quoteFlag==0)
                    || (quoteFlag==0 && token.trim().equals(Character.toString(openSymbol)))) {
                if(elem!=null)
                    tokenList.add(elem.toString());

                elem = new StringBuilder();
                elem = elem.append(token);
                quoteFlag = 1;
            }
            else if((token.length()>1 && token.charAt(0)!=closeSymbol && token.charAt(token.length()-1)==closeSymbol
                    && quoteFlag==1)
                    || (quoteFlag==1 && token.trim().equals(Character.toString(closeSymbol)))) {
                elem = elem.append(" ");
                elem = elem.append(token);
                tokenList.add(elem.toString());
                quoteFlag = 0;
            }
            else if(token.trim().length()>0 && quoteFlag==0){
                tokenList.add(token);
            }
            else if(quoteFlag==1){
                elem = elem.append(" ");
                elem = elem.append(token);
            }

        }

        return tokenList;
    }

    public static void main(String[] args){
        try {
            String input = "{a:b}";
            Map<String, Object> jsonMap = null;

            /*Test case 1*/
            jsonMap = JsonParser.parse(input);
            Assert.assertTrue(jsonMap.get("a").equals("b"));

            //*Test case 2*//*
            input = "{a : {b : 1 }}";
            jsonMap = JsonParser.parse(input);
            Assert.assertTrue(((Map<String, Object>) (jsonMap.get("a"))).get("b").equals(1));
//            UnNestOperator unnestOperator = new UnNestOperator();
//            Map<String,Object> flattenedJson = unnestOperator.getTuple(jsonMap);
//            System.out.println(flattenedJson);

            //*Test case 3*//*
            input = "{a : [{b : 100,c : 2 }, {b : 100,c : 2 }]}";
            jsonMap = JsonParser.parse(input);
           // Assert.assertTrue(((Map<String, Object>) (jsonMap.get("a"))).get("b").equals(100));

            //*Test case 3*//*
            input = "{a : {b : 1,c : {a:1} }}";
            jsonMap = JsonParser.parse(input);
            Assert.assertTrue(((Map<String, Object>)((Map<String, Object>) (jsonMap.get("a"))).get("c")).get("a").equals(1));

            //*Test case 3*//*
            input = "{a : {b : {d : 1},c : {a:1} }}";
            jsonMap = JsonParser.parse(input);
            Assert.assertTrue(((Map<String, Object>) ((Map<String, Object>) (jsonMap.get("a"))).get("b")).get("d").equals(1));


            input = "{a : {b : 1 , c : 2 , p : { d : 3 , e : 7 , f : { a : 1 } } }}";
            jsonMap = JsonParser.parse(input);
            Assert.assertTrue(((Map<String, Object>) ((Map<String, Object>) ((Map<String, Object>)
                    (jsonMap.get("a"))).get("p")).get("f")).get("a").equals(1));

            input = "{a : {}}";
            jsonMap = JsonParser.parse(input);

            input = "{ debug : on, window:{title : sample, size:500} }";
            jsonMap = JsonParser.parse(input);
            Assert.assertTrue((jsonMap.get("debug")).equals("on"));

            Assert.assertTrue(((Map<String, Object>)(jsonMap.get("window"))).get("title").equals("sample"));
            Assert.assertTrue(((Map<String, Object>)(jsonMap.get("window"))).get("size").equals(500));

            input = "{"+
                    "GlossTerm: (Standard asfd, Generalized \"Abhinit Kumar\" )," +
                    "\"country\":\"India\""+
                    "}";

            jsonMap = JsonParser.parse(input);
            
            Gson gson  = new Gson();
            input = "{a : [{b : 100,c : 2 }, {b : 100,c : 2 }]}";
            Map<String,Object> map = new HashMap<String,Object>();
            map = (Map<String,Object>) gson.fromJson(input, map.getClass());
            System.out.println(map);
            UnNestOperator unnestOperator = new UnNestOperator();
            Map<String,Object> flattenedJson = unnestOperator.getTuple(map);
            System.out.println(flattenedJson);
           
            

        }catch (Exception ex){
            ex.printStackTrace();
        }

    }
}
