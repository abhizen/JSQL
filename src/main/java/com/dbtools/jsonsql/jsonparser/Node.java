package com.dbtools.jsonsql.jsonparser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by abhinit on 11/23/15.
 */
public class Node implements Expression{
    private Expression value;
    private List<Node> children = null;

    public Node(Expression value){
        this.value = value;
        this.children = new ArrayList<Node>();
    }

    public Expression getValue(){
        return this.value;
    }

    public void setChildren(Node childNode){
        this.children.add(childNode);
    }

    public List<Node> getChildren(){
        return this.children;
    }

    public Expression get(String key){
        Expression expr = null;
        if(this.children!=null && !this.children.isEmpty()){
            for(Node child : this.children){
                expr = child.getValue();
                if(expr instanceof Variable
                        && ((String)((Variable) expr).getName()).equals(key))
                    return child;
            }
        }

        return null;
    }

    public boolean equals(String key){
        Expression expr = null;

        if(this.children!=null && !this.children.isEmpty()){

            for(Node child : this.children){
                expr = child.getValue();
                if(expr instanceof Variable
                        && ((String)((Variable) expr).getName()).equals(key)) {
                    if(child.getChildren()==null || child.getChildren().isEmpty())
                        return true;
                }
            }
        }

        return false;
    }
}
