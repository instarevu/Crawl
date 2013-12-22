package com.ir.core.crawllib.parser;


import com.ir.core.crawllib.crawler.Configurable;
import com.ir.core.crawllib.crawler.CrawlConfig;
import com.ir.core.crawllib.crawler.Page;
import com.ir.core.crawllib.url.URLCanonicalizer;
import com.ir.core.crawllib.url.WebURL;
import com.ir.core.crawllib.util.Util;
import com.ir.util.StringUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


public class Parser extends Configurable {

    protected static final Logger logger = LogManager.getLogger(Parser.class.getName());

	public Parser(CrawlConfig config) {
		super(config);
	}

	public boolean parse(Page page, String contextURL) {

		if (Util.hasBinaryContent(page.getContentType())) {
			if (!config.isIncludeBinaryContentInCrawling()) {
				return false;
			}

			page.setParseData(BinaryParseData.getInstance());
			return true;

		} else if (Util.hasPlainTextContent(page.getContentType())) {
			try {
				TextParseData parseData = new TextParseData();
				if (page.getContentCharset() == null) {
					parseData.setTextContent(new String(page.getContentData()));
				} else {
					parseData.setTextContent(new String(page.getContentData(), page.getContentCharset()));
				}
				page.setParseData(parseData);
				return true;
			} catch (Exception e) {
				logger.error(e.getMessage() + ", while parsing: " + page.getWebURL().getURL());
			}
			return false;
		}

		InputStream inputStream = null;
        Document document = null;
        try {
			inputStream = new ByteArrayInputStream(page.getContentData());
            String data = StringUtil.convertStreamToString(inputStream);
            document = Jsoup.parse(data, "http://www.amazon.com");
		} catch (Exception e) {
			logger.error(e.getMessage() + ", while parsing: " + page.getWebURL().getURL());
		} finally {
			try {
				if (inputStream != null) {
					inputStream.close();
				}
			} catch (IOException e) {
				logger.error(e.getMessage() + ", while parsing: " + page.getWebURL().getURL());
			}
		}


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
