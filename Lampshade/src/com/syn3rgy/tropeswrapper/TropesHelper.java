package com.syn3rgy.tropeswrapper;

import java.util.List;

import android.net.Uri;

/** Provides helper functions */
public class TropesHelper {
	
	public static String titleFromUrl(Uri url) {
		List<String> segments = url.getPathSegments();
		String title = segments.get(segments.size() - 1);
		
		if(title.indexOf("?") != -1) {
			title = title.substring(0, title.indexOf("?"));
		}
		
		return title;
	}
	
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
	
	public static TropesIndexSelector findMatchingSelector(List<TropesIndexSelector> selectors, Uri url) {
		return findMatchingSelector(selectors, titleFromUrl(url));
	}
	
}
