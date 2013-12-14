package com.ir.crawl.parse.parser;


import com.ir.crawl.parse.ParseQuery;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractParser {

    String CHARSET = "UTF-8";

    String baseURI = "";

    List<ParseQuery> productQueries = null;

    public Map<String, String> parseProductAttributes(String htmlData) {
        Document doc = Jsoup.parse(htmlData, baseURI);
        Map<String, String> responseMap = new HashMap<String, String>();

        for(ParseQuery query : productQueries){
            String value = "";
            Elements elements = doc.select(query.firstLevelQuery);
            if(query.attributeQuery != null){
                value = elements.attr(query.attributeQuery);
            } else {
                value = elements.text();
            }
            responseMap.put(query.key, value);
        }
        return responseMap;
    }

}
