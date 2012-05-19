package com.syn3rgy.tropeswrapper;

import java.util.List;

import android.net.Uri;

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
