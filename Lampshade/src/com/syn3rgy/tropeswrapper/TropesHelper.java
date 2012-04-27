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
	
}
