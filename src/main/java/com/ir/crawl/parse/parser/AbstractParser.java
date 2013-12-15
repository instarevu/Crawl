package com.ir.crawl.parse.parser;


import com.ir.crawl.parse.DataElement;
import com.ir.crawl.parse.mine.ValueMiner;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractParser {

    String CHARSET = "UTF-8";

    String baseURI = "";

    List<DataElement> dataElements = null;

    public Map<String, String> parseProductAttributes(String htmlData) {
        Document doc = Jsoup.parse(htmlData, baseURI);
        Map<String, String> responseMap = new HashMap<String, String>();

        for(DataElement dataElement : dataElements){
            if(responseMap.get(dataElement.getField()) == null){
                String value = null;
                Elements elements = doc.select(dataElement.getQuery().getElementQuery());
                value = ValueMiner.mine(elements, dataElement.getQuery(), dataElement.getMinerType());
                if(!(value == null || value.equalsIgnoreCase(""))){
                    value = finalizeValue(dataElement.getField(), value);
                    responseMap.put(dataElement.getField(), value);
                }
            }
        }
        return responseMap;
    }

    abstract String finalizeValue(String field, String extractedValue);

}
