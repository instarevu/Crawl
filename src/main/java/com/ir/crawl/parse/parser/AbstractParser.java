package com.ir.crawl.parse.parser;


import com.google.gson.JsonParser;
import com.ir.crawl.parse.field.Field;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.*;

public abstract class AbstractParser implements Parser {

    static final JsonParser jsonParser = new JsonParser();

    String baseURI = "";

    Set<Field> decisionFields = null;

    Set<Field> fields = null;

    public Map<Field, Object> parseAll(String htmlData) {
        Document doc = Jsoup.parse(htmlData, baseURI);
        Map<Field, Object> dataMap = new LinkedHashMap<Field, Object>();

        isValidForProcessing(doc, dataMap);

        for(Field f : fields){
            f.extract(this, dataMap, doc);
        }
        // Purposefully done in separate loop
        for(Field f : fields){
            f.convertDataType(dataMap);
        }
        //Indexer.addDoc(dataMap);
        return dataMap;
    }

    public boolean isValidForProcessing(Document doc, Map<Field, Object> dataMap){
        for(Field f : decisionFields){
            f.extract(this, dataMap, doc);
        }

        for(Field f : decisionFields){
            if(!f.isValid(this, dataMap));
                return false;
        }
        return true;
    }

    public abstract boolean finalizeAndAddValue(Map<Field, Object> dataMap, Field field, String value);

    public Set<Field> getFields(){
        return fields;
    }


    static Map<String, Field> fieldNameMap = null;

    public Field getFieldByName(String fieldName){
        if(fieldNameMap == null){
            fieldNameMap = new HashMap<String, Field>(fields.size());
            for(Field f : fields) {
                fieldNameMap.put(f.getName(), f);
            }
        }
        return fieldNameMap.get(fieldName);

    }

}
