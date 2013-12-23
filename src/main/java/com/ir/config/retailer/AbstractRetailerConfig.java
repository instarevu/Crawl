package com.ir.config.retailer;


import com.ir.config.retailer.amazon.AmazonItemRetailerConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractRetailerConfig implements RetailerConfig {

    protected String baseURI = null;

    protected int maxPagesToFetch = 10;

    protected int noOfCrawlers = 1;

    protected List<String> seedList = new ArrayList<String>(10);

    protected Class crawlClass = null;

    protected String crawlStorageLocation = "var/data/crawl/";

    protected AbstractRetailerConfig(Class crawlClazz, String baseURI, String crawlStorageLocationPrefix){
        this.crawlClass = crawlClazz;
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

    public void addSeed(String seed){
        this.seedList.add(seed);
    }

    public List<String> getSeeds(){
        return seedList;
    }

    private static Map<String, RetailerConfig> configInstanceMap = new HashMap<String, RetailerConfig>(5);

    public synchronized static RetailerConfig getConfigInstance(String retailerKey){

        if(configInstanceMap.get(retailerKey) == null){
            if(retailerKey.equalsIgnoreCase(AmazonItemRetailerConfig.KEY)){
                configInstanceMap.put(retailerKey, new AmazonItemRetailerConfig());
            }
        }
        return configInstanceMap.get(retailerKey);
    }


}
