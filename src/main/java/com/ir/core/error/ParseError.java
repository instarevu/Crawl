package com.ir.core.error;

import com.ir.core.Stage;

public class ParseError extends AbstractError {


    public static final ParseError MISSING_ID = new ParseError(Stage.PARSE+"-030000", "Unable to identify ID.");

    public static final ParseError MISSING_ATTRIBUTE = new ParseError(Stage.PARSE+"-030005", "Missing required attribute.");

    public static final ParseError MISSING_DEPENDENCY = new ParseError(Stage.PARSE+"-030006", "Missing dependent attribute.");

    public static final ParseError MISSING_ITEM_CLASSIFIER = new ParseError(Stage.PARSE+"-030010", "Unable to identify item classification.");

    public static final ParseError MISSING_IDENTIFIER = new ParseError(Stage.PARSE+"-030011", "Unable to find any unique item identifiers.");

    public static final ParseError MISSING_PRICE = new ParseError(Stage.PARSE+"-030012", "Unable to identify item price.");


    private ParseError(String code, String description){
        this.stage = Stage.PARSE;
        this.code = code;
        this.description = description;
    }

}
