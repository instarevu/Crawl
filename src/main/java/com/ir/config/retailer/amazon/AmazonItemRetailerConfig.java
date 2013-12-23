package com.ir.config.retailer.amazon;

import com.ir.config.retailer.AbstractRetailerConfig;


public class AmazonItemRetailerConfig extends AbstractRetailerConfig {

    public static final String KEY = "amazon-item";

    public AmazonItemRetailerConfig(){
        super(ItemCrawler.class, "http://www.amazon.com", KEY);
        //Exclude Items
        addSeed("http://www.amazon.com/Big-Christmas-Box-Various-artists/dp/B00A2KV7XW");
        addSeed("http://www.amazon.com/dp/B00B8YSQOE");
        //addSeed("http://www.amazon.com/Hadoop-Practice-Alex-Holmes/dp/1617290238");

    }

}