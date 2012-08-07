package eu.prismsw.tropeswrapper;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import android.net.Uri;

public class OfflineArticle extends TropesArticle {
	String mainJS = "";
	
	public OfflineArticle(String html, Uri url, String mainJS, TropesArticleSettings settings) throws Exception {
		super();
		Document doc = loadArticle(html, url);
		this.mainJS = mainJS;
		parseArticle(doc, settings);
	}
	
	/** Return the Jsoup document for the provided html */
	protected Document loadArticle(String html, Uri url) {
		// We still need to have all the information about the article, thus the url has to be provided
		this.url = url;
		Document doc = Jsoup.parse(html);
		return doc;
	}
	
	@Override
	public void parseArticle(Document doc, TropesArticleSettings settings) throws TropesArticleParseException, IOException {
		this.settings = settings;
		this.title = getTitle(doc);
		this.content = getContent(doc);
		this.subpages = getSubpages(doc);
		
		inlineJS(this.content, this.mainJS);
		manipulateStyle(this.content, settings);
	}
}
