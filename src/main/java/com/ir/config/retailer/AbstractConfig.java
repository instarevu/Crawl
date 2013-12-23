package com.ir.config.retailer;


import com.ir.config.retailer.amazon.AmazonConfig;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractConfig implements Config{

    protected String baseURI = null;

    protected int maxPagesToFetch = 10;

    protected int noOfCrawlers = 1;

    protected Class crawlClass = null;

    protected String crawlStorageLocation = "var/data/crawl/";

    protected AbstractConfig(String baseURI, String crawlStorageLocationPrefix){
        this.baseURI = baseURI;
        this.crawlStorageLocation += crawlStorageLocationPrefix;
    }

    public int getMaxPagesToFetch() {
        return maxPagesToFetch;
    }

    public int getNoOfCrawlers() {
        return noOfCrawlers;
    }

    public Class getCrawlClass() {
        return crawlClass;
    }

    public String getCrawlStorageLocation() {
        return crawlStorageLocation;
    }

    private static Map<String, Config> configInstanceMap = new HashMap<String, Config>(5);

    public synchronized static Config getConfigInstance(String retailerKey){

        if(configInstanceMap.get(retailerKey) == null){
            if(retailerKey.equalsIgnoreCase("amazon")){
                configInstanceMap.put(retailerKey, new AmazonConfig());
            }
        }
        return configInstanceMap.get(retailerKey);
    }


}
