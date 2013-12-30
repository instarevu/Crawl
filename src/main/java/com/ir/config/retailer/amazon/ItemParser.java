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
import com.ir.index.es.Indexer;
import com.ir.util.StringUtil;

import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import static com.ir.config.retailer.amazon.FieldNames.*;


public class ItemParser extends AbstractParser {

    private static final String[] DEL_TOKENS_MERCHANT = { "ships from and", "sold by", "gift-wrap available.",
            "in easy-to-open packaging.", "and fulfilled by amazon."};

    private static final String[] EXCLUSION_CATEGORIES = { "mp3 downloads", "music", "kindle", "appstore",
            "video games", "books", "video", "magazine", "movies", "gift" };

    public ItemParser(){
        super("http://www.amazon.com/", Indexer.INDEX_TYPE_ITEM, "amazon");
        decisionFields = ImmutableSet.of(
            fb(ID).q("input[id=ASIN]", "value").q("input[name*=ASIN]", "value").notNull(ParseError.MISSING_ID).c(),
            fb(TITLE).q("title").notNull().del("amazon.com( *)(:*)", "^-", "-$").excludeIf("protection plan").c(), // "-$" --> replace '-' char appearing last
            fb(CAT).excludeIf(EXCLUSION_CATEGORIES).c(),
            fb(CAT_NAV).q("li[class*=nav-category-button]").q("#nav-subnav", "data-category").notNull().excludeIf(EXCLUSION_CATEGORIES).c(),
            fb(BRAND).q("#brand").q("a[href*=brandtextbin]").q("#mbc", "data-brand").q("a[href*=fb-keywords]").notNull().c()
        );
        fields = ImmutableSet.of(
            fb(TITLE_DISPLAY).q("h1[id=title]").q("#btAsinTitle").q("h1[class*=parseasinTitle]").c(),
            fb(BREADCRUMB).q("div[class=detailBreadcrumb]").c(),
            fb(MERCHANT).q("#merchant-info").q("div[class=buying] > b").del(DEL_TOKENS_MERCHANT).c(),
            fb(PRC_LIST, Float.class).q("td[class*=a-text-strike]").q("#listPriceValue").del(DEL_TOKENS_PRICE).c(),
            fb(PRC_ACTUAL, Float.class).q("#priceblock_ourprice").q("#actualPriceValue").del(DEL_TOKENS_PRICE).c(),
            fb(PRC_SALE, Float.class).q("#priceblock_saleprice").del(DEL_TOKENS_PRICE).c(),
            fb(PRC_MIN, Float.class).c(),
            fb(PRC_MAX, Float.class).c(),
            fb(IDF_MODEL).q("li:contains(item model number:)").q("td:contains(item model number:) ~ td").del("item model number:").c(),
            fb(IDF_UPC).q("li:contains(upc:)").q("td:contains(upc:) ~ td").del("upc:").c(),
            fb(IDF_ISBN10).q("li:contains(ISBN-10)").del("isbn-10:").c(),
            fb(IDF_ISBN13).q("li:contains(ISBN-13)").del("isbn-13:").c(),
            fb(RANK_L1).q("#SalesRank").del("best sellers rank", "amazon", ":").c(),
            fb(RANK_L2).depends(RANK_L1).c(),
            fb(RANK_L3).depends(RANK_L2).c(),
            fb(REVIEW_AVG, Float.class).q("#avgRating").c(),
            fb(REVIEW_COUNT, Integer.class).q("#summaryStars").del(",").c(),
            fb(VRNT_SPEC).q("div[class=disclaim]").depends(VRNT_IDS).c(),
            fb(VRNT_IDS).q(new RawStringQuery("script[data-a-state*=twisterData]")).c(),
            fb(URL_CAN).q("link[rel=canonical]", "href").notNull().c(),
            fb(URL_IMG).q("#landingImage", "src").q("#main-image", "src").q("#prodImage", "src").notNull().c()
        );
        itemRules = ImmutableSet.of(
            new AtleastOneRule(ParseError.MISSING_ITEM_CLASSIFIER, RANK_L1, BREADCRUMB),
            new AtleastOneRule(ParseError.MISSING_PRICE, PRC_LIST, PRC_ACTUAL, PRC_MIN, PRC_MAX),
            (ItemRule)new AtleastOneRule(ParseError.MISSING_IDENTIFIER, IDF_MODEL, IDF_UPC, IDF_ISBN10, IDF_ISBN13)
        );
    }

    public boolean finalizeAndAddValue(Map<Field, Object> dataMap, Field field, String input){
        // PRICE
        String name = field.getName();
        if(name.equals(PRC_LIST)){
            if(input.trim().contains(" ")){
                String prices[] = input.split(" ");
                input = prices[0].trim();
                dataMap.put(getField(PRC_ACTUAL), prices[1].trim());
            } else {
                input = StringUtil.dedupeString(input, false);
            }
        } else if(name.equals(TITLE)){
            String[] tokens = input.split(":");
            input = tokens[0].trim();
            if(tokens.length > 1) {
                dataMap.put(getField(CAT), tokens[tokens.length - 1].trim());
            }
        } else if(name.equals(PRC_ACTUAL)){
            if(input.contains("-")){
                String prices[] = input.split("-");
                dataMap.put(getField(PRC_MIN), prices[0].trim());
                dataMap.put(getField(PRC_MAX), prices[1].trim());
                input = null;
            }
        }else if(name.equals(MERCHANT)){
            if(input.endsWith("."))
                input = input.substring(0, input.length()-1);
        // RANK
        } else if(name.equals(RANK_L1)){
            String[] ranks  = input.split("#");
            dataMap.put(getField(RANK_L1), ranks[1].split("\\(")[0]);
            if(ranks.length > 2)dataMap.put(getField(RANK_L2), ranks[2]);
            if(ranks.length > 3)dataMap.put(getField(RANK_L3), ranks[3]);
        // RATING
        } else if(name.equals(REVIEW_AVG)){
            input = input.split(" out")[0];
        } else if(name.equals(REVIEW_COUNT)){
            input = input.split("\\(")[1].split("\\)")[0];
        // VARIANT
        } else if(name.equals(VRNT_SPEC)){
            input = StringUtil.dedupeString(input, false);
        } else if(name.equals(VRNT_IDS)){
            Set<String> variantIds = new TreeSet<String>();
            for( Map.Entry<String, JsonElement> e : ((JsonObject) jsonParser.parse(getVariationValues(input))).entrySet()){
                variantIds.add(e.getKey());
            }
            dataMap.put(field, variantIds.toArray(new String[]{}));
        } else if(name.equals(IDF_UPC)){
            dataMap.put(field, input.split(" "));
        }
        if(dataMap.get(field) == null) dataMap.put(field, input);
        return true;
    }

    public void dedupeValues(Map<Field, Object> dataMap){
        String title = (String)dataMap.get(getField(TITLE));
        String titleDisplay = (String)dataMap.get(getField(TITLE));
        if(title != null && titleDisplay != null){
            if(title.equals(titleDisplay)){
                dataMap.remove(getField(TITLE_DISPLAY));
            }
        }
        String cat = (String)dataMap.get(getField(CAT));
        String navCat = (String)dataMap.get(getField(CAT_NAV));
        if(cat != null && navCat != null){
            if(cat.equals(navCat)){
                dataMap.remove(getField(CAT_NAV));
            }
        }
    }

    private static final String VARIANT_STR_TOKEN_1 = "asin_variation_values\":";

    private static final String VARIANT_STR_TOKEN_2 = ",\"variation_values\"";

    private static String getVariationValues(String input){
        return input.split(VARIANT_STR_TOKEN_1)[1].split(VARIANT_STR_TOKEN_2)[0];
    }

}
