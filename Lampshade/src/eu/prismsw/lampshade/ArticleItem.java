package eu.prismsw.lampshade;

import android.net.Uri;

import eu.prismsw.tropeswrapper.*;

/** Represents a saved article from the database */
public class ArticleItem {
	public long id;
	public String title;
	public Uri url;
	
	public ArticleItem(long id, String title, Uri url) {
		this.id = id;
		this.title = title;
		this.url = url;
	}
	
	public ArticleItem(long id, Uri url) {
		this.id = id;
		this.title = TropesHelper.titleFromUrl(url);
		this.url = url;
	}
	
	
	// Returns the title for the ListView
	@Override
	public String toString() {
		return this.title;
	}
}
