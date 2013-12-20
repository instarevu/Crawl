package com.ir.crawl.parse.parser;


import com.google.gson.JsonParser;
import com.ir.crawl.parse.bean.ParseResponse;
import com.ir.crawl.parse.field.Field;
import com.ir.index.es.Indexer;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.*;

public abstract class AbstractParser implements Parser {

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

}
