package com.syn3rgy.tropeswrapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.net.Uri;

/**  A wrapper for TvTropes articles that server mainly as an index page */
public class TropesIndex extends TropesArticle {
	
	public List<TropeListItem> tropes;

	public TropesIndex(Uri url, TropesIndexSelector selector) throws IOException {
		super(url);
		parseTropeList(content, selector.selector);
	}
	
	/** Finds the items matching the selector and returns the attributes of a link in those items */
	private void parseTropeList(Element content, String selector) {
		ArrayList<TropeListItem> tropes = new ArrayList<TropeListItem>();
		
		Elements items = content.select(selector);
		
		for(Element item : items) {
			Element a = item.getElementsByTag("a").first();
			String title = a.text();
			String url = a.attr("href").toString();
			tropes.add(new TropeListItem(title, url));
		}
		
		this.tropes = tropes;
	}
}
