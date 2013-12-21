package com.ir.crawl.parse.validation.item;


import com.ir.crawl.parse.field.Field;
import com.ir.crawl.parse.parser.Parser;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AtleastOneRule extends AbstractItemRule {

    private Set<String> fields;

    public AtleastOneRule(String description, String... fieldsArray){
        super(ItemRuleType.ATLEAST_ONE);
        this.description = description;
        fields = new HashSet<String>(fieldsArray.length);
        for(String fieldName : fieldsArray){
            fields.add(fieldName);
        }
    }

    @Override
    public boolean validate(Parser parser, Map<Field, Object> dataMap) {
        for(String fieldName : fields){
            if(dataMap.get(parser.getFieldByName(fieldName)) != null)
                return true;
        }
        System.out.println("Failed on Rule: " + this.getRuleType() + " - " + description);
        return false;
    }

    public String toString(){
        return ruleType.toString() + " on ( " + fields + " )";
    }

}
