package com.ir.crawl.parse.validation.field;


import com.ir.crawl.parse.field.Field;
import com.ir.crawl.parse.parser.Parser;

import java.util.Map;

public interface Rule {

    public boolean validate(Field field, Map<Field, Object> dataMap);

    public boolean validate(Field field, Parser parser, Map<Field, Object> dataMap);

    public RuleType getRuleType();

}
