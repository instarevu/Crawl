package com.ir.core.error;

import com.ir.core.Stage;

public class ParseError extends AbstractError {


    public static final ParseError MISSING_ID = new ParseError("030000", "Unable to identify ID.");

    public static final ParseError MISSING_ATTRIBUTE = new ParseError("030005", "Missing required attribute.");

    public static final ParseError MISSING_DEPENDENDENCY = new ParseError("030006", "Missing dependent attribute.");

    public static final ParseError MISSING_ITEM_CLASSIFIER = new ParseError("030010", "Unable to identify item classification.");

    public static final ParseError MISSING_IDNF = new ParseError("030011", "Unable to identify any unique item identifier.");

    public static final ParseError MISSING_PRICE = new ParseError("030012", "Unable to identify item price.");



    private ParseError(String code, String description){
        this.stage = Stage.PARSE;
        this.code = code;
        this.description = description;
    }

}
