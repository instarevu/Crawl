package com.ir.crawl.crawler;


import com.ir.config.retailer.AbstractRetailerConfig;
import com.ir.config.retailer.RetailerConfig;
import com.ir.config.retailer.amazon.AmazonItemRetailerConfig;
import com.ir.core.crawllib.crawler.CrawlConfig;
import com.ir.core.crawllib.crawler.CrawlController;
import com.ir.core.crawllib.fetcher.PageFetcher;
import com.ir.core.crawllib.robotstxt.RobotstxtConfig;
import com.ir.core.crawllib.robotstxt.RobotstxtServer;

public class Controller {

    public static void main(String[] args) throws Exception {
        startCrawlProcess(AmazonItemRetailerConfig.KEY);
    }

    public static void startCrawlProcess(String crawlProcessKey) throws Exception {
        RetailerConfig retailerConfig = AbstractRetailerConfig.getConfigInstance(crawlProcessKey);
        CrawlConfig crawlConfig = new CrawlConfig();
        crawlConfig.setCrawlStorageFolder(retailerConfig.getCrawlStorageLocation());
        crawlConfig.setMaxPagesToFetch(retailerConfig.getMaxPagesToFetch());

        PageFetcher pageFetcher = new PageFetcher(crawlConfig);
        RobotstxtServer robotstxtServer = new RobotstxtServer(new RobotstxtConfig(), pageFetcher);
        CrawlController controller = new CrawlController(crawlConfig, pageFetcher, robotstxtServer);

        for(String seed : retailerConfig.getSeeds()){
            controller.addSeed(seed);
        }
        controller.start(retailerConfig.getCrawlClass(), retailerConfig.getNoOfCrawlers());
    }

}
