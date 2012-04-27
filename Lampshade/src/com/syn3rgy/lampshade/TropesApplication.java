package com.syn3rgy.lampshade;

import android.app.Application;

/** Provides cross-activity data */
public class TropesApplication extends Application {
	public final String randomUrl = "http://tvtropes.org/pmwiki/randomitem.php?p=1";
	public final String baseUrl = "http://tvtropes.org/pmwiki/pmwiki.php/Main/";
	
	public SavedArticlesSource articlesSource = null;
	
	@Override
	public void onCreate() {
		articlesSource = new SavedArticlesSource(this);
	}
}
