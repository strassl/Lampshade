package com.syn3rgy.tropeswrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.net.Uri;

/** Provides helper functions*/
public class TropesHelper {
	
	/** Gets the page's title from the url */
	public static String titleFromUrl(Uri url) {
		List<String> segments = url.getPathSegments();
		String title = segments.get(segments.size() - 1);
		
		if(title.indexOf("?") != -1) {
			title = title.substring(0, title.indexOf("?"));
		}
		
		return title;
	}

	/** Gets the page's title from the url (in String form) **/
	public static String titleFromUrl(String url) {
		return titleFromUrl(Uri.parse(url));
	}
	
	public static Boolean isTropesLink(Uri url) {
		String host = url.getHost();
		Pattern pattern = Pattern.compile("([.*]\\.)?tvtropes\\.org");
		Matcher matcher = pattern.matcher(host);
		
		return matcher.matches();
	}
	
	/** Finds a matching selector or returns a generic one */
	public static TropesIndexSelector findMatchingSelector(List<TropesIndexSelector> selectors, String title) {
		// Check if a predefined selector exists for this page
		for(TropesIndexSelector s : selectors) {
			if(title.equals(s.page)) {
				// If it exists, return it
				return s;
			}
		}
		
		// Else return a generic selector for the index page
		return new TropesIndexSelector(title, "li");
	}
	
	/** Finds a matching selector for the url or returns a generic one */
	public static TropesIndexSelector findMatchingSelector(List<TropesIndexSelector> selectors, Uri url) {
		return findMatchingSelector(selectors, titleFromUrl(url));
	}
	
	/** Converts a list of TropesLinks into List of <a> tags **/
	public static List<String> linkListToHtmlList(List<TropesLink> links) {
		List<String> tags = new ArrayList<String>();
		
		for(TropesLink link : links) {
			tags.add(linkToHtml(link));
		}
		
		return tags;
	}
	
	/** Converts a TropesLink into an <a> tag **/
	public static String linkToHtml(TropesLink link) {
		String html = "<a href =\"" + link.url + "\" >" + link.title + "</a>";
		return html;
	}
}
