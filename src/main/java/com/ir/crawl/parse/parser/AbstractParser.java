package com.ir.crawl.parse.parser;


import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.gson.JsonParser;
import com.ir.core.error.ErrorUtil;
import com.ir.crawl.parse.bean.ParseResponse;
import com.ir.crawl.parse.field.Field;
import com.ir.crawl.parse.field.FieldBuilder;
import com.ir.crawl.parse.field.FieldNames;
import com.ir.crawl.parse.validation.item.ItemRule;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.nio.charset.Charset;
import java.util.*;

public abstract class AbstractParser implements Parser {

    static final String[] DEL_TOKENS_PRICE = {",", "\\$"};

    static final JsonParser jsonParser = new JsonParser();

    Charset charSet = Charsets.ISO_8859_1;

    String baseURI = "";

    Set<Field> decisionFields = new HashSet<Field>(0);

    Set<Field> fields = new HashSet<Field>(0);

    final Field errorField = field(FieldNames._ERRORS).c();

    Set<ItemRule> itemRules = new HashSet<ItemRule>(0);

    public ParseResponse parseAll(String htmlData) {
        Document doc = Jsoup.parse(htmlData, baseURI);
        Map<Field, Object> dataMap = createNewDataMap();
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

        findErrors(dataMap);
        //Indexer.addDoc((String)dataMap.get(getFieldByName("id")) ,dataMap);
        return new ParseResponse(true, dataMap);
    }

    private Map<Field, Object> createNewDataMap(){
        Map<Field, Object> dataMap = new LinkedHashMap<Field, Object>();
        return dataMap;
    }

    public boolean isValidForProcessing(Document doc, Map<Field, Object> dataMap){
        for(Field f : decisionFields){
            f.extract(this, dataMap, doc);
            if(!f.isValid(this, dataMap))
                return false;
        }
        return true;
    }

    public void findErrors(Map<Field, Object> dataMap){
        for(ItemRule itemRule : itemRules){
            if(!itemRule.validate(this, dataMap)){
                ErrorUtil.addError(itemRule.getError(), errorField, dataMap);
            }
        }
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

    public Field getErrorField() {
        return errorField;
    }

    public Charset getCharSet(){ return charSet; };

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
            String data = Files.toString(f, Charsets.ISO_8859_1);
            Document doc = Jsoup.parse(data, "http://www.amazon.com/");

            // if List has multiple price '-' assign smallest to actual, if actual is null. List can be null.
            //if(doc.select("div[class=buying] > b").size() > 0){
                System.out.println(f.getName() + "     LIST-1: " + doc.select("div[class=detailBreadcrumb]").text());
            //}
            System.out.println(f.getName() + "---------------------------------------------------------------------------");
        }

    }
}
