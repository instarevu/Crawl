package com.ir.crawl.parse.field;


import com.ir.core.error.DecisionError;
import com.ir.core.error.Error;
import com.ir.crawl.parse.query.AttrValueQuery;
import com.ir.crawl.parse.query.Query;
import com.ir.crawl.parse.query.TextQuery;
import com.ir.crawl.parse.validation.field.DependencyRule;
import com.ir.crawl.parse.validation.field.NotNullRule;
import com.ir.crawl.parse.validation.field.Rule;
import com.ir.crawl.parse.validation.item.ExcludeOnMatchRule;

import java.util.LinkedHashSet;
import java.util.Set;

public class FieldBuilder {

    private String fieldName = null;

    private Set<Query> queries = new LinkedHashSet<Query>(3);

    private Set<Rule> rules = new LinkedHashSet<Rule>(3);

    private Set<String> deleteTokens = null;

    private Class dataType = String.class;

    private String[] exclusionTokens = null;

    public FieldBuilder(String fieldName, Class dataType){
        if(fieldName == null) throw new IllegalArgumentException("Field Name cannot be null");
        this.fieldName = fieldName;
        this.dataType = dataType;
    }

    public FieldBuilder q(Query query){
        queries.add(query);
        return this;
    }

    public FieldBuilder del(String... tokens){
        if(deleteTokens == null)
            deleteTokens = new LinkedHashSet<String>(5);
        for(String token : tokens){
            deleteTokens.add("(?i)"+token);
        }
        return this;
    }

    public FieldBuilder q(String... queryStr){
        if(queryStr.length < 1 || queryStr.length > 2 )
            throw new IllegalArgumentException("Need 1 String minimum, 2 Maximum.");
        if(queryStr.length == 1)
            queries.add(new TextQuery(queryStr[0]));
        if(queryStr.length == 2)
            queries.add(new AttrValueQuery(queryStr[0], queryStr[1]));
        return this;
    }

    public FieldBuilder notNull(){
        rules.add(new NotNullRule());
        return this;
    }

    public FieldBuilder notNull(Error error){
        rules.add(new NotNullRule(error));
        return this;
    }

    public FieldBuilder depends(String dependsField){
        rules.add(new DependencyRule(dependsField));
        return this;
    }

    public FieldBuilder excludeIf(String... tokens){
        exclusionTokens = tokens;
        return this;
    }

    public Field c(){
        Field field = new Field(fieldName, dataType, queries, rules, deleteTokens);
        if(exclusionTokens != null) {
            ExcludeOnMatchRule exclusionRule = new ExcludeOnMatchRule(DecisionError.CAT_EXCLUSION, field, exclusionTokens);
            field.setExclusionRule(exclusionRule);
        }
        return field;
    }


}
