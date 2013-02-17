package eu.prismsw.lampshade.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

/** Helper class for the database */
public class SavedArticlesHelper extends SQLiteOpenHelper {
	
	public static final String TABLE_SAVED = "saved";
    public static final String TABLE_RECENT = "recent";
    public static final String TABLE_FAVORITE = "favorite";

	public static final String ARTICLES_COLUMN_ID = "_id";
	public static final String ARTICLES_COLUMN_TITLE = "title";
	public static final String ARTICLES_COLUMN_URL = "url";

	protected static final String DATABASE_NAME = "saved_articles.db";
	protected static final int DATABASE_VERSION = 2;

    public SavedArticlesHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	public SavedArticlesHelper(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
        createTable(db, TABLE_SAVED);
        createTable(db, TABLE_RECENT);
        createTable(db, TABLE_FAVORITE);
	}

    private void createTable (SQLiteDatabase db, String table) {
        db.execSQL("create table " + table + "( "
                + ARTICLES_COLUMN_ID + " integer primary key autoincrement, "
                + ARTICLES_COLUMN_TITLE + " text not null, "
                + ARTICLES_COLUMN_URL + " text not null" + ");");
    }

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_SAVED);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECENT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVORITE);
		onCreate(db);
	}
}
