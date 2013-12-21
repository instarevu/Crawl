package com.ir.crawl.parse.parser;


import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.gson.JsonParser;
import com.ir.crawl.parse.bean.ParseResponse;
import com.ir.crawl.parse.field.Field;
import com.ir.crawl.parse.field.FieldBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public abstract class AbstractParser implements Parser {

    static final String[] DEL_TOKENS_PRICE = {",", "\\$"};

    static final JsonParser jsonParser = new JsonParser();

    String baseURI = "";

    Set<Field> decisionFields = null;

    Set<Field> fields = null;

    public ParseResponse parseAll(String htmlData) {
        Document doc = Jsoup.parse(htmlData, baseURI);
        Map<Field, Object> dataMap = new LinkedHashMap<Field, Object>();
        if(!isValidForProcessing(doc, dataMap)){
            return new ParseResponse(false);
        }

        for(Field f : fields){
            f.extract(this, dataMap, doc);
        }
        // Purposefully done in separate loop
        for(Field f : fields){
            f.convertDataType(dataMap);
        }
        //Indexer.addDoc((String)dataMap.get(getFieldByName("id")) ,dataMap);
        return new ParseResponse(true, dataMap);
    }

    public boolean isValidForProcessing(Document doc, Map<Field, Object> dataMap){
        for(Field f : decisionFields){
            f.extract(this, dataMap, doc);
            if(!f.isValid(this, dataMap))
                return false;
        }
        return true;
    }

    FieldBuilder field(String fieldName){
        return new FieldBuilder(fieldName, String.class);
    }

    FieldBuilder field(String fieldName, Class type){
        return new FieldBuilder(fieldName, type);
    }

    public abstract boolean finalizeAndAddValue(Map<Field, Object> dataMap, Field field, String value);

    public Set<Field> getFields(){
        return fields;
    }


    static Map<String, Field> fieldNameMap = null;

    public Field getFieldByName(String fieldName){
        if(fieldNameMap == null){
            fieldNameMap = new HashMap<String, Field>(fields.size());
            for(Field f : decisionFields) {
                fieldNameMap.put(f.getName(), f);
            }
            for(Field f : fields) {
                fieldNameMap.put(f.getName(), f);
            }
        }
        return fieldNameMap.get(fieldName);

    }


    public static void main(String[] args) throws Exception{
        File file = new File("/Users/sathiya/Work/Git/ir/Crawl/src/test/resources/amazon/data");

        for (File f : file.listFiles()){
            String data = Files.toString(f, Charsets.UTF_8);
            Document doc = Jsoup.parse(data, "http://www.amazon.com/");

            // if List has multiple price '-' assign smallest to actual, if actual is null. List can be null.
            //if(doc.select("div[class=buying] > b").size() > 0){
                System.out.println(f.getName().substring(0, 20) + "     LIST-1: " + doc.select("li[class*=nav-category-button]").text());
                //System.out.println(f.getName().substring(0, 20) + "     LIST-2: " + doc.select("#mbc").attr("data-brand"));
                //System.out.println(f.getName().substring(0, 20) + "     LIST-3: " + doc.select("a[href*=brandtextbin]").text());;
            //}
            System.out.println(f.getName().substring(0, 20) + "---------------------------------------------------------------------------");
            //System.out.println();
        }

    }
}
