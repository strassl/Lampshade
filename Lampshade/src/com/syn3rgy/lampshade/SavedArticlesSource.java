package com.syn3rgy.lampshade;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

/** Wrapper for common database functions */
public class SavedArticlesSource {
	
	private SQLiteDatabase database;
	private SavedArticlesHelper helper;
	private String[] allColumns = {SavedArticlesHelper.COLUMN_ID, SavedArticlesHelper.COLUMN_TITLE, SavedArticlesHelper.COLUMN_URL};
	
	public SavedArticlesSource(Context context) {
		helper = new SavedArticlesHelper(context);
	}
	
	public void open() throws SQLException {
		database = helper.getWritableDatabase();
	}
	
	public void close() {
		helper.close();
	}
	
	// Saves the title and URL, returns an object with the newly created id
	public ArticleItem createArticleItem (String title, Uri url) {
		ContentValues values = new ContentValues();
		values.put(SavedArticlesHelper.COLUMN_TITLE, title);
		values.put(SavedArticlesHelper.COLUMN_URL, url.toString());
		
		long id = database.insert(SavedArticlesHelper.TABLE_ARTICLES, null, values);
		
		return new ArticleItem(id, title, url);
	}
	
	public void removeArticle(ArticleItem item) {
		database.delete(SavedArticlesHelper.TABLE_ARTICLES, SavedArticlesHelper.COLUMN_ID + " = " + item.id, null);
	}
	
	public List<ArticleItem> getAllArticles() {
		List<ArticleItem> items = new ArrayList<ArticleItem>();
		
		Cursor cursor = database.query(SavedArticlesHelper.TABLE_ARTICLES, allColumns, null, null, null, null, null);
		
		cursor.moveToFirst();
		while(!cursor.isAfterLast()) {
			ArticleItem item = cursorToArticle(cursor);
			items.add(item);
			cursor.moveToNext();
		}
		
		cursor.close();
		return items;
	}
	
	private ArticleItem cursorToArticle(Cursor cursor) {
		long id = cursor.getLong(0);
		String title = cursor.getString(1);
		String url = cursor.getString(2);
		
		ArticleItem item = new ArticleItem(id, title, Uri.parse(url));
		return item;
	}
}
