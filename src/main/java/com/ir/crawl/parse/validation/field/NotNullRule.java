package com.ir.crawl.parse.validation.field;


import com.ir.crawl.parse.field.Field;

import java.util.Map;

public class NotNullRule extends AbstractRule {

    public NotNullRule(){
        super(RuleType.NOT_NULL);
    }

    @Override
    public boolean validate(Field field, Map<Field, Object> dataMap) {
        if(dataMap.get(field) == null)
            return false;
        else
            return true;
    }
}
