package com.ir.util;

import com.google.common.collect.ImmutableList;
import org.apache.commons.lang.StringEscapeUtils;

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

    private static final List<String> HTML_CLEANUP_PRICE_TOKENS = ImmutableList.of(
            ",", "\\$");
    // Rogue Char --> �
    public static String purgeSpecialChars(String input){
        //input = deleteListOfTokens(input, HTML_CLEANUP_PRICE_TOKENS);
        input = StringEscapeUtils.escapeHtml(input).replaceAll("&nbsp;", " ");
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


    private static final String MAP_FORMAT = "%-15s: %s \n";

    public static String prettifyMapForDebug(Map<? extends Object, ? extends Object> map){
        StringBuffer prettyString = new StringBuffer("[ \n");
        for(Map.Entry<? extends Object, ? extends Object> o : map.entrySet()){
            prettyString.append(String.format(MAP_FORMAT, o.getKey(), o.getValue()));
        }

        return prettyString.append(" ]").toString();
    }
}
