package com.ir.config.retailer;


public interface Config {

    public int getMaxPagesToFetch();

    public int getNoOfCrawlers();

    public Class getCrawlClass();

    public String getCrawlStorageLocation();
}
