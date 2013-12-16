package com.ir.crawl.parse.parser;


import com.ir.crawl.parse.DataElement;
import com.ir.crawl.parse.mine.ValueMiner;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractParser {

    String baseURI = "";

    List<DataElement> dataElements = null;

    public Map<String, String> parseProductAttributes(String htmlData) {
        Document doc = Jsoup.parse(htmlData, baseURI);
        Map<String, String> response = new LinkedHashMap<String, String>();

        for(DataElement dataElement : dataElements){
            if(response.get(dataElement.getField()) == null){
                Elements elements = doc.select(dataElement.getQuery().getElementQuery());
                String value = ValueMiner.mine(elements, dataElement.getQuery(), dataElement.getMinerType());
                if(!(value == null || value.equalsIgnoreCase(""))){
                    finalizeAndAddValue(response, dataElement.getField(), value);
                }
            }
        }
        return response;
    }

    abstract String finalizeAndAddValue(Map<String, String> response, String field, String extractedValue);

    public List<DataElement> getDataElements(){
        return dataElements;
    }

}
