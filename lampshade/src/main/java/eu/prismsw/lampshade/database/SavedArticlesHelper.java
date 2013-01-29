package eu.prismsw.lampshade.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

/** Helper class for the database */
public class SavedArticlesHelper extends SQLiteOpenHelper {
	
	public static final String TABLE_ARTICLES = "articles";
	public static final String ARTICLES_COLUMN_ID = "_id";
	public static final String ARTICLES_COLUMN_TITLE = "title";
	public static final String ARTICLES_COLUMN_URL = "url";
	
	protected static final String DATABASE_NAME = "saved_articles.db";
	protected static final int DATABASE_VERSION = 1;
	
	protected static final String DATABASE_CREATE = "create table " + TABLE_ARTICLES + "( "
					+ ARTICLES_COLUMN_ID + " integer primary key autoincrement, "
					+ ARTICLES_COLUMN_TITLE + " text not null, "
					+ ARTICLES_COLUMN_URL + " text not null" + ");";
	

	public SavedArticlesHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	public SavedArticlesHelper(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
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
	
	public Boolean recordExists(SQLiteDatabase db, Long id) {
		Cursor c = db.query(TABLE_ARTICLES, new String[] {ARTICLES_COLUMN_ID}, ARTICLES_COLUMN_ID + "= ?", new String[] {id.toString()}, null, null, null);
		Boolean exists = (c.getCount() > 0);
		c.close();
		return exists;
	}
	
	public Boolean recordExists(SQLiteDatabase db, Uri url) {
		Cursor c = db.query(TABLE_ARTICLES, new String[] {ARTICLES_COLUMN_ID}, ARTICLES_COLUMN_URL + "= ?", new String[] {url.toString()}, null, null, null);
		Boolean exists = (c.getCount() > 0);
		c.close();
		return exists;
	}

}
