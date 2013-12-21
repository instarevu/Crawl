package com.ir.crawl.parse.validation.item;


import com.ir.core.error.Error;
import com.ir.crawl.parse.field.Field;
import com.ir.crawl.parse.parser.Parser;

import java.util.Map;

public interface ItemRule {

    public boolean validate(Parser parser, Map<Field, Object> dataMap);

    public ItemRuleType getRuleType();

    public Error getError();

}
