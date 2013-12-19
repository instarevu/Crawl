package com.ir.crawl.parse.query;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class TextQuery extends AbstractQuery {


    public TextQuery(String query) {
        super(query);
    }

    public String mineForValue(Document doc){
        Elements elements = doc.select(getQuery());
        return elements.text();
    }


}