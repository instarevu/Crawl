package com.ir.util;

import com.ir.config.retailer.amazon.FieldNames;
import com.ir.crawl.parse.field.Field;
import com.ir.crawl.parse.field.GenericFieldNames;
import com.sun.tools.javac.jvm.Gen;
import org.apache.commons.lang.StringEscapeUtils;

import java.util.Map;
import java.util.Set;

public class StringUtil {

    private StringUtil(){}

    private static final String CHAR_EMPTY = "";

    private static final String CHAR_WHITESPACE = "";

    public static String deleteListOfTokens(String input, Set<String> deleteTokens){
        for(String token : deleteTokens){
            input = input.trim().replaceAll(token, CHAR_EMPTY);
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
            Field f = (Field)o.getKey();
            if( o.getValue() instanceof String []){
                String value = "";
                for(String s : (String[])o.getValue()){
                    value += s + " ";
                }
                prettyString.append(String.format(MAP_FORMAT, f, value));
            } else if( o.getValue() instanceof Set){
                String value = "";
                for(Object s : (Set<Object>)o.getValue()){
                    value += s + " ";
                }
                prettyString.append(String.format(MAP_FORMAT, f, value));
            } else {
                prettyString.append(String.format(MAP_FORMAT, f, o.getValue()));
            }
        }

        return prettyString.append(" ]").toString();
    }

}
