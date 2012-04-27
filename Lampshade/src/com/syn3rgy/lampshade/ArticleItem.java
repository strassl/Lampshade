package com.syn3rgy.lampshade;

import android.net.Uri;

import com.syn3rgy.tropeswrapper.*;

/** Represents a saved article from the database */
public class ArticleItem {
	long id;
	String title;
	Uri url;
	
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
