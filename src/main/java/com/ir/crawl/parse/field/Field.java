package com.ir.crawl.parse.field;


import com.ir.crawl.parse.parser.Parser;
import com.ir.crawl.parse.query.Query;
import com.ir.crawl.parse.validation.Rule;
import com.ir.util.StringUtil;
import org.jsoup.nodes.Document;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class Field {

    private String name =  null;

    private Set<Rule> rules = new LinkedHashSet<Rule>(3);

    private Set<Query> queries = new LinkedHashSet<Query>(3);

    Field(String fieldName, Set<Query> queries,  Set<Rule> rules){
        this.name = fieldName;
        this.queries = queries;
        this.rules = rules;
    }

    public boolean validate(Map<Field, Object> dataMap){
        for(Rule rule : rules){
            if(!rule.validate(this, dataMap))
                return false;
        }
        return true;
    }

    public boolean extract(Parser parser, Map<Field, Object> dataMap, Document doc){
        boolean validationResult = true;
        for(Query query : queries){
            String data = query.mineForValue(doc);
            if(!(data == null || data.equalsIgnoreCase(""))){
                data = StringUtil.purgeSpecialChars(data);
                parser.finalizeAndAddValue(dataMap, this, data);
            }
        }
        return validationResult;
    }

    public String getName(){
        return name;
    }

    public String toString(){
        return name;
    }


}
