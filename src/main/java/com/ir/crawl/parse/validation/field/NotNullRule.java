package com.ir.crawl.parse.validation.field;


import com.ir.core.error.Error;
import com.ir.core.error.ParseError;
import com.ir.crawl.parse.field.Field;

import java.util.Map;

public class NotNullRule extends AbstractRule {

    public NotNullRule(){
        super(RuleType.NOT_NULL, ParseError.MISSING_ATTRIBUTE);
    }

    public NotNullRule(Error error){
        super(RuleType.NOT_NULL, error);
    }

    @Override
    public boolean validate(Field field, Map<Field, Object> dataMap) {
        if(dataMap.get(field) == null)
            return false;
        else
            return true;
    }
}
