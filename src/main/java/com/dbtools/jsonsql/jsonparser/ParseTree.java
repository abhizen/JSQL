package com.dbtools.jsonsql.jsonparser;


import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by abhinit on 11/24/15.
 */
public class ParseTree {
    public Node treeRoot = null;
    private Stack<Expression> expressionStack = null;
    private Map<String,Object> map = new HashMap<String, Object>();
    private Pattern numericPattern = Pattern.compile("-?[0-9]+");
    private Matcher mtch =null;
    /**
     * Method loads JSON string into ParseTree
     * Only alphanumeric strings are saved in parsetree.
     * Special Characters are not saved.
     * @param expression is token from JSON string
     * @throws InvalidJSONFormat
     */
    public  void load(List<String> expression) throws InvalidJSONFormat{
        expressionStack = new Stack<Expression>();
        Variable var = null;
        Expression expr = null;

        for(String token : expression){
        	
            if(token.equals(ParserUtil.closingBrace)){

                expr = expressionStack.pop();
                Node lastNode = (Node)processLastNode(expr);

                if(lastNode!=null)
                    expr = lastNode;

                //Save all the child nodes in list
                List<Expression> children = getChildrenList(expr);
                if(children!=null && !expressionStack.isEmpty()) {
                    expr = expressionStack.pop(); //This expression holds :
                    Node parent = new Node(expressionStack.pop()); //Pop parent node
                    parent = addChildren(parent, children);
                    expressionStack.push(parent);
                }
                /*Add child to stack if parent node is not present*/
                else if(children!=null && expressionStack.isEmpty()){
                    expressionStack.push(children.get(0));
                }

            }
            else if(token.equals(ParserUtil.comma)){
                /*pops till last node before : and adds variable to node as leaf*/
                processComma();
            }
            else if(token.trim().length()>0)
                processVariable(token);
        }
        treeRoot = (Node)expressionStack.pop();
    }

    /**
     * Method wraps alphanumeric string, opening brace and colon
     * into Variable object.
     * @param token
     * @throws InvalidJSONFormat
     */
    private void processVariable(String token) throws InvalidJSONFormat{
        Variable var = null;

        if(isValidColon(token) || isValidVariable(token)) {
            var = new Variable(token);
            expressionStack.push(var);
        }
        else
            throw new InvalidJSONFormat();
    }

    /**
     * Method key : value pair before comma into
     * Parent node with value added as child.
     * @param
     * @return Node
     */
    private void processComma() throws InvalidJSONFormat{
        Expression expr = null;

        if(!isValidComma())
            throw new InvalidJSONFormat();

        if(!expressionStack.isEmpty() &&
                ((expr=expressionStack.peek()) instanceof Variable) ) {
            expr = expressionStack.pop();
            Node child = new Node(expr);
            expr = expressionStack.pop();
            Node parent = new Node(expressionStack.pop());
            parent.setChildren(child);
            expressionStack.push(parent);
        }
       
    }

    /*
     * Method converts the key : value pair before closing brace
     * into Node.
     * In case there is no string between opening and closing brace,
     * method creates a node with empty string.
     * @param expr
     * @return Node
     */
    private  Expression processLastNode(Expression expr){
        if(expr instanceof Variable &&
                !((Variable) expr).getName().equals(ParserUtil.openingBrace)) {
            Node child = new Node(expr);
            expr = expressionStack.pop();
            Node parent = new Node(expressionStack.pop());
            parent.setChildren(child);

            return parent;
        }
        else if(expr instanceof Variable &&
                ((Variable) expr).getName().equals(ParserUtil.openingBrace)) {
            Variable var = new Variable("");
            Node child = new Node(var);
            expressionStack.push(expr);
            return child;
        }

        return null;
    }

    /**
     * Method puts all the nodes separated by comma between two braces
     * into a list.
     * @param expr
     * @return List of child nodes.
     * @throws InvalidJSONFormat
     */
    private List<Expression> getChildrenList(Expression expr) throws InvalidJSONFormat{
        List<Expression> children = null;

        if(expr instanceof Node) {
            children = new ArrayList<Expression>();

            while (!(expr instanceof Variable &&
                    ((Variable) expr).getName().equals(ParserUtil.openingBrace)
                    ) && !expressionStack.isEmpty()) {
                children.add(expr);

                expr = expressionStack.pop();
            }

            if(!(expr instanceof Variable &&
                    ((Variable) expr).getName().equals(ParserUtil.openingBrace)))
                throw new InvalidJSONFormat();

            return children;
        }
        else
            return null;
    }

    /**
     * Method adds list of children to parent children list.
     * @param parent
     * @param children
     * @return returns parent node
     */
    private  Node addChildren(Node parent,
                                    List<Expression> children){
        if(children!=null) {
            /**
             * Add nodes from list to parent as child nodes
             **/
            for (Expression node : children) {
                if (!(node instanceof Variable &&
                        ((Variable) node).getName().equals(ParserUtil.colon))) {
                    if (node instanceof Node)
                        parent.setChildren((Node) node);
                    else
                        parent.setChildren(new Node(node));
                }

            }
        }

        return parent;
    }

    private boolean isValidComma(){
        Expression expression = null;
        String elem = null;


        if(expressionStack.isEmpty())
            return false;
        else {
            expression = expressionStack.peek();

            if (expression instanceof Variable) {
                elem = ((Variable) expression).getName();

                if(elem.equals(ParserUtil.openingBrace) ||
                        elem.equals(ParserUtil.colon) ||
                        elem.equals(ParserUtil.comma))
                    return false;
            }
        }

        return true;
    }

    /**
     * Method checks if colon is between key and value.
     * Else return false.
     * @param token
     * @return If colon is valid return true
     */
    private boolean isValidColon(String token){
        Expression expression = null;

        if(token.equals(ParserUtil.colon)){
            if(expressionStack.isEmpty())
                return false;
            else{
                expression = expressionStack.peek();

                if(!(expression instanceof Variable))
                    return false;
                else{
                    String elem = ((Variable) expression).getName();

                    if(elem.equals(ParserUtil.openingBrace) ||
                            elem.equals(ParserUtil.closingBrace) ||
                            elem.equals(ParserUtil.colon) ||
                            elem.equals(ParserUtil.comma))
                        return false;
                }

            }
        }

        return true;
    }

    /**
     * Method checks if a alphanumeric string is
     * separated by comma or brace or colon.
     * @param token
     * @return
     */
    private boolean isValidVariable(String token){
        Expression expression = null;
        String elem = null;


        if(expressionStack.isEmpty())
                return false;
        else {
            expression = expressionStack.peek();
            if(expression instanceof Node)
                return true;
            else if(expression instanceof Variable){
                elem = ((Variable) expression).getName();
                if(elem.equals(ParserUtil.comma) || elem.equals(ParserUtil.colon) ||
                        elem.equals(ParserUtil.openingBrace))
                    return true;
                else return false;
            }
        }

        return true;
    }

    /**
     * Method traverses the ParseTree and creates key value map.
     * @param node
     * @return
     */
    public Object traverse(Node node){
        Object elem = null;
        Map<String,Object> map = new HashMap<String, Object>();

        if(node==null)
            return null;

        List<Node> children = node.getChildren();

        if(children.size()==0)
            return null;

        for(Node child : children){
            elem = traverse(child);

            if(elem!=null && !(elem instanceof String)) {
                if(child.getValue() instanceof Variable) {
                    map.put(((Variable)child.getValue()).getName(),elem);
                }
            }
            else if(elem!=null && (elem instanceof String)){
                mtch = numericPattern.matcher((String)elem);
                if(mtch.matches()){
                    map.put(((Variable)child.getValue()).getName(),Integer.parseInt((String)elem));
                }
                else
                    map.put(((Variable)child.getValue()).getName(),elem);
            }
            else if(elem==null)
                return ((Variable)child.getValue()).getName();

        }

        return map;
    }

    /**
     * Method takes list of tokens and if valid returns JSON map.
     * @param input is list of tokens
     * @return Map of JSON key values
     * @throws InvalidJSONFormat
     */
    public Map<String,Object> parse(List<String> input) throws InvalidJSONFormat{
        load(input);

        Map<String,Object> jsonMap = (Map<String, Object>) traverse(treeRoot);

        return jsonMap;
    }

}
