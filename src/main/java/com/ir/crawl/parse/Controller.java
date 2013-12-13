package com.ir.crawl.parse;


import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

public class Controller {

    public static final String CRAWL_STORAGE_FOLDER = "data/crawl/root";

    public static void main(String[] args) throws Exception {

        int numberOfCrawlers = 1;

        CrawlConfig config = new CrawlConfig();
        config.setMaxPagesToFetch(1);
        config.setCrawlStorageFolder(CRAWL_STORAGE_FOLDER);

        PageFetcher pageFetcher = new PageFetcher(config);
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
        CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);

        controller.addSeed("http://www.drugstore.com/ddrops-baby-vitamin-d3-400iu/qxp378610?catid=183172");
        controller.start(Crawler.class, numberOfCrawlers);

    }

}