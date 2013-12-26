package com.ir.config.retailer.amazon;

import com.ir.config.retailer.AbstractRetailerConfig;


public class AmazonItemRetailerConfig extends AbstractRetailerConfig {

    public static final String KEY = "amazon-item";

    public AmazonItemRetailerConfig(){
        super(ItemCrawler.class, "http://www.amazon.com", KEY);
        addSeed("http://www.amazon.com/dp/B0019LVFSU");
        addSeed("http://www.amazon.com/dp/B00FNPD1VW");
        //Exclude Items
        addSeed("http://www.amazon.com/Big-Christmas-Box-Various-artists/dp/B00A2KV7XW");
    }

}