package com.ir.crawl.parse.parser;


import com.google.common.base.Charsets;
import com.google.gson.JsonParser;
import com.ir.core.error.ErrorUtil;
import com.ir.crawl.parse.bean.ParseResponse;
import com.ir.crawl.parse.field.Field;
import com.ir.crawl.parse.field.FieldBuilder;
import com.ir.crawl.parse.field.GenericFieldNames;
import com.ir.crawl.parse.validation.item.ItemRule;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.common.joda.time.DateTime;
import org.elasticsearch.common.joda.time.format.DateTimeFormatter;
import org.elasticsearch.common.joda.time.format.ISODateTimeFormat;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;

public abstract class AbstractParser implements Parser {

    protected static final Logger logger = LogManager.getLogger(AbstractParser.class.getName());

    public static final String[] DEL_TOKENS_PRICE = {",", "\\$"};

    public static final JsonParser jsonParser = new JsonParser();

    public Charset charSet = Charsets.ISO_8859_1;

    public String baseURI = "";

    public Set<Field> decisionFields = new HashSet<Field>(0);

    public Set<Field> fields = new HashSet<Field>(0);

    public final Field errorField = field(GenericFieldNames._ERRORS).c();

    public Set<ItemRule> itemRules = new HashSet<ItemRule>(0);

    public ParseResponse parseAll(Document htmlDocument) {
        Map<Field, Object> dataMap = createNewDataMap();
        if(!isValidForProcessing(htmlDocument, dataMap)){
            return new ParseResponse(false, dataMap);
        }

        for(Field f : fields){
            f.extract(this, dataMap, htmlDocument);
        }
        // Purposefully done in separate loop
        for(Field f : fields){
            f.convertDataType(dataMap);
        }

        findErrors(dataMap);
        logger.debug("Completed Parsing: " + dataMap);
        try {
            logger.info("JSON: " + transformToJSON(dataMap));

        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ParseResponse(true, dataMap);
    }

    private Map<Field, Object> createNewDataMap(){
        return new LinkedHashMap<Field, Object>();
    }

    public boolean isValidForProcessing(Document doc, Map<Field, Object> dataMap){
        for(Field f : decisionFields){
            f.extract(this, dataMap, doc);
            if(!f.isValid(this, dataMap)){
                return false;
            }
        }
        for(Field f : decisionFields){
            if(f.getExclusionRule() != null){
                boolean exclude = !f.getExclusionRule().validate(this, dataMap);
                if(exclude){
                    ErrorUtil.addError(f.getExclusionRule().getError(), errorField, dataMap);
                    return false;
                }
            }
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


    public String transformToJSON(Map<Field, Object> dataMap) throws IOException {
        DateTimeFormatter dateTimeFormatter = ISODateTimeFormat.basicDateTimeNoMillis();
        dateTimeFormatter.print(new DateTime());

        XContentBuilder jsonBuilder = XContentFactory.jsonBuilder()
                .startObject();
        for(Map.Entry<Field, Object> entry : dataMap.entrySet()){
            jsonBuilder.field(entry.getKey().getName(), entry.getValue());
        }

        jsonBuilder.endObject();

        return jsonBuilder.prettyPrint().string();
    }

    public FieldBuilder field(String fieldName){
        return new FieldBuilder(fieldName, String.class);
    }

    public FieldBuilder field(String fieldName, Class type){
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

}
