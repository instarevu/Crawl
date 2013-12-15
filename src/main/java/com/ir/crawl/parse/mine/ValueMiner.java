package com.ir.crawl.parse.mine;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ir.crawl.parse.query.Query;
import org.jsoup.select.Elements;

import java.util.*;

public class ValueMiner {

    public static String mine(Elements elements, Query query, MinerType minerType){
        switch (minerType) {
            case TEXT:
                return mineForText(elements);
            case ATTRIBUTE:
                return mineForAttribute(elements, query.getNestedQuery());
            case RAW_STRING:
                return mineForRawString(elements);
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

    private static final JsonParser parser = new JsonParser();

    private static String mineForRawString(Elements elements){
        return elements.toString();
    }

}
