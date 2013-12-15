package com.ir.crawl.parse.parser;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ir.crawl.parse.DataElement;
import com.ir.crawl.parse.field.AmazonFields;
import com.ir.crawl.parse.mine.MinerType;
import com.ir.crawl.parse.query.AttrValueQuery;
import com.ir.crawl.parse.query.RawStringQuery;
import com.ir.crawl.parse.query.TextQuery;
import com.ir.util.StringUtil;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class AmazonParser extends AbstractParser{

    private static final String BASE_URI = "http://www.amazon.com/";

    public AmazonParser(){
        this.baseURI = BASE_URI;
        this.dataElements = ImmutableList.of(
            new DataElement(true, new AttrValueQuery("input[id=ASIN]", "value"), AmazonFields.ID, MinerType.ATTRIBUTE),
            new DataElement(true, new TextQuery("h1[id=title]"), AmazonFields.TITLE, MinerType.TEXT),
            new DataElement(true, new AttrValueQuery("div[id=mbc]", "data-brand"), AmazonFields.BRAND, MinerType.ATTRIBUTE),
            new DataElement(true, new TextQuery("a[id=brand]"), AmazonFields.BRAND, MinerType.TEXT),
            new DataElement(true, new TextQuery("span[id=priceblock_ourprice]"), AmazonFields.PRICE_OFFERED, MinerType.TEXT),
            new DataElement(false, new TextQuery("td[class*=a-text-strike]"), AmazonFields.PRICE_LISTED, MinerType.TEXT),
            new DataElement(true, new AttrValueQuery("link[rel=canonical]", "href"), AmazonFields.URL, MinerType.ATTRIBUTE),
            new DataElement(true, new AttrValueQuery("img[id=landingImage]", "src"), AmazonFields.URL_IMAGE, MinerType.ATTRIBUTE),
            new DataElement(false, new TextQuery("div[class=disclaim]"), AmazonFields.VARIANT_SPECS, MinerType.TEXT),
            new DataElement(false, new RawStringQuery("script[data-a-state*=twisterData]"), AmazonFields.VARIANT_IDS, MinerType.RAW_STRING)
        );
    }

    private static final List<String> VARIANT_DELETE_TOKENS = ImmutableList.of(
            "<div class=\"disclaim\">", "<strong>", "</strong>", "&nbsp;", "</div>", "(\\r|\\n)");

    private static final String VARIANT_STR_TOKEN_1 = "asin_variation_values\":";

    private static final String VARIANT_STR_TOKEN_2 = ",\"variation_values\"";

    private static final JsonParser parser = new JsonParser();

    public String finalizeValue(String field, String input){

        if(field.equalsIgnoreCase(AmazonFields.VARIANT_SPECS)){
            input = StringUtil.deleteListofTokens(input, VARIANT_DELETE_TOKENS);
            input = input.trim();
            int length = input.length();
            if(input.substring(0, length/2).equalsIgnoreCase(input.substring(length/2+1, length))){
                input = input.substring(0, length/2);
            }
        } else if(field.equalsIgnoreCase(AmazonFields.VARIANT_IDS)){
            JsonObject jsonObject = (JsonObject) parser.parse(getVariationValues(input));
            Set<String> variantIds = new TreeSet<String>();
            for( Map.Entry<String, JsonElement> e : jsonObject.entrySet()){
                variantIds.add(e.getKey());
            }
            input = variantIds.toString();
        }
        return input;
    }

    private static String getVariationValues(String input){
        return input.split(VARIANT_STR_TOKEN_1)[1].split(VARIANT_STR_TOKEN_2)[0];
    }

}
