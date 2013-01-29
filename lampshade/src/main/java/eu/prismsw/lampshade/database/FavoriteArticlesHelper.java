package eu.prismsw.lampshade.database;

import android.content.Context;

/** Helper class for the database */
public class FavoriteArticlesHelper extends SavedArticlesHelper {
	protected static final String DATABASE_NAME = "favorite_articles.db";
	protected static final int DATABASE_VERSION = 1;
	
	public FavoriteArticlesHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
}
