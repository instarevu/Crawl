package com.ir.crawl.parse.parser;


import com.ir.crawl.parse.field.Field;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.*;

public abstract class AbstractParser implements Parser {

    String baseURI = "";

    Set<Field> fields = null;

    AbstractParser(String baseURI, Set<Field> fields){
        this.baseURI = baseURI;
        this.fields = fields;
    }

    public Map<Field, Object> parseAll(String htmlData) {
        Document doc = Jsoup.parse(htmlData, baseURI);
        Map<Field, Object> response = new LinkedHashMap<Field, Object>();

        for(Field f : fields){
            f.extract(this, response, doc);
        }
        //Indexer.addDoc(response);
        return response;
    }

    public abstract boolean finalizeAndAddValue(Map<Field, Object> dataMap, Field field, String value);

    public Set<Field> getFields(){
        return fields;
    }


    static Map<String, Field> fieldNameMap = null;

    Field getFieldByName(String fieldName){
        if(fieldNameMap == null){
            fieldNameMap = new HashMap<String, Field>(fields.size());
            for(Field f : fields) {
                fieldNameMap.put(f.getName(), f);
            }
        }
        return fieldNameMap.get(fieldName);

    }

}
