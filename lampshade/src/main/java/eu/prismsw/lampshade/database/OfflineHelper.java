package eu.prismsw.lampshade.database;

import android.content.Context;

/** Helper class for the database */
public class OfflineHelper extends SavedArticlesHelper {
	
	public static final String ARTICLES_COLUMN_HTML = "html";
	
	public static final String TABLE_RESSOURCES = "ressources";
	public static final String RES_COLUMN_ID = "_id";
	public static final String RES_COLUMN_NAME = "name";
	public static final String RES_COLUMN_URL = "url";
	public static final String RES_COLUMN_TYPE = "type";
	public static final String RES_COLUMN_DATA = "data";
	
	protected static final String DATABASE_NAME = "offline.db";
	protected static final int DATABASE_VERSION = 1;
	
	protected static final String DATABASE_CREATE = "create table " + TABLE_ARTICLES + "( "
					+ ARTICLES_COLUMN_ID + " integer primary key autoincrement, "
					+ ARTICLES_COLUMN_TITLE + " text not null, "
					+ ARTICLES_COLUMN_URL + " text not null, "
					+ ARTICLES_COLUMN_HTML + " text not null" + ");"
					
					+ "create table " + TABLE_RESSOURCES + "( "
					+ RES_COLUMN_ID + " integer primary key autoincrement, "
					+ RES_COLUMN_NAME + " text not null, "
					+ RES_COLUMN_URL + " text not null, "
					+ RES_COLUMN_TYPE + " text not null, "
					+ RES_COLUMN_DATA + " text not null" + ");";
	

	public OfflineHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
}
