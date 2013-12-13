
package com.ir.core.crawllib.parser;



import com.ir.core.crawllib.url.WebURL;

import java.util.List;

public class HtmlParseData implements ParseData {

	private String html;
	private String text;
	private String title;

	private List<WebURL> outgoingUrls;

	public String getHtml() {
		return html;
	}

	public void setHtml(String html) {
		this.html = html;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<WebURL> getOutgoingUrls() {
		return outgoingUrls;
	}

	public void setOutgoingUrls(List<WebURL> outgoingUrls) {
		this.outgoingUrls = outgoingUrls;
	}
	
	@Override
	public String toString() {
		return text;
	}

}
