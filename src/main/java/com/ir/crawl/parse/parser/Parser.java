package com.ir.crawl.parse.parser;

import com.ir.crawl.parse.field.Field;

import java.util.Map;

public interface Parser {

    public Map<Field, Object> parseAll(String htmlData);

    public boolean finalizeAndAddValue(Map<Field, Object> dataMap, Field field, String value);

    public Field getFieldByName(String fieldName);
}
