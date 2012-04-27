package com.syn3rgy.tropeswrapper;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import android.net.Uri;

public class TropesArticle {
	public String url = null;
	
	public String title;
	public String content;
	

	public TropesArticle(Uri url) throws IOException {
		loadArticle(url);
	}
	
	public TropesArticle(String title, String url, String content) {
		this.title = title;
		this.url = url;
		this.content = content;
	}
	
	private void loadArticle(Uri url) throws IOException {
		Document doc = Jsoup.connect(url.toString()).get();
				
		Element wikibody = doc.getElementById("wikibody");
		
		//Split the document into title and content
		Element title = wikibody.getElementById("wikititle").getElementsByClass("pagetitle").first();
		this.title = title.text();
		
		Element content = wikibody.getElementById("wikitext");
		content = hideSpoilers(content);
		this.content = content.html();
	}
	
	private Element hideSpoilers(Element content) {
		// The hover style is triggered on touch and thus a viable workaround for onClick
		String style = "<style type=\"text/css\">.spoiler { background-color:black; } .spoiler:hover { background-color:transparent; } </style>";
		content.prepend(style);
		return content;
	}
}
