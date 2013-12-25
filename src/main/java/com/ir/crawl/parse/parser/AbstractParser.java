package com.ir.crawl.parse.parser;


import com.google.common.base.Charsets;
import com.google.gson.JsonParser;
import com.ir.core.error.*;
import com.ir.core.error.Error;
import com.ir.crawl.parse.bean.ParseResponse;
import com.ir.crawl.parse.field.Field;
import com.ir.crawl.parse.field.FieldBuilder;
import com.ir.crawl.parse.validation.item.ItemRule;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.common.joda.time.DateTime;
import org.elasticsearch.common.joda.time.format.DateTimeFormatter;
import org.elasticsearch.common.joda.time.format.ISODateTimeFormat;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;

import static com.ir.config.retailer.amazon.FieldNames.*;

public abstract class AbstractParser implements Parser {

    protected static final Logger logger = LogManager.getLogger(AbstractParser.class.getName());

    public static final String[] DEL_TOKENS_PRICE = {",", "\\$"};

    public static final JsonParser jsonParser = new JsonParser();

    public Charset charSet = Charsets.ISO_8859_1;

    public String baseURI = "";

    public Set<Field> decisionFields = new HashSet<Field>(0);

    public Set<Field> fields = new HashSet<Field>(0);

    public final Field errorField = field(_ERRORS).c();

    public Set<ItemRule> itemRules = new HashSet<ItemRule>(0);

    public ParseResponse parseAll(Document htmlDocument) {
        Map<Field, Object> dataMap = createNewDataMap();
        if(!isValidForProcessing(htmlDocument, dataMap)){
            return new ParseResponse(false, dataMap);
        }

        for(Field f : fields){
            f.extract(this, dataMap, htmlDocument);
        }
        // Purposefully done in separate loop
        for(Field f : fields){
            f.convertDataType(dataMap);
        }

        findErrors(dataMap);
        try {
            logger.info("JSON: " + transformToJSON(dataMap));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ParseResponse(true, dataMap);
    }

    private Map<Field, Object> createNewDataMap(){
        return new LinkedHashMap<Field, Object>();
    }

    public boolean isValidForProcessing(Document doc, Map<Field, Object> dataMap){
        for(Field f : decisionFields){
            f.extract(this, dataMap, doc);
            if(!f.isValid(this, dataMap)){
                return false;
            }
        }
        for(Field f : decisionFields){
            if(f.getExclusionRule() != null){
                boolean exclude = !f.getExclusionRule().validate(this, dataMap);
                if(exclude){
                    ErrorUtil.addError(f.getExclusionRule().getError(), errorField, dataMap);
                    return false;
                }
            }
        }
        return true;
    }

    public void findErrors(Map<Field, Object> dataMap){
        for(ItemRule itemRule : itemRules){
            if(!itemRule.validate(this, dataMap)){
                ErrorUtil.addError(itemRule.getError(), errorField, dataMap);
            }
        }
    }


    public FieldBuilder field(String fieldName){
        return new FieldBuilder(fieldName, String.class);
    }

    public FieldBuilder field(String fieldName, Class type){
        return new FieldBuilder(fieldName, type);
    }

    public abstract boolean finalizeAndAddValue(Map<Field, Object> dataMap, Field field, String value);

    public Set<Field> getFields(){
        return fields;
    }

    public Field getErrorField() {
        return errorField;
    }

    static Map<String, Field> fieldNameMap = null;

    public Field getField(String fieldName){
        if(fieldNameMap == null){
            fieldNameMap = new HashMap<String, Field>(fields.size());
            for(Field f : decisionFields) {
                fieldNameMap.put(f.getName(), f);
            }
            for(Field f : fields) {
                fieldNameMap.put(f.getName(), f);
            }
        }
        return fieldNameMap.get(fieldName);
    }


    private static final DateTimeFormatter dateTimeFormatter = ISODateTimeFormat.basicDateTimeNoMillis();

    public String transformToJSON(Map<Field, Object> dataMap) throws IOException {
        String now = dateTimeFormatter.print(new DateTime());
        boolean rank = false, review = false, variant = false, identifier = false, price = false, url = false;
        XContentBuilder xb = XContentFactory.jsonBuilder();
        xb.startObject();
        for(Map.Entry<Field, Object> entry : dataMap.entrySet()){
            Field field = entry.getKey();
            String name = field.getName();
            if(name.equals(RANK_L1) || name.equals(RANK_L2) || name.equalsIgnoreCase(RANK_L3)){
                if(!rank){
                    xb.startArray("rnk");
                    addRankSpec(xb, RANK_L1, dataMap);
                    addRankSpec(xb, RANK_L2, dataMap);
                    addRankSpec(xb, RANK_L3, dataMap);
                    xb.endArray();
                    rank = true;
                }
                continue;
            } else if(name.equals(PRC_ACTUAL) || name.equals(PRC_LIST) ||
                      name.equals(PRC_MAX) || name.equals(PRC_MIN) ||
                      name.equals(PRC_SALE) || name.equals(MERCHANT) ){
                if(!price){
                    xb.startArray("prc");
                    xb.startObject();
                    addField(xb, "a", PRC_ACTUAL, dataMap);
                    addField(xb, "l", PRC_LIST, dataMap);
                    addField(xb, "s", PRC_SALE, dataMap);
                    addField(xb, "min", PRC_MIN, dataMap);
                    addField(xb, "max", PRC_MAX, dataMap);
                    addField(xb, "by", MERCHANT, dataMap);
                    xb.field("__t", now);
                    xb.endObject();
                    xb.endArray();
                    price = true;
                }
                continue;
            } else if(name.equals(REVIEW_AVG) || name.equals(REVIEW_COUNT)){
                if(!review){
                    xb.startObject("rvw");
                    addField(xb, "avg", REVIEW_AVG, dataMap);
                    addField(xb, "cnt", REVIEW_COUNT, dataMap);
                    xb.endObject();
                    review = true;
                }
                continue;
            } else if(name.equals(VRNT_IDS) || name.equals(VRNT_SPEC)){
                if(!variant){
                    xb.startObject("vrnt");
                    addField(xb, "vs", VRNT_SPEC, dataMap);
                    addField(xb, "vid", VRNT_IDS, dataMap);
                    xb.endObject();
                    variant = true;
                }
                continue;
            } else if(name.equals(IDF_MODEL) || name.equals(IDF_UPC) ||
                      name.equals(IDF_ISBN10) || name.equals(IDF_ISBN13) ){
                if(!identifier){
                    xb.startObject("idnf");
                    addField(xb, "m", IDF_MODEL, dataMap);
                    addField(xb, "u", IDF_UPC, dataMap);
                    addField(xb, "i10", IDF_ISBN10, dataMap);
                    addField(xb, "i13", IDF_ISBN13, dataMap);
                    xb.endObject();
                    identifier = true;
                }
                continue;
            } else if(name.equals(URL) || name.equals(URL_IMG)){
                if(!url){
                    xb.startObject("url");
                    addField(xb, "can", URL, dataMap);
                    addField(xb, "img", URL_IMG, dataMap);
                    xb.endObject();
                    url = true;
                }
                continue;
            } else if(name.equals(_ERRORS)){
                List<Error> errors = ErrorUtil.getErrorCodes(getField(_ERRORS), dataMap);
                if(errors != null){
                    String[] errorCodes = new String[errors.size()];
                    int i = 0;
                    for(Error e : errors){
                        errorCodes[i++] = e.getCode();
                    }
                    xb.array("__e", errorCodes);
                }
                continue;
            }
            xb.field(field.getName(), entry.getValue());
        }
        xb.field(_TIME, now);
        xb.endObject();
        return xb.prettyPrint().string();
    }

    private void addField(XContentBuilder xb, String jsonName, String fieldName, Map<Field, Object> dataMap)
        throws IOException {
        if(dataMap.get(getField(fieldName)) != null){
            xb.field(jsonName, dataMap.get(getField(fieldName)));
        }
    }


    private void addRankSpec(XContentBuilder xb, String fieldName, Map<Field, Object> dataMap)
        throws IOException {
        String rankSpec = (String)dataMap.get(getField(fieldName));
        if(rankSpec != null){
            String[] tokens = rankSpec.split("in");
            if(tokens.length > 1){
                xb.startObject();
                xb.field("p", Integer.parseInt(tokens[0].trim().replaceAll(",", "")));
                xb.field("c", tokens[1].trim());
                xb.endObject();
            }
        }
    }

}
