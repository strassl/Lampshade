package com.syn3rgy.tropeswrapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import android.net.Uri;

public class TropesArticle {
	public String url = null;
	
	public String title;
	public Element content;
	
	private String linkColor = "#33B5E5";
	private String spoilerColor = "#000000";
	
	public TropesArticle(Uri url) throws IOException {
		Document doc = loadArticle(url);
		parseArticle(doc);
	}
	
	protected Document loadArticle(Uri url) throws IOException {
		Response resp = Jsoup.connect(url.toString()).execute();
		this.url = resp.url().toString();
		Document doc = resp.parse();
				
		return doc;
	}
	
	protected void parseArticle(Document doc) {
		Element wikibody = doc.getElementById("wikibody");
		
		//Split the document into title and content
		Element title = wikibody.getElementById("wikititle").getElementsByClass("pagetitle").first().getElementsByTag("span").first();
		this.title = title.text();
		
		Element content = wikibody.getElementById("wikitext");
		changeLinkStyle(content);
		hideSpoilers(content);
		
		this.content = content;
	}
		
	protected void insertStylesheet(Element element, List<String> selectors) {
		String style = "";
		for(String selector : selectors) {
			style += selector;
		}
		
		String style_tag = "<style type=\"text/css\">" + style + "</style>";
		element.prepend(style_tag);
	}
	
	protected void changeLinkStyle(Element content) {
		ArrayList<String> selectors = new ArrayList<String>();
		selectors.add("a { color:" + linkColor + ";" + " }");
		insertStylesheet(content, selectors);
	}
	
	protected void hideSpoilers(Element content) {
		// The hover style is triggered on touch and thus a viable workaround for onClick
		ArrayList<String> selectors = new ArrayList<String>();
		selectors.add(".spoiler { background-color:" + spoilerColor + ";" + "color:" + spoilerColor + "; }");
		selectors.add(".spoiler a { color:" + spoilerColor + "; }");
		selectors.add(".spoiler:hover { background-color:transparent; }");
		selectors.add(".spoiler:hover a { color:" + linkColor + "; }");
		insertStylesheet(content, selectors);
	}
}
