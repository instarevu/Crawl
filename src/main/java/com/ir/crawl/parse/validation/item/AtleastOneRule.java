package com.ir.crawl.parse.validation.item;

import com.ir.core.error.Error;
import com.ir.crawl.parse.field.Field;
import com.ir.crawl.parse.parser.Parser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AtleastOneRule extends AbstractItemRule {

    private static final Logger logger = LogManager.getLogger(AtleastOneRule.class);

    private Set<String> fields;

    public AtleastOneRule(Error error, String... fieldsArray){
        super(error, ItemRuleType.ATLEAST_ONE);
        fields = new HashSet<String>(fieldsArray.length);
        for(String fieldName : fieldsArray){
            fields.add(fieldName);
        }
    }

    @Override
    public boolean validate(Parser parser, Map<Field, Object> dataMap) {
        for(String fieldName : fields){
            if(dataMap.get(parser.getField(fieldName)) != null)
                return true;
        }
        logger.debug("Failed on Rule: " + this.getRuleType() + " - " + error.getDescription());
        return false;
    }

    public String toString(){
        return ruleType.toString() + " on ( " + fields + " )";
    }

}
