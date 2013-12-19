package com.ir.crawl.parse.validation;


import com.ir.crawl.parse.field.Field;

import java.util.Map;

public interface Rule {

    public boolean validate(Field field, Map<Field, Object> dataMap);

}
