package com.ir.crawl.parse.parser;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableSet;
import com.google.common.io.Files;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.ir.config.retailer.amazon.AmazonFieldNames;
import com.ir.crawl.parse.field.Field;
import com.ir.crawl.parse.query.RawStringQuery;
import com.ir.crawl.parse.validation.NotNullRule;
import com.ir.util.StringUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import static com.ir.config.retailer.amazon.AmazonFieldNames.*;


public class AmazonParser extends AbstractParser {

    private static Set<Field> fields = ImmutableSet.of(
            Field.n(ID).addQ("input[id=ASIN]", "value").addV(new NotNullRule()).c(),
            Field.n(TITLE).addQ("h1[id=title]").addV(new NotNullRule()).c(),
            Field.n(BRAND).addQ("div[id=mbc]", "data-brand").addQ("a[id=brand]").addV(new NotNullRule()).c(),
            Field.n(PRC_OFFER).addQ("span[id=priceblock_ourprice]").addV(new NotNullRule()).c(),
            Field.n(PRC_LIST).addQ("td[class*=a-text-strike]").c(),
            Field.n(IDF_MODEL).addQ("li:contains(Item model number)").addQ("td:contains(Item model number) ~ td").del("Item model number:").c(),
            Field.n(IDF_UPC).addQ("li:contains(UPC)").addQ("td:contains(UPC) ~ td").del("UPC:").c(),
            Field.n(IDF_ISBN10).addQ("li:contains(ISBN-10)").c(),
            Field.n(IDF_ISBN13).addQ("li:contains(ISBN-13)").c(),
            Field.n(RANK_L1).addQ("#SalesRank").del("Best Sellers Rank", "Amazon", ":").c(),
            Field.n(RANK_L2).c(),
            Field.n(RANK_L3).c(),
            Field.n(RTNG_AVG).addQ("#avgRating").c(),
            Field.n(RTNG_TOTAL).addQ("#summaryStars").c(),
            Field.n(VRNT_SPEC).addQ("div[class=disclaim]").del("<div class=\"disclaim\">", "<strong>", "</strong>", "&nbsp;", "</div>", "(\\r|\\n)").c(),
            Field.n(VRNT_IDS).addQ(new RawStringQuery("script[data-a-state*=twisterData]")).c(),
            Field.n(URL).addQ("link[rel=canonical]", "href").addV(new NotNullRule()).c(),
            Field.n(URL_IMG).addQ("img[id=landingImage]", "src").addV(new NotNullRule()).c()
    );

    public AmazonParser(){
        super("http://www.amazon.com/", fields);
    }

    public boolean finalizeAndAddValue(Map<Field, Object> dataMap , Field field, String input){
        // PRICE
        String fieldName = field.getName();
        if(fieldName.equalsIgnoreCase(AmazonFieldNames.PRC_LIST)){
            input = StringUtil.dedupeString(input, false);
        } else if(fieldName.equalsIgnoreCase(AmazonFieldNames.PRC_OFFER)){
            if(input.contains("-")){
                String prices[] = input.split("-");
                dataMap.put(getFieldByName(PRC_MIN), prices[0].trim());
                dataMap.put(getFieldByName(PRC_MAX), prices[1].trim());
                dataMap.put(getFieldByName(PRC_OFFER), "N/A");
            }
        // RANK
        } else if(fieldName.equalsIgnoreCase(RANK_L1)){
            String[] ranks  = input.split("#");
            dataMap.put(getFieldByName(RANK_L1), ranks[1].split("\\(")[0]);
            if(ranks.length > 2)dataMap.put(getFieldByName(RANK_L2), ranks[2]);
            if(ranks.length > 3)dataMap.put(getFieldByName(RANK_L3), ranks[3]);
        // RATING
        } else if(fieldName.equalsIgnoreCase(RTNG_AVG)){
            input = input.split(" out")[0];
        } else if(fieldName.equalsIgnoreCase(RTNG_TOTAL)){
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

    public static void main(String[] args) throws Exception{
        File file = new File("/Users/sathiya/Work/Git/ir/Crawl/src/test/resources/amazon/data");

        for (File f : file.listFiles()){
            String data = Files.toString(f, Charsets.UTF_8);
            Document doc = Jsoup.parse(data, "http://www.amazon.com/");
            System.out.println(f.getName() + "2: " + doc.select("li:contains(Item model number)").text());
            System.out.println();
        }

    }


}
