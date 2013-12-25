package com.ir.util;

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.apache.commons.lang.StringEscapeUtils;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class StringUtil {

    private StringUtil(){}


    private static final String CHAR_EMPTY = "";

    private static final String CHAR_WHITESPACE = "";

    public static String deleteListOfTokens(String input, Set<String> deleteTokens){
        for(String token : deleteTokens){
            input = input.replaceAll(token, CHAR_EMPTY);
        }
        return input.trim();
    }

    // Rogue Char --> � ( due to encoding problem > gets replaced with \uFFFD )
    public static String purgeSpecialChars(String input){
        input = StringEscapeUtils.escapeHtml(input).replaceAll("&nbsp;", " ")
                .replaceAll("\uFFFD", ">")
                .replaceAll("&#xfffd;", ">");
        return StringEscapeUtils.unescapeHtml(input);
    }

    public static String dedupeString(String input, boolean removeWhiteSpace){
        input = input.trim();
        if(removeWhiteSpace){
            input = input.replaceAll(CHAR_WHITESPACE, CHAR_EMPTY);
        }
        int length = input.length();
        if(input.substring(0, length/2).equalsIgnoreCase(input.substring(length/2+1, length))){
            input = input.substring(0, length/2);
        }
        return input;
    }

    private static final String MAP_FORMAT = "%-7s: %s \n";

    public static String prettifyMapForDebug(Map<? extends Object, ? extends Object> map){
        StringBuffer prettyString = new StringBuffer("[ \n");
        for(Map.Entry<? extends Object, ? extends Object> o : map.entrySet()){
            prettyString.append(String.format(MAP_FORMAT, o.getKey(), o.getValue()));
        }

        return prettyString.append(" ]").toString();
    }

}
