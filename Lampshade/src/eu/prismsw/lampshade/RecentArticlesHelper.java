package eu.prismsw.lampshade;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/** Helper class for the database */
public class RecentArticlesHelper extends SQLiteOpenHelper {
	
	public static final String TABLE_ARTICLES = "articles";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_TITLE = "title";
	public static final String COLUMN_URL = "url";
	
	private static final String DATABASE_NAME = "recent_articles.db";
	private static final int DATABASE_VERSION = 1;
	
	private static final String DATABASE_CREATE = "create table " + TABLE_ARTICLES + "( "
					+ COLUMN_ID + " integer primary key autoincrement, "
					+ COLUMN_TITLE + " text not null, "
					+ COLUMN_URL + " text not null" + ");";
	

	public RecentArticlesHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(DATABASE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_ARTICLES);
		onCreate(db);
	}
}
