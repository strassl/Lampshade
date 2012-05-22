package com.syn3rgy.tropeswrapper;

import java.util.List;

import android.net.Uri;

/** Contains all the important info about an article sans the content, which would need too much memory **/
public class TropesArticleInfo {
	public String title;
	public Uri url;
	public List<TropesLink> subpages;
	
	public TropesArticleInfo(String title, Uri url, List<TropesLink> subpages) {
		this.title = title;
		this.url = url;
		this.subpages = subpages;
	}
}
