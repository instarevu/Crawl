package com.ir.crawl.parse.query;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class AttrValueQuery extends AbstractQuery {

    private String attributeKey = "";

    public AttrValueQuery(String query, String attributeKey) {
        super(query);
        this.attributeKey = attributeKey;
    }

    public String mineForValue(Document doc){
        Elements elements = doc.select(getQuery());
        return elements.attr(attributeKey);
    }


}