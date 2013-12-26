package com.ir.core.error;

import com.ir.core.Stage;
import com.ir.crawl.parse.field.Field;

import java.lang.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ErrorUtil {

    public static void addError(Error error, Field errorField, Map<Field, Object> dataMap){
        if(dataMap.get(errorField) == null){
            Map<Stage, List<Error>> errorData = new HashMap<Stage, List<Error>>();
            dataMap.put(errorField, errorData);
        }

        Map<Stage, List<Error>> errorData = (Map<Stage, List<Error>>)dataMap.get(errorField);
        if(errorData.get(error.getStage()) == null){
            List<Error> errorList = new ArrayList<Error>(3);
            errorData.put(error.getStage(), errorList);
        }

        List<Error> list = (List<Error>)errorData.get(error.getStage());
        list.add(error);
    }

    public static List<Error> getErrorCodes(Field errorField, Map<Field, Object> dataMap){
        if(dataMap.get(errorField) == null){
            return null;
        }

        List<Error> errorList = new ArrayList<Error>(3);
        Map<Stage, List<Error>> errorData = (Map<Stage, List<Error>>)dataMap.get(errorField);
        for(List<Error> errors : errorData.values()){
            errorList.addAll(errors);
        }
        return errorList;
    }

    public static boolean isErrorCodePresent(Field errorField, Map<Field, Object> dataMap, Error error){
        List<Error> list = getErrorCodes(errorField, dataMap);
        if(list != null && list.contains(error)){
            return true;
        }
        return false;
    }


    public static String getErrorDescription(List<Error> errors){
        String _e = " ";
        if(errors != null){
            for(Error error : errors){
                _e += error.toString() + " ";
            }
        }
        return _e;
    }

}
