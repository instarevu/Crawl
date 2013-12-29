package com.ir.crawl.parse.validation.item;


import com.ir.core.error.Error;
import com.ir.crawl.parse.field.Field;
import com.ir.crawl.parse.parser.Parser;

import java.util.Map;

public abstract class AbstractItemRule implements ItemRule {

    String description = "Atleast one fb must be present.";

    ItemRuleType ruleType;

    Error error;

    AbstractItemRule(Error error, ItemRuleType ruleType){
        this.ruleType = ruleType;
        this.error = error;
    }

    public boolean validate(Parser parser, Map<Field, Object> dataMap){
        return false;
    }

    public ItemRuleType getRuleType(){
        return ruleType;
    }

    public com.ir.core.error.Error getError() {
        return error;
    }

    public String toString(){
        return ruleType.toString();
    }

}
