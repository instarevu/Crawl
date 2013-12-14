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

        //controller.addSeed("http://www.drugstore.com/ddrops-baby-vitamin-d3-400iu/qxp378610?catid=183172");
        controller.addSeed("http://www.amazon.com/Transcend-Class-Flash-Memory-TS32GSDHC10E/dp/B003VNKNF0/ref=pd_cp_p_2");

        controller.start(Crawler.class, numberOfCrawlers);

    }

}