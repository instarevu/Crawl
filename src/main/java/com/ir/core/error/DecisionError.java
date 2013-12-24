package com.ir.core.error;

import com.ir.core.Stage;

public class DecisionError extends AbstractError {


    public static final DecisionError CAT_EXCLUSION = new DecisionError(Stage.DECISION+"-020001", "Category Excluded.");


    private DecisionError(String code, String description){
        this.stage = Stage.DECISION;
        this.code = code;
        this.description = description;
    }

}
