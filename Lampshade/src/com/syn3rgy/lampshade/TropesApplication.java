package com.syn3rgy.lampshade;

import android.app.Application;

/** Provides cross-activity data */
public class TropesApplication extends Application {
	public static final String randomUrl = "http://tvtropes.org/pmwiki/randomitem.php?p=1";
	public static final String baseUrl = "http://tvtropes.org/pmwiki/pmwiki.php/Main/";
	public static final String tropesUrl = "http://tvtropes.org/pmwiki/pmwiki.php/Main/Tropes";
	
	public static final String[] indexPages = {"Tropes", "NarrativeTropes", "GenreTropes" };
	
	public SavedArticlesSource articlesSource = null;
	
	@Override
	public void onCreate() {
		articlesSource = new SavedArticlesSource(this);
	}
}
