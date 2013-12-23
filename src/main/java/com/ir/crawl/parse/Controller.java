package com.ir.crawl.parse;


import com.ir.core.crawllib.crawler.CrawlConfig;
import com.ir.core.crawllib.crawler.CrawlController;
import com.ir.core.crawllib.fetcher.PageFetcher;
import com.ir.core.crawllib.robotstxt.RobotstxtConfig;
import com.ir.core.crawllib.robotstxt.RobotstxtServer;

public class Controller {

    public static final String CRAWL_STORAGE_FOLDER = "data/crawl/root";

    public static void main(String[] args) throws Exception {

        int numberOfCrawlers = 1;

        CrawlConfig config = new CrawlConfig();
        config.setMaxPagesToFetch(10);
        config.setCrawlStorageFolder(CRAWL_STORAGE_FOLDER);

        PageFetcher pageFetcher = new PageFetcher(config);
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
        CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);

        //Exclude Items
        //controller.addSeed("http://www.amazon.com/Big-Christmas-Box-Various-artists/dp/B00A2KV7XW");
        controller.addSeed("http://www.amazon.com/dp/B00G118LYY/ref=nav_sap_mas_13_12_22");

        //controller.addSeed("http://www.amazon.com/dp/B00B8YSQOE");
        //controller.addSeed("http://www.amazon.com/Hadoop-Practice-Alex-Holmes/dp/1617290238");

        controller.start(Crawler.class, numberOfCrawlers);

    }

}