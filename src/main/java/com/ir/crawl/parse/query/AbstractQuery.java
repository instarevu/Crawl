package com.ir.crawl.parse.query;

import org.jsoup.nodes.Document;

public abstract class AbstractQuery implements Query {

    public String query = null;

    AbstractQuery(String query){
        this.query = query;
    }

    public abstract String mineForValue(Document document);

    public String getQuery(){
        return  query;
    }

}
