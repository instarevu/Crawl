package com.ir.crawl.parse.validation;


import com.ir.crawl.parse.field.Field;

import java.util.Map;

public class ValueContainsRule extends AbstractRule {

    private String concatinsText = null;

    public ValueContainsRule(String containsText){
        super(RuleType.VALUE_CONTAINS);
        this.concatinsText = containsText;
    }

    @Override
    public boolean validate(Field field, Map<Field, Object> dataMap) {
        if(dataMap.get(field) == null)
            return false;
        else if(((String)dataMap.get(field)).contains(concatinsText))
            return true;
        else
            return false;
    }
}
