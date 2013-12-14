package com.ir.crawl.parse.parser;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import com.google.common.io.Files;
import com.ir.crawl.parse.ParseQuery;

import java.io.File;
import java.util.Map;

public class AmazonParser extends AbstractParser {

    public AmazonParser(){
        this.baseURI = "http://www.amazon.com/";
        this.productQueries = ImmutableList.of(
            new ParseQuery("Canonical URL", "link[rel=canonical]", "href"),
            new ParseQuery("Title", "h1[id=title]"),
            new ParseQuery("Price Offered", "span[id=priceblock_ourprice]"),
            new ParseQuery("Price Listed", "td[class*=a-text-strike]"),
            new ParseQuery("Brand", "a[id=brand]"),
            new ParseQuery("ASIN", "input[id=ASIN]", "value"),
            new ParseQuery("Image", "img[id=landingImage]", "src"));
    }


    public static void main(String[] args) throws Exception {

    }

}
