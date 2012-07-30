package eu.prismsw.lampshade;

import java.util.ArrayList;
import java.util.List;

import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;

import eu.prismsw.lampshade.database.ArticlesSource;
import eu.prismsw.lampshade.database.FavoriteArticlesHelper;
import eu.prismsw.lampshade.database.RecentArticlesHelper;
import eu.prismsw.lampshade.database.SavedArticlesHelper;
import eu.prismsw.tropeswrapper.TropesHelper;
import eu.prismsw.tropeswrapper.TropesIndexSelector;


/** Provides cross-activity data and functionality */
public class TropesApplication extends Application {
	public static final String loadAsArticle = "ASARTICLE";

	public static final String remoteUrl = "http://lampshade.prismsw.eu/";
	public static final String versionUrl = "http://lampshade.prismsw.eu/version.xml";
	public static final String helpUrl = "http://lampshade.prismsw.eu/help.html";
	
	public static final Integer maxRecentArticles = 15;
	
	public List<TropesIndexSelector> indexPages;
	public ArticlesSource savedArticlesSource = null;
	public ArticlesSource recentArticlesSource = null;
	public ArticlesSource favoriteArticlesSource = null;
	
	@Override
	public void onCreate() {
		savedArticlesSource = new ArticlesSource(new SavedArticlesHelper(this));
		recentArticlesSource = new ArticlesSource(new RecentArticlesHelper(this));
		favoriteArticlesSource = new ArticlesSource(new FavoriteArticlesHelper(this));
		
		indexPages =  new ArrayList<TropesIndexSelector>();
		// TODO A horrible way to add all the items, maybe some kind of xml file would be a better idea
		// TODO Automation would be even better
		indexPages.add(new TropesIndexSelector("Tropes", "li"));
		indexPages.add(new TropesIndexSelector("NarrativeTropes", "li"));
		indexPages.add(new TropesIndexSelector("GenreTropes", "li"));
		indexPages.add(new TropesIndexSelector("CharacterizationTropes", "li"));
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
	
    public void openActivity(Class<?> cls) {
    	Intent intent = new Intent(getApplicationContext(), cls);
    	intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    	startActivity(intent);
    }
    
}
