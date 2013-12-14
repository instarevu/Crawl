package com.ir.crawl.parse;


import com.ir.core.crawllib.crawler.Page;
import com.ir.core.crawllib.crawler.WebCrawler;
import com.ir.core.crawllib.parser.HtmlParseData;
import com.ir.core.crawllib.url.WebURL;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.regex.Pattern;

public class Crawler extends WebCrawler {

    protected static final Logger logger = Logger.getLogger(Crawler.class.getName());

    private final static Pattern EXCLUSION_FILTERS = Pattern.compile(".*(\\.(css|js|bmp|gif|jpe?g"
            + "|png|tiff?|mid|mp2|mp3|mp4"
            + "|wav|avi|mov|mpeg|ram|m4v|pdf"
            + "|rm|smil|wmv|swf|wma|zip|rar|gz))$");


    public boolean shouldVisit(WebURL url) {
        String href = url.getURL().toLowerCase();

        boolean shouldVisit = !EXCLUSION_FILTERS.matcher(href).matches()
                && href.contains("/dp")
                && ( href.startsWith("http://www.amazon.com/") || href.startsWith("http://www.drugstore.com/") ) ;
        return shouldVisit;
    }

    @Override
    public void visit(Page page) {
        String url = page.getWebURL().getURL();
        logger.info("Visiting URL: " + url);

        if (page.getParseData() instanceof HtmlParseData) {
            HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
            String text = htmlParseData.getText();
            String html = htmlParseData.getHtml();
            List<WebURL> links = htmlParseData.getOutgoingUrls();
            //AmazonParser.parseHTML(html);

            System.out.println("Text length: " + text.length());
            System.out.println("Html length: " + html.length());
            System.out.println("Number of outgoing links: " + links.size());
        }
    }



}