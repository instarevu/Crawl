package com.ir.crawl.parse.validation.field;


import com.ir.core.error.ParseError;
import com.ir.crawl.parse.field.Field;
import com.ir.crawl.parse.parser.Parser;

import java.util.Map;

public class DependencyRule extends AbstractRule {

    private String dependentFieldName;

    public DependencyRule(String dependentFieldName){
        super(RuleType.DEPENDENCY, ParseError.MISSING_DEPENDENCY);
        this.dependentFieldName = dependentFieldName;
    }

    @Override
    public boolean validate(Field field, Parser parser, Map<Field, Object> dataMap) {
        Field dependentField = parser.getField(dependentFieldName);
        if(dataMap.get(field) != null){
            if(dataMap.get(dependentField) != null && dependentField.isValid(parser, dataMap, field.getName()))
                return true;
            else
                return false;
        }
        return true;
    }

    public String getDependentFieldName(){
        return dependentFieldName;
    }

    public String toString(){
        return ruleType.toString() + " on ( " + dependentFieldName + " )";
    }

}
