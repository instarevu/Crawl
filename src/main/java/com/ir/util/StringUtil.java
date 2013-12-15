package com.ir.util;

import org.apache.commons.collections.MapUtils;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Map;

public class StringUtil {

    private StringUtil(){}


    private static final String NULL_STRING = "";

    public static String deleteListofTokens(String input, List<String> deleteTokens){
        for(String token : deleteTokens){
            input = input.replaceAll(token, NULL_STRING);
        }
        return input;
    }


    private static final String MAP_FORMAT = "%-15s: %s \n";

    public static String prettifyMapForDebug(Map<? extends Object, ? extends Object> map){
        StringBuffer prettyString = new StringBuffer("[ \n");
        for(Map.Entry<? extends Object, ? extends Object> o : map.entrySet()){
            prettyString.append(String.format(MAP_FORMAT, o.getKey(), o.getValue()));
        }

        return prettyString.append(" ]").toString();
    }
}
