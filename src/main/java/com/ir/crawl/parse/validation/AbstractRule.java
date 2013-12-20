package com.ir.crawl.parse.validation;


import com.ir.crawl.parse.field.Field;
import com.ir.crawl.parse.parser.Parser;

import java.util.Map;

public abstract class AbstractRule implements Rule {

    RuleType ruleType;

    AbstractRule(RuleType ruleType){
        this.ruleType = ruleType;
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

    public String toString(){
        return ruleType.toString();
    }

}
