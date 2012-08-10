package eu.prismsw.lampshade.database;

import android.content.Context;

/** Helper class for the database */
public class RecentArticlesHelper extends SavedArticlesHelper {
	private static final String DATABASE_NAME = "recent_articles.db";
	private static final int DATABASE_VERSION = 1;

	public RecentArticlesHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
}
