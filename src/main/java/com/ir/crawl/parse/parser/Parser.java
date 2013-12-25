package com.ir.crawl.parse.parser;

import com.ir.crawl.parse.bean.ParseResponse;
import com.ir.crawl.parse.field.Field;
import org.jsoup.nodes.Document;

import java.util.Map;
import java.util.Set;

public interface Parser {

    public ParseResponse parseAll(Document htmlDocument);

    public boolean finalizeAndAddValue(Map<Field, Object> dataMap, Field field, String value);

    public Field getField(String fieldName);

    public Set<Field> getFields();

    public Field getErrorField();

    public String getDataType();

    public String getRetailer();

    public String getBaseURI();

    public String getParserLabel();
}
