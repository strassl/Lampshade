package com.syn3rgy.tropeswrapper;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.net.Uri;

/**  A wrapper for TvTropes articles that server mainly as an index page */
public class TropesIndex extends TropesArticle {
	
	public List<TropesLink> tropes;

	public TropesIndex(Uri url, TropesIndexSelector selector) throws Exception {
		super(url);
		this.tropes = parseTropeList(content, selector.selector);
	}
	
	/** Finds the items matching the selector and returns the attributes of a link in those items */
	private List<TropesLink> parseTropeList(Element content, String selector) throws TropesArticleParseException {
		try {
			ArrayList<TropesLink> tropes = new ArrayList<TropesLink>();
			
			Elements items = content.select(selector);
			
			for(Element item : items) {
				Element a = item.getElementsByTag("a").first();
				if(a != null) {
					String title = a.text();
					String url = a.attr("href").toString();
					tropes.add(new TropesLink(title, Uri.parse(url)));
				}
			}
			
			return tropes;
		}
		catch(Exception e) {
			throw new TropesArticleParseException("parseTropeList");
		}
	}
}
