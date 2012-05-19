package com.syn3rgy.tropeswrapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.net.Uri;

/** Wrapper for a TvTropes article */
public class TropesArticle {
	public Uri url = null;
	
	public String title;
	public Element content;
	
	public List<TropesLink> subpages;
	
	private String linkColor = "#33B5E5";
	private String spoilerColor = "#000000";
	
	public TropesArticle(Uri url) throws Exception {
		Document doc = loadArticle(url);
		parseArticle(doc);
		parseSubpages(doc);
	}
	
	/** Returns the Jsoup document of the url */
	protected Document loadArticle(Uri url) throws IOException {
		Response resp = Jsoup.connect(url.toString()).execute();
		// We can only set this here due to possible redirects
		this.url = Uri.parse(resp.url().toString());
		Document doc = resp.parse();
				
		return doc;
	}
	
	/** Splits the document into title and content */
	protected void parseArticle(Document doc) throws TropesArticleParseException{
		try {
			Element wikibody = doc.getElementById("wikibody");
			
			//Split the document into title and content
			Element title = wikibody.getElementById("wikititle").getElementsByClass("pagetitle").first().getElementsByTag("span").first();
			this.title = title.text();
			
			Element content = wikibody.getElementById("wikitext");
			changeLinkStyle(content);
			hideSpoilers(content);
			
			this.content = content;
		}
		catch (Exception e) {
			throw new TropesArticleParseException("parseArticle");
		}
	}
	
	/** Extracts the subpages from the page */
	protected void parseSubpages(Document doc) throws TropesArticleParseException{
		try {
			Element wikititle = doc.getElementById("wikititle");
			Element buttons = wikititle.getElementsByClass("namespacebuttons").first();
			Elements links = buttons.getElementsByTag("a");
			
			this.subpages =  new ArrayList<TropesLink>();
			
			for(Element link : links) {
				String title = link.text().trim();
				
				if(title.isEmpty()) {
					title = link.attr("title").trim();
				}
				
				if(title.isEmpty()) {
					Element img = link.getElementsByTag("img").first();
					title = img.attr("title");
				}
				String url = link.attr("href");
				this.subpages.add(new TropesLink(title, Uri.parse(url)));
			}
		}
		catch(Exception e) {
			throw new TropesArticleParseException("parseSubpages");
		}
	}
	
	protected void injectButtons(Elements links) {
		content.prepend("<hr />");
		for(Element link : links) {
			link.text(link.text() + " |");
			content.prependChild(link);
		}
	}
	
	/** Combines a List of css selectors into a stylesheet and inserts it into the page */
	protected void insertStylesheet(Element element, List<String> selectors) {
		String style = "";
		for(String selector : selectors) {
			style += selector;
		}
		
		String style_tag = "<style type=\"text/css\">" + style + "</style>";
		element.prepend(style_tag);
	}
	
	/** Inserts a stylesheet that changes the colour of links */
	protected void changeLinkStyle(Element content) {
		ArrayList<String> selectors = new ArrayList<String>();
		selectors.add("a { color:" + linkColor + ";" + " }");
		insertStylesheet(content, selectors);
	}
	
	/** Modifies the hover state of .spoiler elements */
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
