package com.ir.crawl.parse.bean;


import com.ir.crawl.parse.field.Field;

import java.util.Map;

public class ParseResponse {

    private boolean isEligibleForProcessing = true;

    private Map<Field, Object> dataMap = null;

    public ParseResponse(boolean isEligibleForProcessing, Map<Field, Object> dataMap) {
        this.isEligibleForProcessing = isEligibleForProcessing;
        this.dataMap = dataMap;
    }

    public boolean isEligibleForProcessing() {
        return isEligibleForProcessing;
    }

    public Map<Field, Object> getDataMap() {
        return dataMap;
    }
}
