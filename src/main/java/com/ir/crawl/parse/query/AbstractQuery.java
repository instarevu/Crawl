package com.ir.crawl.parse.query;

public abstract class AbstractQuery implements Query {

    public String elementQuery = null;

    public String nestedQuery = null;

    AbstractQuery(String elementQuery){
        this.elementQuery = elementQuery;
    }

    AbstractQuery(String query, String nestedQuery){
        this.elementQuery = query;
        this.nestedQuery = nestedQuery;
    }

    public String getElementQuery(){
        return  elementQuery;
    }

    public String getNestedQuery(){
        return  nestedQuery;
    }

}
