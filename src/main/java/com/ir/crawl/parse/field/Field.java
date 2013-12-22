package com.ir.crawl.parse.field;


import com.ir.crawl.parse.parser.Parser;
import com.ir.crawl.parse.query.Query;
import com.ir.crawl.parse.validation.field.DependencyRule;
import com.ir.crawl.parse.validation.field.Rule;
import com.ir.util.StringUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.nodes.Document;

import java.util.Map;
import java.util.Set;

public class Field {

    private static final Logger logger = LogManager.getLogger(Field.class);

    private String name =  null;

    private Set<Rule> rules = null;

    private Set<Query> queries = null;

    private Set<String> deleteTokens = null;

    private Class dataType = String.class;

    Field(String fieldName, Class dataType, Set<Query> queries,  Set<Rule> rules, Set<String> deleteTokens){
        this.name = fieldName;
        this.queries = queries;
        this.rules = rules;
        this.deleteTokens = deleteTokens;
        this.dataType = dataType;
    }

    public boolean isValid(Parser parser, Map<Field, Object> dataMap){
        return isValid(parser, dataMap, null);
    }

    public boolean isValid(Parser parser, Map<Field, Object> dataMap, String originField){
        for(Rule rule : rules){
            switch(rule.getRuleType()){
                case NOT_NULL:
                    if(!rule.validate(this, dataMap)) {
                        logger.debug("Validation Failed. ID: \"" + dataMap.get(parser.getFieldByName("id")) + "\"  Field: \"" + this + "\" for Rule: \"" + rule + "\"");
                        return false;
                    }
                    break;
                case DEPENDENCY:
                    if(originField == null || !((DependencyRule)rule).getDependentFieldName().equalsIgnoreCase(originField)){
                        if(!rule.validate(this, parser, dataMap)) {
                            logger.debug("Validation Failed. Field: \"" + this + "\" for Rule:  \"" + rule + "\"");
                            return false;
                        }
                    }
                    break;
                case VALUE_CONTAINS:
                    if(!rule.validate(this, dataMap)) {
                        logger.debug("Validation Failed. ID: \"" + dataMap.get(parser.getFieldByName("id")) + "\"  Field: \"" + this + "\" for Rule: \"" + rule + "\"");
                        return false;
                    }
                    break;
            }
        }
        return true;
    }

    public boolean extract(Parser parser, Map<Field, Object> dataMap, Document doc){
        boolean validationResult = true;
        for(Query query : queries){
            String data = query.mineForValue(doc);
            if(!(data == null || data.equalsIgnoreCase(""))){
                data = StringUtil.purgeSpecialChars(data);
                if(deleteTokens != null)
                    data = StringUtil.deleteListOfTokens(data, deleteTokens);
                parser.finalizeAndAddValue(dataMap, this, data);
            }
        }
        return validationResult;
    }

    public String extract(Document doc){
        String data = "";
        for(Query query : queries){
            data = query.mineForValue(doc);
            if(!(data == null || data.equalsIgnoreCase(""))){
                data = StringUtil.purgeSpecialChars(data);
                if(deleteTokens != null)
                    data = StringUtil.deleteListOfTokens(data, deleteTokens);
            }
        }
        return data;
    }

    public boolean convertDataType(Map<Field, Object> dataMap){
        if(this.dataType != String.class && dataMap.get(this) != null){
            Object current = dataMap.get(this);
            if(dataType == Integer.class){
                dataMap.put(this, Integer.parseInt((String) current));
            } else if(dataType == Float.class) {
                dataMap.put(this, Float.parseFloat((String) current));
            }

        }
        return true;
    }

    public String getName(){
        return name;
    }

    public String toString(){
        return name;
    }

    public static FieldBuilder n(String fieldName){
        return new FieldBuilder(fieldName, String.class);
    }

    public static FieldBuilder n(String fieldName, Class dataType){
        return new FieldBuilder(fieldName, dataType);
    }

}
