package com.ir.crawl.parse.query;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class RawStringQuery extends AbstractQuery {

    public RawStringQuery(String query) {
        super(query);
    }

    public String mineForValue(Document doc){
        Elements elements = doc.select(getQuery());
        return elements.toString();
    }

}