package com.syn3rgy.tropeswrapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.net.Uri;

public class TropesIndex extends TropesArticle {
	
	public List<TropeListItem> tropes;

	public TropesIndex(Uri url) throws IOException {
		super(url);
	}
	
	@Override
	protected void parseArticle(Document doc) {
		Element wikibody = doc.getElementById("wikibody");
		
		//Split the document into title and content
		Element title = wikibody.getElementById("wikititle").getElementsByClass("pagetitle").first().getElementsByTag("span").first();
		this.title = title.text();
		
		Element content = wikibody.getElementById("wikitext");
		changeLinkStyle(content);
		hideSpoilers(content);
		
		parseTropeList(content);
		
		this.content = content.html();
	}
	
	private void parseTropeList(Element content) {
		ArrayList<TropeListItem> tropes = new ArrayList<TropeListItem>();
		
		Elements listItems = content.getElementsByTag("li");
		
		for(Element li : listItems) {
			Element a = li.getElementsByTag("a").first();
			String title = a.text();
			String url = a.attr("href").toString();
			tropes.add(new TropeListItem(title, url));
		}
		
		this.tropes = tropes;
	}

}
