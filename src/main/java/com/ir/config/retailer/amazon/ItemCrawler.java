package com.ir.config.retailer.amazon;


import com.ir.core.crawllib.crawler.Page;
import com.ir.core.crawllib.crawler.WebCrawler;
import com.ir.core.crawllib.parser.HtmlParseData;
import com.ir.core.crawllib.url.WebURL;
import com.ir.crawl.parse.bean.ParseResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ItemCrawler extends WebCrawler {

    protected static final Logger logger = LogManager.getLogger(ItemCrawler.class.getName());

    public ItemCrawler(){
        super(new ItemParser(),  "http://www.amazon.com/");
    }


    public boolean shouldVisit(WebURL url) {
        String href = url.getURL().toLowerCase();
        return( !GENERIC_EXCLUSION_FILTERS.matcher(href).matches() && href.contains("/dp/") && href.startsWith(baseURI) ) ;
    }


    @Override
    public void visit(Page page) {
        String url = page.getWebURL().getURL();
        //logger.info("Visiting URL: " + url);

        if (page.getParseData() instanceof HtmlParseData) {
            HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
            ParseResponse parseResponse = valueParser.parseAll(htmlParseData.getDocument());
            logger.info("URL: " + url.replaceAll(baseURI, "") + "    :: " + parseResponse.getDataMap().toString());
            try {
                Thread.sleep(0);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}