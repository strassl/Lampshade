package eu.prismsw.lampshade;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


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
}
