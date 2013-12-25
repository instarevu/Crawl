package com.ir.crawl.parse.validation.item;

import com.ir.core.error.Error;
import com.ir.crawl.parse.field.Field;
import com.ir.crawl.parse.parser.Parser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ExcludeOnMatchRule extends AbstractItemRule {

    private static final Logger logger = LogManager.getLogger(ExcludeOnMatchRule.class);

    private Set<String> tokens;

    private Field field;

    public ExcludeOnMatchRule(Error error, Field field, String... tokens){
        super(error, ItemRuleType.EXCLUSION_RULE);
        this.field = field;
        this.tokens = new HashSet<String>(tokens.length);
        for(String token : tokens){
            this.tokens.add(token.toLowerCase());
        }
    }

    @Override
    public boolean validate(Parser parser, Map<Field, Object> dataMap) {
        String fieldData = ((String)dataMap.get(field)).toLowerCase();
        for(String token : tokens){
            if(fieldData.contains(token)){
                logger.error("Failed on Rule: " + this.getRuleType() + " - " + error.getDescription());
                return false;
            }
        }
        return true;
    }

    public String toString(){
        return ruleType.toString() + " on ( " + field.getName() + " )";
    }

}
