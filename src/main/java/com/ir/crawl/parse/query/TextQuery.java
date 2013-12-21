package com.ir.crawl.parse.query;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class TextQuery extends AbstractQuery {


    public TextQuery(String query) {
        super(query);
    }

    public String mineForValue(Document doc){
        Elements elements = doc.select(getQuery());
        if(elements.size() > 0)
            return elements.get(0).text();
        else
            return elements.text();
    }


}