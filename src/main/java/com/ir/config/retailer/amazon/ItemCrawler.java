package com.ir.config.retailer.amazon;


import com.ir.core.crawllib.crawler.Page;
import com.ir.core.crawllib.crawler.WebCrawler;
import com.ir.core.crawllib.parser.HtmlParseData;
import com.ir.core.crawllib.url.WebURL;
import com.ir.crawl.parse.bean.ParseResponse;
import com.ir.index.es.Indexer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.atomic.AtomicInteger;

public class ItemCrawler extends WebCrawler {

    protected static final Logger logger = LogManager.getLogger(ItemCrawler.class.getName());

    private AtomicInteger count = new AtomicInteger();

    public ItemCrawler(){
        super(new ItemParser());
    }


    public boolean shouldVisit(WebURL url) {
        String href = url.getURL().toLowerCase();
        return( !GENERIC_EXCLUSION_FILTERS.matcher(href).matches() && href.contains("/dp/") && href.startsWith(baseURI) ) ;
    }


    @Override
    public void visit(Page page) {
        String url = page.getWebURL().getURL();
        if (page.getParseData() instanceof HtmlParseData) {
            HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
            ParseResponse parseResponse = parser.parseAll(htmlParseData.getDocument());
            if(parseResponse.isEligibleForProcessing()){
                logger.info(parser.getParserLabel() + this.getMyId() + "-" + count.incrementAndGet() + " " + url.replaceAll(baseURI, ""));
                Indexer.addDoc(parser, parseResponse.getDataMap());
            } else{
                logger.info(parser.getParserLabel() + this.getMyId() + "-" +  count.incrementAndGet() + " " + url.replaceAll(baseURI, "") + "   -- Excluded. " + parseResponse.getDataMap().get(parser.getErrorField()));
             }
            try {
                Thread.sleep(0);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}