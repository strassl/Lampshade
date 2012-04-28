package com.syn3rgy.tropeswrapper;


public class TropeListItem {
	public String title;
	public String url;
	
	public TropeListItem(String title, String url) {
		this.title = title;
		this.url = url;
	}
	
	@Override
	public String toString() {
		return this.title;
	}
}
