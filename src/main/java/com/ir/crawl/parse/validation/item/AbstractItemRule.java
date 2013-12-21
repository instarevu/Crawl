package com.ir.crawl.parse.validation.item;


import com.ir.crawl.parse.field.Field;
import com.ir.crawl.parse.parser.Parser;
import com.ir.crawl.parse.validation.Rule;
import com.ir.crawl.parse.validation.RuleType;

import java.util.Map;

public abstract class AbstractItemRule implements ItemRule {

    String description = "Atleast one field present.";

    ItemRuleType ruleType;

    AbstractItemRule(ItemRuleType ruleType){
        this.ruleType = ruleType;
    }

    public boolean validate(Parser parser, Map<Field, Object> dataMap){
        return false;
    }

    public ItemRuleType getRuleType(){
        return ruleType;
    }

    public String toString(){
        return ruleType.toString();
    }

}
