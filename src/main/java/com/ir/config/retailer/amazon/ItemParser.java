package com.ir.config.retailer.amazon;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.ir.core.error.ParseError;
import com.ir.crawl.parse.field.Field;
import com.ir.crawl.parse.parser.AbstractParser;
import com.ir.crawl.parse.query.RawStringQuery;
import com.ir.crawl.parse.validation.item.AtleastOneRule;
import com.ir.crawl.parse.validation.item.ItemRule;
import com.ir.util.StringUtil;
import org.elasticsearch.common.joda.time.DateTime;
import org.elasticsearch.common.joda.time.format.DateTimeFormatter;
import org.elasticsearch.common.joda.time.format.ISODateTimeFormat;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import sun.net.www.content.audio.x_aiff;

import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import static com.ir.config.retailer.amazon.FieldNames.*;


public class ItemParser extends AbstractParser {

    private static final String[] DEL_TOKENS_MERCHANT = { "Ships from and", "sold by", "Sold by", "Gift-wrap available.","in easy-to-open packaging.","and Fulfilled by Amazon."};

    private static final String[] EXCLUSION_CATEGORIES = { "Amazon MP3 Store", "Music", "Your Instant Video", "Buy a Kindle","Magazine Subscriptions", "Books", "Video Games", "Appstore for Android", "Movies & TV" };

    public ItemParser(){
        super();
        baseURI = "http://www.amazon.com/";
        decisionFields = ImmutableSet.of(
                field(ID).addQ("input[id=ASIN]", "value").addQ("input[name*=ASIN]", "value").addNotNullRule(ParseError.MISSING_ID).c(),
                field(NAV_CAT).addQ("li[class*=nav-category-button]").addNotNullRule().setExclusionRule(EXCLUSION_CATEGORIES).c()
        );
        fields = ImmutableSet.of(
                field(TITLE).addQ("h1[id=title]").addQ("#btAsinTitle").addQ("h1[class*=parseasinTitle]").addNotNullRule().c(),
                field(BRAND).addQ("#brand").addQ("a[href*=brandtextbin]").addQ("#mbc", "data-brand").addNotNullRule().c(),
                field(BREADCRUMB).addQ("div[class=detailBreadcrumb]").c(),
                field(MERCHANT).addQ("#merchant-info").addQ("div[class=buying] > b").del(DEL_TOKENS_MERCHANT).c(),
                field(PRC_LIST, Float.class).addQ("td[class*=a-text-strike]").addQ("#listPriceValue").del(DEL_TOKENS_PRICE).c(),
                field(PRC_ACTUAL, Float.class).addQ("#priceblock_ourprice").addQ("#actualPriceValue").del(DEL_TOKENS_PRICE).c(),
                field(PRC_MIN, Float.class).c(),
                field(PRC_MAX, Float.class).c(),
                field(PRC_SALE, Float.class).addQ("#priceblock_saleprice").del(DEL_TOKENS_PRICE).c(),
                field(IDF_MODEL).addQ("li:contains(Item model number)").addQ("td:contains(Item model number) ~ td").del("Item model number:").c(),
                field(IDF_UPC).addQ("li:contains(UPC)").addQ("td:contains(UPC) ~ td").del("UPC:").c(),
                field(IDF_ISBN10).addQ("li:contains(ISBN-10)").del("ISBN-10:").c(),
                field(IDF_ISBN13).addQ("li:contains(ISBN-13)").del("ISBN-13:").c(),
                field(RANK_L1).addQ("#SalesRank").del("Best Sellers Rank", "Amazon", ":").c(),
                field(RANK_L2).addDependsRule(RANK_L1).c(),
                field(RANK_L3).addDependsRule(RANK_L2).c(),
                field(REVIEW_AVG, Float.class).addQ("#avgRating").c(),
                field(REVIEW_COUNT, Integer.class).addQ("#summaryStars").del(",").c(),
                field(VRNT_SPEC).addQ("div[class=disclaim]").addDependsRule(VRNT_IDS).c(),
                field(VRNT_IDS).addQ(new RawStringQuery("script[data-a-state*=twisterData]")).c(),
                field(URL).addQ("link[rel=canonical]", "href").addNotNullRule().c(),
                field(URL_IMG).addQ("#landingImage", "src").addQ("#main-image", "src").addQ("#prodImage", "src").addNotNullRule().c()
        );
        itemRules = ImmutableSet.of(
                new AtleastOneRule(ParseError.MISSING_ITEM_CLASSIFIER, RANK_L1, BREADCRUMB),
                new AtleastOneRule(ParseError.MISSING_PRICE, PRC_LIST, PRC_ACTUAL, PRC_MIN, PRC_MAX),
                (ItemRule)new AtleastOneRule(ParseError.MISSING_IDENTIFIER, IDF_MODEL, IDF_UPC, IDF_ISBN10, IDF_ISBN13)
        );
    }

    public boolean finalizeAndAddValue(Map<Field, Object> dataMap, Field field, String input){
        // PRICE
        String fieldName = field.getName();
        if(fieldName.equalsIgnoreCase(PRC_LIST)){
            if(input.trim().contains(" ")){
                String prices[] = input.split(" ");
                input = prices[0].trim();
                dataMap.put(getFieldByName(PRC_ACTUAL), prices[1].trim());
            } else {
                input = StringUtil.dedupeString(input, false);
            }
        } else if(fieldName.equalsIgnoreCase(PRC_ACTUAL)){
            if(input.contains("-")){
                String prices[] = input.split("-");
                dataMap.put(getFieldByName(PRC_MIN), prices[0].trim());
                dataMap.put(getFieldByName(PRC_MAX), prices[1].trim());
                input = null;
            }
        // RANK
        } else if(fieldName.equalsIgnoreCase(RANK_L1)){
            String[] ranks  = input.split("#");
            dataMap.put(getFieldByName(RANK_L1), ranks[1].split("\\(")[0]);
            if(ranks.length > 2)dataMap.put(getFieldByName(RANK_L2), ranks[2]);
            if(ranks.length > 3)dataMap.put(getFieldByName(RANK_L3), ranks[3]);
        // RATING
        } else if(fieldName.equalsIgnoreCase(REVIEW_AVG)){
            input = input.split(" out")[0];
        } else if(fieldName.equalsIgnoreCase(REVIEW_COUNT)){
            input = input.split("\\(")[1].split("\\)")[0];
        // VARIANT
        } else if(fieldName.equalsIgnoreCase(VRNT_SPEC)){
            input = StringUtil.dedupeString(input, false);
        } else if(fieldName.equalsIgnoreCase(VRNT_IDS)){
            Set<String> variantIds = new TreeSet<String>();
            for( Map.Entry<String, JsonElement> e : ((JsonObject) jsonParser.parse(getVariationValues(input))).entrySet()){
                variantIds.add(e.getKey());
            }
            input = variantIds.toString();
        }

        if(dataMap.get(field) == null) dataMap.put(field, input);

        return true;
    }

    private static final String VARIANT_STR_TOKEN_1 = "asin_variation_values\":";

    private static final String VARIANT_STR_TOKEN_2 = ",\"variation_values\"";

    private static String getVariationValues(String input){
        return input.split(VARIANT_STR_TOKEN_1)[1].split(VARIANT_STR_TOKEN_2)[0];
    }

}
