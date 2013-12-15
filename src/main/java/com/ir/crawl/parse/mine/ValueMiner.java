package com.ir.crawl.parse.mine;

import com.ir.crawl.parse.query.Query;
import org.jsoup.select.Elements;

public class ValueMiner {

    public static String mine(Elements elements, Query query, MinerType minerType){
        switch (minerType) {
            case TEXT:
                return mineForText(elements);
            case ATTRIBUTE:
                return mineForAttribute(elements, query.getNestedQuery());
            case JSON:
                return mineForJSON(elements,  query.getNestedQuery());
            default:
                return null;
        }
    }

    private static String mineForText(Elements elements){
        return elements.text();
    }

    private static String mineForAttribute(Elements elements, String attributeKey){
        return elements.attr(attributeKey);
    }

    private static String mineForJSON(Elements elements, String jsonXPath){
        return elements.toString();
    }

}
