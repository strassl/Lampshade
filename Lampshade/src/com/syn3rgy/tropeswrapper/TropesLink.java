package com.syn3rgy.tropeswrapper;


public class TropesLink {
	public String title;
	public String url;
	
	public TropesLink(String title, String url) {
		this.title = title;
		this.url = url;
	}
	
	@Override
	public String toString() {
		return this.title;
	}
}
