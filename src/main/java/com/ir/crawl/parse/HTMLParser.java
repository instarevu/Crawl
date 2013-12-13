package com.ir.crawl.parse;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;

public class HTMLParser {

    public static void main(String[] args) throws Exception{
        // http://www.amazon.com/Transcend-Class-Flash-Memory-TS32GSDHC10E/dp/B003VNKNF0/ref=pd_cp_p_2
		File input = new File("/Users/sathiya/Desktop/B003VNKNF0.html");
		org.jsoup.nodes.Document doc = Jsoup.parse(input, "UTF-8", "http://www.amazon.com/");

		String canonicalUrl = "";
		String price = "";
		String titleText = "";
		String imageUrl = "";
		//org.jsoup.nodes.Document doc = Jsoup.connect("http://www.amazon.com/Transcend-Class-Flash-Memory-TS32GSDHC10E/dp/B003VNKNF0/ref=pd_cp_p_2").get();
		Elements links = doc.getElementsByTag("link");
		for (Element link : links) {
			  String linkRel = link.attr("rel");
			  //System.out.println("Link Rel: " + linkRel );
			  if(linkRel.equalsIgnoreCase("canonical")){
				  canonicalUrl = link.attr("href");
				  System.out.println("Canonical URL: " + canonicalUrl );
				  break;
			  }
		}
		
		Elements spans = doc.getElementsByTag("span");
		for (Element span : spans) {
			  String attrValue = span.attr("id");
			  if(attrValue.equalsIgnoreCase("priceblock_ourprice")){
				  price = span.text();
				  System.out.println("Price: " +  price);
				  break;
			  }
		}
		
		Elements titles = doc.getElementsByTag("h1");
		for (Element title : titles) {
			  String attrValue = title.attr("id");
			  if(attrValue.equalsIgnoreCase("title")){
				  titleText = title.text();
				  System.out.println("Title: " +  titleText);
				  break;
			  }
		}
		
		Elements scripts = doc.getElementsByTag("script");
		for (Element script : scripts) {
			  String value = script.data();
			  if(value.contains("prefetchURL")){
				  imageUrl = value.split("=")[1].split(";")[0].replaceAll("\"", "");
				  System.out.println("Value: " +  imageUrl);
				  break;
			  }
		}
    }

}
