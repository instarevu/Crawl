package com.ir.crawl.parse.parser;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.io.Files;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ir.config.retailer.amazon.AmazonFieldNames;
import com.ir.crawl.parse.field.Field;
import com.ir.crawl.parse.field.FieldBuilder;
import com.ir.crawl.parse.query.RawStringQuery;
import com.ir.crawl.parse.validation.NotNullRule;
import com.ir.util.StringUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import static com.ir.config.retailer.amazon.AmazonFieldNames.*;

public class AmazonParser extends AbstractParser{

    private static final String BASE_URI = "http://www.amazon.com/";

    private static Set<Field> fields = ImmutableSet.of(
            new FieldBuilder(ID).addQuery("input[id=ASIN]", "value").addValidation(new NotNullRule()).create(),
            new FieldBuilder(BRAND).addQuery("h1[id=title]").addQuery("div[id=mbc]", "data-brand").addValidation(new NotNullRule()).create(),
            new FieldBuilder(PRICE_OFFER).addQuery("span[id=priceblock_ourprice]").addValidation(new NotNullRule()).create(),
            new FieldBuilder(PRICE_LIST).addQuery("td[class*=a-text-strike]").create(),
            new FieldBuilder(IDF_MODEL).addQuery("td[class*=a-text-strike]").addQuery("td:contains(Item model number) ~ td").create(),
            new FieldBuilder(IDF_UPC).addQuery("li:contains(UPC)").addQuery("td:contains(UPC) ~ td").create(),
            new FieldBuilder(IDF_ISBN_10).addQuery("li:contains(ISBN-10)").create(),
            new FieldBuilder(IDF_ISBN_13).addQuery("li:contains(ISBN-13)").create(),
            new FieldBuilder(RANK_L1_SPEC).addQuery("#SalesRank").create(),
            new FieldBuilder(RANK_L2_SPEC).create(),
            new FieldBuilder(RANK_L3_SPEC).create(),
            new FieldBuilder(RATING_AVG).addQuery("#avgRating").create(),
            new FieldBuilder(RATING_TOTAL).addQuery("#summaryStars").create(),
            new FieldBuilder(VARIANT_SPECS).addQuery("div[class=disclaim]").create(),
            new FieldBuilder(VARIANT_IDS).addQuery(new RawStringQuery("script[data-a-state*=twisterData]")).create(),
            new FieldBuilder(URL).addQuery("link[rel=canonical]", "href").addValidation(new NotNullRule()).create(),
            new FieldBuilder(URL_IMAGE).addQuery("img[id=landingImage]", "src").addValidation(new NotNullRule()).create()
    );

    public AmazonParser(){
        super(BASE_URI, fields);
    }

    private static final List<String> VARIANT_DELETE_TOKENS = ImmutableList.of(
            "<div class=\"disclaim\">", "<strong>", "</strong>", "&nbsp;", "</div>", "(\\r|\\n)");

    private static final List<String> RANK_DELETE_TOKENS = ImmutableList.of("Best Sellers Rank", "Amazon", ":");

    private static final List<String> IDNF_DELETE_TOKENS = ImmutableList.of("Item model number:", "UPC:");

    private static final String VARIANT_STR_TOKEN_1 = "asin_variation_values\":";

    private static final String VARIANT_STR_TOKEN_2 = ",\"variation_values\"";

    private static final JsonParser parser = new JsonParser();

    public boolean finalizeAndAddValue(Map<Field, Object> response , Field field, String input){
        // PRICE
        String fieldName = field.getName();
        if(fieldName.equalsIgnoreCase(AmazonFieldNames.PRICE_LIST)){
            input = StringUtil.dedupeString(input, false);
        } else if(fieldName.equalsIgnoreCase(AmazonFieldNames.PRICE_OFFER)){
            if(input.contains("-")){
                String prices[] = input.split("-");
                response.put(getFieldByName(PRICE_MIN), prices[0].trim());
                response.put(getFieldByName(PRICE_MAX), prices[1].trim());
                response.put(getFieldByName(PRICE_OFFER), "N/A");
            }
        // RANK
        } else if(fieldName.equalsIgnoreCase(RANK_L1_SPEC)){
            //Needed to take care of rogue special char
            input = StringUtil.deleteListOfTokens(input, RANK_DELETE_TOKENS);
            String[] ranks  = input.split("#");
            response.put(getFieldByName(RANK_L1_SPEC), ranks[1].split("\\(")[0]);
            if(ranks.length > 2)response.put(getFieldByName(RANK_L2_SPEC), ranks[2]);
            if(ranks.length > 3)response.put(getFieldByName(RANK_L3_SPEC), ranks[3]);
        // RATING
        } else if(fieldName.equalsIgnoreCase(RATING_AVG)){
            input = input.split(" out")[0];
        } else if(fieldName.equalsIgnoreCase(RATING_TOTAL)){
            input = input.split("\\(")[1].split("\\)")[0];
        // VARIANT
        } else if(fieldName.equalsIgnoreCase(VARIANT_SPECS)){
            input = StringUtil.deleteListOfTokens(input, VARIANT_DELETE_TOKENS);
            input = StringUtil.dedupeString(input, false);
        } else if(fieldName.equalsIgnoreCase(VARIANT_IDS)){
            JsonObject jsonObject = (JsonObject) parser.parse(getVariationValues(input));
            Set<String> variantIds = new TreeSet<String>();
            for( Map.Entry<String, JsonElement> e : jsonObject.entrySet()){
                variantIds.add(e.getKey());
            }
            input = variantIds.toString();
        } else if(fieldName.equalsIgnoreCase(IDF_MODEL) || fieldName.equalsIgnoreCase(IDF_UPC)){
            input = StringUtil.deleteListOfTokens(input, IDNF_DELETE_TOKENS);
        }

        if(response.get(field) == null){
            response.put(field, input);
        }
        return true;
    }

    private static String getVariationValues(String input){
        return input.split(VARIANT_STR_TOKEN_1)[1].split(VARIANT_STR_TOKEN_2)[0];
    }

    public static void main(String[] args) throws Exception{
        File file = new File("/Users/sathiya/Work/Git/ir/Crawl/src/test/resources/amazon/data");

        for (File f : file.listFiles()){
            String data = Files.toString(f, Charsets.UTF_8);
            Document doc = Jsoup.parse(data, BASE_URI);
            System.out.println(f.getName() + "2: " + doc.select("li:contains(Item model number)").text());
            System.out.println();
        }

    }


}
