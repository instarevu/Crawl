
package com.ir.core.crawllib.parser;



import com.ir.core.crawllib.url.WebURL;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class HtmlParseData implements ParseData {

	private Document document;

	private List<WebURL> outgoingUrls;

	public Document getDocument() {
		return document;
	}

	public void setDocument(Document document) {
		this.document = document;
	}

	public List<WebURL> getOutgoingUrls() {
		return outgoingUrls;
	}

	public void setOutgoingUrls(List<WebURL> outgoingUrls) {
		this.outgoingUrls = outgoingUrls;
	}
	

    public List<String> getHrefs(){
        //TODO: Add more link tags AREA, LINK, IFRAME, FRAME, EMBED, IMG, BASE, META, BODY
        Elements linkElements  = document.getElementsByTag("a");
        List<String> hrefs = new ArrayList<String>(linkElements.size());
        for(Element e : linkElements){
            hrefs.add(e.attr("href"));
        }
        return hrefs;
    }

}
