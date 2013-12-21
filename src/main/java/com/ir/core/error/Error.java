package com.ir.core.error;

import com.ir.core.Stage;

public interface Error {

    public Stage getStage();

    public String getCode();

    public String getDescription();

}
