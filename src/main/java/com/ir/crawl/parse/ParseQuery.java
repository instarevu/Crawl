package com.ir.crawl.parse;

public class ParseQuery {

    public String key = "";

    public String firstLevelQuery = "";

    public String attributeQuery = null;

    public ParseQuery(String key, String firstLevelQuery) {
        this(key, firstLevelQuery, null);
    }

    public ParseQuery(String key, String firstLevelQuery, String attributeQuery) {
        this.key = key;
        this.firstLevelQuery = firstLevelQuery;
        this.attributeQuery = attributeQuery;
    }


}