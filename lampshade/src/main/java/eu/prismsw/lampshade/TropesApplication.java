package eu.prismsw.lampshade;

import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import eu.prismsw.tropeswrapper.TropesHelper;


/** Provides cross-activity data and functionality */
public class TropesApplication extends Application {
	public static final String loadAsArticle = "ASARTICLE";

	public static final String remoteUrl = "http://lampshade.prismsw.eu/";
	public static final String versionUrl = "http://lampshade.prismsw.eu/version.xml";
	public static final String helpUrl = "http://lampshade.prismsw.eu/help.html";
	
	public static final Integer maxRecentArticles = 15;

	@Override
	public void onCreate() {
	}
	
	public Boolean isDarkTheme() {
		Boolean isDark = false;
		isDark = getThemeName().equalsIgnoreCase("HoloDark");
		
		return isDark;
	}
	
	public String getThemeName() {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		String theme = preferences.getString("preference_theme", "HoloLight");
		return theme;
	}
	
	public void loadWebsite(Uri url) {
		Intent websiteIntent = new Intent(Intent.ACTION_VIEW);
		websiteIntent.setData(url);
		websiteIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(websiteIntent);
	}
	
	public void loadPage(Uri url) {
		String page = TropesHelper.titleFromUrl(url);
		
    	if(TropesHelper.isIndex(page)) {
    		loadIndex(url);
    	}
    	else {
    		loadArticle(url);
    	}
	}
	
	/** Opens a page as an article, and only as an article **/
	public void loadArticle(Uri url) {
		Intent articleIntent = new Intent(getApplicationContext(), ArticleActivity.class);
		articleIntent.putExtra(TropesApplication.loadAsArticle, true);
		articleIntent.setData(url);
		articleIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    	startActivity(articleIntent);
	}
	
	public void loadIndex(Uri url) {
		Intent indexIntent = new Intent(getApplicationContext(), ArticleActivity.class);
		indexIntent.setData(url);
		indexIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    	startActivity(indexIntent);
	}
    
}
