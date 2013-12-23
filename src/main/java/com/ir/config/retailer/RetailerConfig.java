package com.ir.config.retailer;


import java.util.List;

public interface RetailerConfig {

    public int getMaxPagesToFetch();

    public int getNoOfCrawlers();

    public Class getCrawlClass();

    public String getCrawlStorageLocation();

    public void addSeed(String seed);

    public List<String> getSeeds();
}
