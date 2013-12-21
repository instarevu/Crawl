package com.ir.core.error;

import com.ir.core.Stage;

public class ParseError extends AbstractError {


    public static final ParseError MISSING_ITEM_CLASSIFIER = new ParseError("030001", "Unable to identify item classification.");

    public static final ParseError MISSING_IDNF = new ParseError("030002", "Unable to identify any unique item identifier.");

    public static final ParseError MISSING_PRICE = new ParseError("030003", "Unable to identify item price.");


    private ParseError(String code, String description){
        this.stage = Stage.PARSE;
        this.code = code;
        this.description = description;
    }

    public String toString(){
        return "[Error:" + this.getCode() + "] - " + getDescription();
    }


}
