package com.ir.util;

import java.util.List;

public class StringUtil {

    private StringUtil(){}


    private static final String NULL_STRING = "";

    public static String deleteListofTokens(String input, List<String> deleteTokens){
        for(String token : deleteTokens){
            input = input.replaceAll(token, NULL_STRING);
        }
        return input;
    }
}
