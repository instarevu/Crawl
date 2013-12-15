package com.ir.crawl.parse;

import com.ir.crawl.parse.mine.MinerType;
import com.ir.crawl.parse.query.Query;

public class DataElement {

    private boolean required = true;

    private Query query = null;

    private String field = null;

    private MinerType minerType = null;

    public DataElement(boolean required, Query query, String field, MinerType minerType) {
        this.required = required;
        this.query = query;
        this.field = field;
        this.minerType = minerType;
    }
    public boolean isRequired() {
        return required;
    }

    public Query getQuery() {
        return query;
    }

    public String getField() {
        return field;
    }

    public MinerType getMinerType() {
        return minerType;
    }
}
