package com.ir.config.retailer.amazon;

import com.google.common.collect.ImmutableList;
import com.ir.config.retailer.AbstractRetailerConfig;

import java.text.MessageFormat;
import java.util.List;


public class AmazonItemRetailerConfig extends AbstractRetailerConfig {

    public static final String KEY = "amazon-item";

    private static final MessageFormat PROD_DETAILS_URL = new MessageFormat("http://www.amazon.com/dp/{0}");

    private static final List<String> seedIds = ImmutableList.of(
            "B00AXN3628", "B000KE6E1U", "B000AUIFCA", "B007OXK1WI", "B003YFHCKY",
            "B00DDMJ0JE", "B0041RPGQ6", "B00AJL9E5C", "B003F82I2W", "B0033Y0VZ4",
            "B00B8YSQOE", "B008RLUY46", "B00121PZZG", "B001OOLF82", "B007UZNS5W",
            "B005GSYXHW", "B003UEMOWA", "B005IS7PDO", "B00850F5L6", "B000EFMLQ2"
    );

    public AmazonItemRetailerConfig(){
        super(ItemCrawler.class, "http://www.amazon.com", KEY);
        for(String seedId : seedIds){
            addSeed(PROD_DETAILS_URL.format(new Object[]{seedId}));
        }
    }

}