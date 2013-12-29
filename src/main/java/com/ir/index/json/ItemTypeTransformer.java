package com.ir.index.json;


import com.ir.core.error.Error;
import com.ir.core.error.ErrorUtil;
import com.ir.crawl.parse.field.Field;
import com.ir.crawl.parse.parser.Parser;
import org.elasticsearch.common.joda.time.format.DateTimeFormatter;
import org.elasticsearch.common.joda.time.format.ISODateTimeFormat;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static com.ir.config.retailer.amazon.FieldNames.*;
import static com.ir.crawl.parse.field.GenericFieldNames._ERRORS;
import static com.ir.crawl.parse.field.GenericFieldNames._TIME;

public class ItemTypeTransformer {

    public static final DateTimeFormatter DATE_FORMATTER = ISODateTimeFormat.basicDateTimeNoMillis();


    public static XContentBuilder serialize(Parser parser, Map<Field, Object> data) throws IOException {
        boolean rank = false;
        XContentBuilder xb = XContentFactory.jsonBuilder();
        xb.startObject();
        for(Map.Entry<Field, Object> entry : data.entrySet()){
            Field field = entry.getKey();
            String name = field.getName();
            if(name.equals(RANK_L1) || name.equals(RANK_L2) || name.equalsIgnoreCase(RANK_L3)){
                if(!rank){
                    addRankSpec(parser, xb, RANK_L1, data);
                    addRankSpec(parser, xb, RANK_L2, data);
                    addRankSpec(parser, xb, RANK_L3, data);
                    rank = true;
                }
                continue;
            } else if(field.equals(parser.getErrorField())){
                List<Error> errors = ErrorUtil.getErrorCodes(parser.getErrorField(), data);
                if(errors != null){
                    String[] errorCodes = new String[errors.size()];
                    int i = 0;
                    for(Error e : errors){
                        errorCodes[i++] = e.getCode();
                    }
                    xb.array(_ERRORS, errorCodes);
                }
                continue;
            } else if(!name.equals(ID)){
                xb.field(field.getName(), entry.getValue());
            }
        }
        xb.endObject();
        return xb;
    }

    public static XContentBuilder serializeExclItem(Parser parser, Map<Field, Object> dataMap) throws IOException {
        XContentBuilder xb = XContentFactory.jsonBuilder();
        xb.startObject();
        addField(parser, xb, dataMap, BRAND);
        addField(parser, xb, dataMap, CAT);
        addField(parser, xb, dataMap, _TIME);
        xb.endObject();
        return xb;
    }

    private static void addObject(String objectLabel, Parser parser, XContentBuilder xb, Map<Field, Object> dataMap, String... fields)
            throws IOException {
        if(objectLabel != "")
            xb.startObject(objectLabel);
        else
            xb.startObject();
        addField(parser, xb, dataMap, fields);
        xb.endObject();
    }

    private static void addField(Parser parser, XContentBuilder xb, Map<Field, Object> dataMap, String... fields)
            throws IOException {
        for(String f : fields){
            if(dataMap.get(parser.getField(f)) != null){
                xb.field(f, dataMap.get(parser.getField(f)));
            }
        }
    }

    private static void addRankSpec(Parser parser, XContentBuilder xb, String fieldName, Map<Field, Object> dataMap)
            throws IOException {
        String rankSpec = (String)dataMap.get(parser.getField(fieldName));
        if(rankSpec != null){
            String[] tokens = rankSpec.split(" in ");
            if(tokens.length > 1){
                xb.field(fieldName+"_p", Integer.parseInt(tokens[0].trim().replaceAll(",", "")));
                xb.field(fieldName+"_c", tokens[1].trim());
            }
        }
    }

}
