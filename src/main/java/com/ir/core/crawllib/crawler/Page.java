
package com.ir.core.crawllib.crawler;

import com.google.common.base.Charsets;
import com.ir.core.crawllib.parser.ParseData;
import com.ir.core.crawllib.url.WebURL;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.util.EntityUtils;

import java.nio.charset.Charset;

/**
 * This class contains the data for a fetched and parsed page.
 */
public class Page {

    /**
     * The URL_CAN of this page.
     */
    protected WebURL url;

    /**
     * The content of this page in binary format.
     */
    protected byte[] contentData;

    protected String htmlData;

    /**
     * The ContentType of this page.
     * For example: "text/html; charset=UTF-8"
     */
    protected String contentType;

    /**
     * The encoding of the content.
     * For example: "gzip"
     */
    protected String contentEncoding;

    protected String contentCharset = "ISO-8859-1";
    
    protected Header[] fetchResponseHeaders;

    protected ParseData parseData;

	public Page(WebURL url) {
		this.url = url;
	}

	public WebURL getWebURL() {
		return url;
	}

	public void load(HttpEntity entity) throws Exception {

		contentType = null;
		Header type = entity.getContentType();
		if (type != null) {
			contentType = type.getValue();
		}

		contentEncoding = null;
		Header encoding = entity.getContentEncoding();
		if (encoding != null) {
			contentEncoding = encoding.getValue();
		}

		Charset charset = ContentType.getOrDefault(entity).getCharset();
		if (charset != null) {
			contentCharset = charset.displayName();	
		}

		contentData = EntityUtils.toByteArray(entity);
        htmlData = new String(contentData, contentCharset);
	}
	

	public void setFetchResponseHeaders(Header[] headers) {
		fetchResponseHeaders = headers;
	}

	public ParseData getParseData() {
		return parseData;
	}

	public void setParseData(ParseData parseData) {
		this.parseData = parseData;
	}

	public byte[] getContentData() {
		return contentData;
	}

    public String getHtmlData() {
        return htmlData;
    }

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getContentCharset() {
		return contentCharset;
	}

}
