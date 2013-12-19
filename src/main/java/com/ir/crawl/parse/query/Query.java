package com.ir.crawl.parse.query;


import org.jsoup.nodes.Document;

public interface Query {

    public String getQuery();

    public String mineForValue(Document doc);

}
