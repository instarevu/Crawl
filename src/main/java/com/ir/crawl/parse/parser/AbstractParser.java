package com.ir.crawl.parse.parser;


import com.google.gson.JsonParser;
import com.ir.core.error.ErrorUtil;
import com.ir.crawl.parse.bean.ParseResponse;
import com.ir.crawl.parse.field.Field;
import com.ir.crawl.parse.field.FieldBuilder;
import com.ir.crawl.parse.validation.item.ItemRule;
import com.ir.index.json.ItemTypeTransformer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.common.joda.time.DateTime;
import org.jsoup.nodes.Document;

import java.util.*;

import static com.ir.config.retailer.amazon.FieldNames.MERCHANT;
import static com.ir.config.retailer.amazon.FieldNames._ERRORS;
import static com.ir.crawl.parse.field.GenericFieldNames._TIME;

public abstract class AbstractParser implements Parser {

    protected static final Logger logger = LogManager.getLogger(AbstractParser.class.getName());

    public static final String[] DEL_TOKENS_PRICE = {",", "\\$"};

    public static final JsonParser jsonParser = new JsonParser();

    protected String baseURI = "";

    protected Set<Field> decisionFields = new HashSet<Field>(0);

    protected Set<Field> fields = new HashSet<Field>(0);

    protected Set<Field> metaFields = new HashSet<Field>(2);

    protected Set<ItemRule> itemRules = new HashSet<ItemRule>(0);

    protected String dataType;

    protected String retailer;

    protected String parserLabel;

    protected AbstractParser(String baseURI, String dataType, String retailer) {
        this.baseURI = baseURI;
        this.retailer = retailer;
        this.dataType = dataType;
        this.parserLabel = "[" + getRetailer() + "-" + getDataType() + "]";
        this.metaFields.add(fb(_TIME).c());
        this.metaFields.add(fb(_ERRORS).c());
    }

    public ParseResponse parseAll(Document htmlDocument) {
        Map<Field, Object> dataMap = createNewDataMap();
        if(!isValidForProcessing(htmlDocument, dataMap)){
            return new ParseResponse(false, dataMap);
        }

        for(Field f : fields){
            f.extract(this, dataMap, htmlDocument);
        }
        for(Field f : fields){
            f.convertDataType(dataMap);
        }
        findErrors(dataMap);
        dedupeValues(dataMap);
        return new ParseResponse(true, dataMap);
    }

    private Map<Field, Object> createNewDataMap(){
        Map<Field, Object> dataMap = new LinkedHashMap<Field, Object>();
        String now = ItemTypeTransformer.DATE_FORMATTER.print(new DateTime());
        dataMap.put(getField(_TIME), now);
        return dataMap;
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
                    ErrorUtil.addError(f.getExclusionRule().getError(), getErrorField(), dataMap);
                    return false;
                }
            }
        }
        return true;
    }

    public void findErrors(Map<Field, Object> dataMap){
        for(Field field : fields){
            field.isValid(this, dataMap);
        }
        for(ItemRule itemRule : itemRules){
            if(!itemRule.validate(this, dataMap)){
                ErrorUtil.addError(itemRule.getError(), getErrorField(), dataMap);
            }
        }
    }

    public abstract void dedupeValues(Map<Field, Object> dataMap);

    public FieldBuilder fb(String fieldName){
        return new FieldBuilder(fieldName, String.class);
    }

    public FieldBuilder fb(String fieldName, Class type){
        return new FieldBuilder(fieldName, type);
    }

    public abstract boolean finalizeAndAddValue(Map<Field, Object> dataMap, Field field, String value);

    public Set<Field> getFields(){
        return fields;
    }

    public Field getErrorField() {
        return getField(_ERRORS);
    }

    public String getDataType() {
        return dataType;
    }

    public String getRetailer() {
        return retailer;
    }

    public String getBaseURI(){ return baseURI; }

    public String getParserLabel(){ return parserLabel; }

    static Map<String, Field> fieldNameMap = null;

    public Field getField(String fieldName){
        if(fieldNameMap == null){
            fieldNameMap = new HashMap<String, Field>(fields.size());
            for(Field f : decisionFields) {
                fieldNameMap.put(f.getName(), f);
            }
            for(Field f : fields) {
                fieldNameMap.put(f.getName(), f);
            }
            for(Field f : metaFields) {
                fieldNameMap.put(f.getName(), f);
            }
        }
        return fieldNameMap.get(fieldName);
    }

}