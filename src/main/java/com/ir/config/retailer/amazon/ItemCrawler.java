package com.ir.config.retailer.amazon;


import com.ir.core.crawllib.crawler.Page;
import com.ir.core.crawllib.crawler.WebCrawler;
import com.ir.core.crawllib.parser.HtmlParseData;
import com.ir.core.crawllib.url.WebURL;
import com.ir.core.error.DecisionError;
import com.ir.core.error.Error;
import com.ir.core.error.ErrorUtil;
import com.ir.crawl.parse.bean.ParseResponse;
import com.ir.index.es.Indexer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ItemCrawler extends WebCrawler {

    protected static final Logger logger = LogManager.getLogger(ItemCrawler.class.getName());

    private static AtomicInteger count = new AtomicInteger();

    public ItemCrawler(){
        super(new ItemParser());
    }


    public boolean shouldVisit(WebURL url) {
        String href = url.getURL().toLowerCase();
        return( !GENERIC_EXCLUSION_FILTERS.matcher(href).matches() && href.startsWith(baseURI) && href.contains("/dp/") ) ;
    }

    private static final String LOG_STATUS = "%s [%d-%d] [%4s] %s ";

    private static final String LOG_FAIL_STATUS = "%s [%d-%d] [%4s] %s - %s";

    @Override
    public void visit(Page page) {
        String url = page.getWebURL().getURL();
        if (page.getParseData() instanceof HtmlParseData) {
            HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
            ParseResponse parseResponse = parser.parseAll(htmlParseData.getDocument());
            String relativeUrl = url.replaceAll(baseURI, "");
            if(parseResponse.isEligibleForProcessing()){
                logger.info(String.format(LOG_STATUS, parser.getParserLabel(), getMyId(), count.incrementAndGet(), INDEX_STATUS.DONE, relativeUrl));
                Indexer.addDoc(parser, parseResponse.getDataMap());
            } else{
                if(ErrorUtil.isErrorCodePresent(parser.getErrorField(), parseResponse.getDataMap(), DecisionError.CAT_EXCLUSION)){
                    logger.info(String.format(LOG_STATUS, parser.getParserLabel(), getMyId(), count.incrementAndGet(), INDEX_STATUS.EXCL, relativeUrl));
                    Indexer.addExItemTypeDoc(parser, parseResponse.getDataMap());
                } else {
                    List<Error> errors = ErrorUtil.getErrorCodes(parser.getErrorField(), parseResponse.getDataMap());
                    logger.info(String.format(LOG_FAIL_STATUS, parser.getParserLabel(), getMyId(), count.incrementAndGet(), INDEX_STATUS.FAIL, relativeUrl, ErrorUtil.getErrorDescription(errors)));
                }
             }
            try {
                Thread.sleep(0);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public enum INDEX_STATUS {
        DONE, EXCL, FAIL;
    }
}