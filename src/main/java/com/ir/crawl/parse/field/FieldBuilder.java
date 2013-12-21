package com.ir.crawl.parse.field;


import com.ir.crawl.parse.query.AttrValueQuery;
import com.ir.crawl.parse.query.Query;
import com.ir.crawl.parse.query.TextQuery;
import com.ir.crawl.parse.validation.field.DependencyRule;
import com.ir.crawl.parse.validation.field.NotNullRule;
import com.ir.crawl.parse.validation.field.Rule;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public class FieldBuilder {

    private String fieldName = null;

    private Set<Query> queries = new LinkedHashSet<Query>(3);

    private Set<Rule> rules = new LinkedHashSet<Rule>(3);

    private Set<String> deleteTokens = null;

    private Class dataType = String.class;

    public FieldBuilder(String fieldName, Class dataType){
        if(fieldName == null) throw new IllegalArgumentException("Field Name cannot be null");
        this.fieldName = fieldName;
        this.dataType = dataType;
    }

    public FieldBuilder addQ(Query query){
        queries.add(query);
        return this;
    }

    public FieldBuilder del(String... tokens){
        if(deleteTokens == null)
            deleteTokens = new HashSet<String>(5);
        for(String token : tokens){
            deleteTokens.add(token);
        }
        return this;
    }

    public FieldBuilder addQ(String... queryStr){
        if(queryStr.length < 1 || queryStr.length > 2 )
            throw new IllegalArgumentException("Need 1 String minimum, 2 Maximum.");
        if(queryStr.length == 1)
            queries.add(new TextQuery(queryStr[0]));
        if(queryStr.length == 2)
            queries.add(new AttrValueQuery(queryStr[0], queryStr[1]));
        return this;
    }

    public FieldBuilder addV(Rule rule){
        rules.add(rule);
        return this;
    }

    public FieldBuilder addNotNullRule(){
        rules.add(new NotNullRule());
        return this;
    }

    public FieldBuilder addDependsRule(String dependsField){
        rules.add(new DependencyRule(dependsField));
        return this;
    }

    public Field c(){
        return new Field(fieldName, dataType, queries, rules, deleteTokens);
    }


}
