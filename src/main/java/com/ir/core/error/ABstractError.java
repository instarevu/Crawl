package com.ir.core.error;


import com.ir.core.Stage;

public abstract class AbstractError implements Error {

    Stage stage;

    String code;

    String description;


    public String getCode(){
        return code;
    }

    public String getDescription() {
        return description;
    }

    public Stage getStage() {
        return stage;
    }
}
