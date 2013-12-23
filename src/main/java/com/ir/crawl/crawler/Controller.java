package com.ir.crawl.crawler;


import com.ir.config.retailer.AbstractConfig;
import com.ir.config.retailer.Config;
import com.ir.config.retailer.amazon.ItemCrawler;
import com.ir.core.crawllib.crawler.CrawlConfig;
import com.ir.core.crawllib.crawler.CrawlController;
import com.ir.core.crawllib.fetcher.PageFetcher;
import com.ir.core.crawllib.robotstxt.RobotstxtConfig;
import com.ir.core.crawllib.robotstxt.RobotstxtServer;

public class Controller {

    public static void main(String[] args) throws Exception {
        startCrawlProcess("amazon");
    }


    public static void startCrawlProcess(String crawlProcessKey) throws Exception {

        Config config = AbstractConfig.getConfigInstance(crawlProcessKey);
        CrawlConfig crwalConfig = new CrawlConfig();
        crwalConfig.setCrawlStorageFolder(config.getCrawlStorageLocation());
        crwalConfig.setMaxPagesToFetch(config.getMaxPagesToFetch());

        PageFetcher pageFetcher = new PageFetcher(crwalConfig);
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
        CrawlController controller = new CrawlController(crwalConfig, pageFetcher, robotstxtServer);

        //Exclude Items
        controller.addSeed("http://www.amazon.com/Big-Christmas-Box-Various-artists/dp/B00A2KV7XW");
        controller.addSeed("http://www.amazon.com/dp/B00B8YSQOE");
        //controller.addSeed("http://www.amazon.com/Hadoop-Practice-Alex-Holmes/dp/1617290238");

        controller.start(ItemCrawler.class, config.getNoOfCrawlers());

    }


}
