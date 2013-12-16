package com.ir.crawl.parse.parser;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import com.google.common.io.Files;
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
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.util.*;

public class AmazonParser extends AbstractParser{

    private static final String BASE_URI = "http://www.amazon.com/";

    public AmazonParser(){
        this.baseURI = BASE_URI;
        this.dataElements = ImmutableList.of(
            //CORE
            new DataElement(true, new AttrValueQuery("input[id=ASIN]", "value"), AmazonFields.ID, MinerType.ATTRIBUTE),
            new DataElement(true, new TextQuery("h1[id=title]"), AmazonFields.TITLE, MinerType.TEXT),
            //BRAND
            new DataElement(true, new TextQuery("a[id=brand]"), AmazonFields.BRAND, MinerType.TEXT),
            new DataElement(true, new AttrValueQuery("div[id=mbc]", "data-brand"), AmazonFields.BRAND, MinerType.ATTRIBUTE),
            //PRICE
            new DataElement(true, new TextQuery("span[id=priceblock_ourprice]"), AmazonFields.PRICE_OFFER, MinerType.TEXT),
            new DataElement(false, new TextQuery("td[class*=a-text-strike]"), AmazonFields.PRICE_LIST, MinerType.TEXT),
            //IDENTIFIER
            new DataElement(false, new TextQuery("li:contains(Item model number)"), AmazonFields.IDF_MODEL, MinerType.TEXT),
            new DataElement(false, new TextQuery("td:contains(Item model number) ~ td"), AmazonFields.IDF_MODEL, MinerType.TEXT),
            new DataElement(false, new TextQuery("li:contains(UPC)"), AmazonFields.IDF_UPC, MinerType.TEXT),
            new DataElement(false, new TextQuery("td:contains(UPC) ~ td"), AmazonFields.IDF_UPC, MinerType.TEXT),
            new DataElement(false, new TextQuery("li:contains(ISBN-10)"), AmazonFields.IDF_ISBN_10, MinerType.TEXT),
            new DataElement(false, new TextQuery("li:contains(ISBN-13)"), AmazonFields.IDF_ISBN_13, MinerType.TEXT),
            //RANK
            new DataElement(false, new TextQuery("#SalesRank"), AmazonFields.RANK_L1_SPEC, MinerType.TEXT),
            new DataElement(false, new TextQuery("#avgRating"), AmazonFields.RATING_AVG, MinerType.TEXT),
            new DataElement(false, new TextQuery("#summaryStars"), AmazonFields.RATING_TOTAL, MinerType.TEXT),
            //VARIANT
            new DataElement(false, new TextQuery("div[class=disclaim]"), AmazonFields.VARIANT_SPECS, MinerType.TEXT),
            new DataElement(false, new RawStringQuery("script[data-a-state*=twisterData]"), AmazonFields.VARIANT_IDS, MinerType.RAW_STRING),
            //MISC
            new DataElement(true, new AttrValueQuery("link[rel=canonical]", "href"), AmazonFields.URL, MinerType.ATTRIBUTE),
            new DataElement(true, new AttrValueQuery("img[id=landingImage]", "src"), AmazonFields.URL_IMAGE, MinerType.ATTRIBUTE)
        );
    }

    private static final List<String> VARIANT_DELETE_TOKENS = ImmutableList.of(
            "<div class=\"disclaim\">", "<strong>", "</strong>", "&nbsp;", "</div>", "(\\r|\\n)");

    private static final List<String> RANK_DELETE_TOKENS = ImmutableList.of("Best Sellers Rank", "Amazon", ":");

    private static final List<String> IDNF_DELETE_TOKENS = ImmutableList.of("Item model number:", "UPC:");

    private static final String VARIANT_STR_TOKEN_1 = "asin_variation_values\":";

    private static final String VARIANT_STR_TOKEN_2 = ",\"variation_values\"";

    private static final JsonParser parser = new JsonParser();

    public String finalizeAndAddValue(Map<String, String> response , String field, String input){
        // PRICE
        if(field.equalsIgnoreCase(AmazonFields.PRICE_LIST)){
            input = StringUtil.cleanHTMLText(input);
            input = StringUtil.dedupeString(input, false);
        } else if(field.equalsIgnoreCase(AmazonFields.PRICE_OFFER)){
            input = StringUtil.cleanHTMLText(input);
            if(input.contains("-")){
                String prices[] = input.split("-");
                response.put(AmazonFields.PRICE_MIN, prices[0].trim());
                response.put(AmazonFields.PRICE_MAX, prices[1].trim());
                response.put(AmazonFields.PRICE_OFFER, "N/A");
            }
        // RANK
        } else if(field.equalsIgnoreCase(AmazonFields.RANK_L1_SPEC)){
            //Needed to take care of rogue special char
            input = StringUtil.cleanHTMLText(input);
            input = StringUtil.deleteListOfTokens(input, RANK_DELETE_TOKENS);
            String[] ranks  = input.split("#");
            response.put(AmazonFields.RANK_L1_SPEC, ranks[1].split("\\(")[0]);
            if(ranks.length > 2) response.put(AmazonFields.RANK_L2_SPEC, ranks[2]);
            if(ranks.length > 3)response.put(AmazonFields.RANK_L3_SPEC, ranks[3]);
        // RATING
        } else if(field.equalsIgnoreCase(AmazonFields.RATING_AVG)){
            input = input.split(" out")[0];
        } else if(field.equalsIgnoreCase(AmazonFields.RATING_TOTAL)){
            input = input.split("\\(")[1].split("\\)")[0];
        // VARIANT
        } else if(field.equalsIgnoreCase(AmazonFields.VARIANT_SPECS)){
            input = StringUtil.deleteListOfTokens(input, VARIANT_DELETE_TOKENS);
            input = StringUtil.cleanHTMLText(input);
            input = StringUtil.dedupeString(input, false);
        } else if(field.equalsIgnoreCase(AmazonFields.VARIANT_IDS)){
            JsonObject jsonObject = (JsonObject) parser.parse(getVariationValues(input));
            Set<String> variantIds = new TreeSet<String>();
            for( Map.Entry<String, JsonElement> e : jsonObject.entrySet()){
                variantIds.add(e.getKey());
            }
            input = variantIds.toString();
        } else if(field.equalsIgnoreCase(AmazonFields.IDF_MODEL) || field.equalsIgnoreCase(AmazonFields.IDF_UPC)){
            input = StringUtil.deleteListOfTokens(input, IDNF_DELETE_TOKENS);
        }

        if(response.get(field) == null){
            response.put(field, input);
        }

        return input;
    }

    private static String getVariationValues(String input){
        return input.split(VARIANT_STR_TOKEN_1)[1].split(VARIANT_STR_TOKEN_2)[0];
    }

    public static void main(String[] args) throws Exception{
        File file = new File("/Users/sathiya/Work/Git/ir/Crawl/src/test/resources/amazon/data");
        File[] files = file.listFiles();

        for (File f : files){
            String data = Files.toString(f, Charsets.UTF_8);
            Document doc = Jsoup.parse(data, "http://www.amazon.com/");
            System.out.println(f.getName() + "2: " + doc.select("li:contains(Item model number)").text());
            //System.out.println(f.getName() + "2: " + doc.select("#tmmSwatches").text());
            //System.out.println(f.getName() + "3: " + doc.select("td:contains(UPC) ~ td").text());
            System.out.println();
        }

    }


}
