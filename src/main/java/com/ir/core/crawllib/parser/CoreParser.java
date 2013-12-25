package com.ir.core.crawllib.parser;


import com.ir.core.crawllib.crawler.Configurable;
import com.ir.core.crawllib.crawler.CrawlConfig;
import com.ir.core.crawllib.crawler.Page;
import com.ir.core.crawllib.url.URLCanonicalizer;
import com.ir.core.crawllib.url.WebURL;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.List;


public class CoreParser extends Configurable {

    protected static final Logger logger = LogManager.getLogger(CoreParser.class.getName());

	public CoreParser(CrawlConfig config) {
		super(config);
	}

	public boolean parse(Page page, String contextURL) {
        //TODO: Fix context URL to be fetched from crawl config
        Document document = Jsoup.parse(page.getHtmlData(), "http://www.amazon.com");
		HtmlParseData parseData = new HtmlParseData();
		parseData.setDocument(document);

		List<WebURL> outgoingUrls = new ArrayList<WebURL>();

		String baseURL = "http://www.amazon.com/";
		if (baseURL != null) {
			contextURL = baseURL;
		}

		int urlCount = 0;
		for (String href : parseData.getHrefs()) {
			href = href.trim();
			if (href.length() == 0) {
				continue;
			}
			String hrefWithoutProtocol = href.toLowerCase();
			if (href.startsWith("http://")) {
				hrefWithoutProtocol = href.substring(7);
			}
			if (!hrefWithoutProtocol.contains("javascript:") && !hrefWithoutProtocol.contains("mailto:")
					&& !hrefWithoutProtocol.contains("@")) {
				String url = URLCanonicalizer.getCanonicalURL(href, contextURL);
				if (url != null) {
					WebURL webURL = new WebURL();
					webURL.setURL(url);
					webURL.setAnchor("");
					outgoingUrls.add(webURL);
					urlCount++;
					if (urlCount > config.getMaxOutgoingLinksToFollow()) {
						break;
					}
				}
			}
		}

		parseData.setOutgoingUrls(outgoingUrls);
		page.setParseData(parseData);
		return true;
	}

}
