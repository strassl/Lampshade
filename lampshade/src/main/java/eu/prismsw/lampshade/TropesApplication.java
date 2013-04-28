package eu.prismsw.lampshade;

import android.app.Application;


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
}
