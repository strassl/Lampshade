package com.syn3rgy.tropeswrapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.net.Uri;

public class TropesIndex extends TropesArticle {
	
	public List<TropeListItem> tropes;

	public TropesIndex(Uri url, TropesIndexSelector selector) throws IOException {
		super(url);
		parseTropeList(content, selector.selector);
	}
	
/*	@Override
	protected void parseArticle(Document doc) {
		Element wikibody = doc.getElementById("wikibody");
		
		//Split the document into title and content
		Element title = wikibody.getElementById("wikititle").getElementsByClass("pagetitle").first().getElementsByTag("span").first();
		this.title = title.text();
		
		Element content = wikibody.getElementById("wikitext");
		changeLinkStyle(content);
		hideSpoilers(content);
		this.content = content;
	}*/
	
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
