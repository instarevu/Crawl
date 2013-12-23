package com.ir.crawl.parse.validation.field;


import com.ir.core.error.Error;
import com.ir.crawl.parse.field.Field;
import com.ir.crawl.parse.parser.Parser;

import java.util.Map;

public abstract class AbstractRule implements Rule {

    Error error;

    RuleType ruleType;

    AbstractRule(RuleType ruleType, Error error){
        this.ruleType = ruleType;
        this.error = error;
    }

    public boolean validate(Field field, Map<Field, Object> dataMap){
        return false;
    }

    public boolean validate(Field field, Parser parser, Map<Field, Object> dataMap){
        return false;
    }

    public RuleType getRuleType(){
        return ruleType;
    }

    public Error getError() {
        return error;
    }

    public String toString(){
        return ruleType.toString();
    }

}
