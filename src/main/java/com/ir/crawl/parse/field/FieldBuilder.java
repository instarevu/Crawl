package com.ir.crawl.parse.field;


import com.ir.crawl.parse.query.AttrValueQuery;
import com.ir.crawl.parse.query.Query;
import com.ir.crawl.parse.query.TextQuery;
import com.ir.crawl.parse.validation.Rule;

import java.util.LinkedHashSet;
import java.util.Set;

public class FieldBuilder {

    private String fieldName = null;

    private Set<Query> queries = new LinkedHashSet<Query>(3);

    private Set<Rule> rules = new LinkedHashSet<Rule>(3);

    public FieldBuilder(String fieldName){
        if(fieldName == null) throw new IllegalArgumentException("Field Name cannot be null");
        this.fieldName = fieldName;
    }

    public FieldBuilder addQuery(Query query){
        queries.add(query);
        return this;
    }

    public FieldBuilder addQuery(String... queryStr){
        if(queryStr.length < 1 || queryStr.length > 2 )
            throw new IllegalArgumentException("Need 1 String minimum, 2 Maximum.");
        if(queryStr.length == 1)
            queries.add(new TextQuery(queryStr[0]));
        if(queryStr.length == 2)
            queries.add(new AttrValueQuery(queryStr[0], queryStr[1]));
        return this;
    }

    public FieldBuilder addValidation(Rule rule){
        rules.add(rule);
        return this;
    }

    public Field create(){
        return new Field(fieldName, queries, rules);
    }


}
