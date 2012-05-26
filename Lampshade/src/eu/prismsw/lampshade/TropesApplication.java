package eu.prismsw.lampshade;

import java.util.ArrayList;
import java.util.List;

import eu.prismsw.tropeswrapper.TropesHelper;
import eu.prismsw.tropeswrapper.TropesIndexSelector;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;

/** Provides cross-activity data */
public class TropesApplication extends Application {
	public static final String loadAsArticle = "ASARTICLE";
	
	public static final String helpUrl = "http://lampshade.prismsw.eu/help.html";
	
	public static final String randomUrl = "http://tvtropes.org/pmwiki/randomitem.php?p=1";
	public static final String baseUrl = "http://tvtropes.org/pmwiki/pmwiki.php/Main/";
	public static final String tropesUrl = "http://tvtropes.org/pmwiki/pmwiki.php/Main/Tropes";
	public List<TropesIndexSelector> indexPages;
		
	public SavedArticlesSource articlesSource = null;
	
	@Override
	public void onCreate() {
		articlesSource = new SavedArticlesSource(this);
		indexPages =  new ArrayList<TropesIndexSelector>();
		// TODO A horrible way to add all the items, maybe some kind of xml file would be a better idea
		// TODO Automation would be even better
		indexPages.add(new TropesIndexSelector("Tropes", "li"));
		indexPages.add(new TropesIndexSelector("NarrativeTropes", "li"));
		indexPages.add(new TropesIndexSelector("GenreTropes", "li"));
		indexPages.add(new TropesIndexSelector("CharacterizationTropes", "li"));
	}
	
	public void switchTheme(Activity activity) {
		String theme = getThemeName();
		
		if(theme.equalsIgnoreCase("HoloDark")) {
			activity.setTheme(android.R.style.Theme_Holo);
		}
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

    	if(isIndex(page)) {
    		loadIndex(url);
    	}
    	else {
    		loadArticle(url);
    	}
	}
	
	public Boolean isIndex(String title) {
		//Dirty, but it works and the failure rate is pretty low
		//If it should fail, the user can still view the page as an article
		if(title.matches(".*(Index|index|Tropes|tropes).*")) {
			indexPages.add(new TropesIndexSelector(title, "li"));
			return true;
		}
    	for(TropesIndexSelector selector : indexPages) {
    		if(selector.page.equals(title)) {
    			return true;
    		}
    	}
    	return false;
	}
	
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
	
    public void openActivity(Class<?> cls) {
    	Intent intent = new Intent(getApplicationContext(), cls);
    	intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    	startActivity(intent);
    }
}
