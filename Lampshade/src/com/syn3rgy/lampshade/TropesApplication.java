package com.syn3rgy.lampshade;

import com.syn3rgy.tropeswrapper.TropesHelper;

import android.app.Application;
import android.content.Intent;
import android.net.Uri;

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
	
	public void loadPage(String url) {
		String page = TropesHelper.titleFromUrl(Uri.parse(url));
		
		Boolean isIndex = false;
    	for(String p : TropesApplication.indexPages) {
    		if(p.equalsIgnoreCase(page)) {
    			isIndex = true;
    			break;
    		}
    	}
    	
    	if(isIndex) {
    		loadIndex(url);
    	}
    	else {
    		loadArticle(url);
    	}
	}
	
	public void loadArticle(String url) {
		Intent articleIntent = new Intent(getApplicationContext(), ArticleActivity.class);
		articleIntent.setData(Uri.parse(url));
		articleIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    	startActivity(articleIntent);
	}
	
	public void loadIndex(String url) {
		Intent indexIntent = new Intent(getApplicationContext(), ArticleActivity.class);
		indexIntent.setData(Uri.parse(url));
		indexIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    	startActivity(indexIntent);
	}
	
    public void openActivity(Class<?> cls) {
    	Intent intent = new Intent(getApplicationContext(), cls);
    	intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    	startActivity(intent);
    }
}
