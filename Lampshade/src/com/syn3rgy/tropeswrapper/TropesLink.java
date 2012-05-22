package com.syn3rgy.tropeswrapper;

import android.net.Uri;

/** A simple link wrapper with title and url **/
public class TropesLink {
	public String title;
	public Uri url;
	
	public TropesLink(String title, Uri url) {
		this.title = title;
		this.url = url;
	}
	
	@Override
	public String toString() {
		return this.title;
	}
}
