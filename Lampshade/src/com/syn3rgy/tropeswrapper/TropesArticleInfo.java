package com.syn3rgy.tropeswrapper;

import java.util.List;

public class TropesArticleInfo {
	public String title;
	public String url;
	public List<TropesLink> buttons;
	
	public TropesArticleInfo(String title, String url, List<TropesLink> buttons) {
		this.title = title;
		this.url = url;
		this.buttons = buttons;
	}
}
