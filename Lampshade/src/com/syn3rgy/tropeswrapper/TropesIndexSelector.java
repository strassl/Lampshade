package com.syn3rgy.tropeswrapper;

/** Holds information about which element contains the list elements of a page **/
public class TropesIndexSelector {
	/* 
	 * page is the title of the page (e.g. TheDragon, note the lack of spaces)
	 * selector can be any kind of valid Jsoup selector. It has to contain an a tag though.
	 */
	public String page;
	public String selector;
	
	public TropesIndexSelector(String page, String selector) {
		this.page = page;
		this.selector = selector;
	}
}
