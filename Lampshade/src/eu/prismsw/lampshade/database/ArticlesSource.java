package eu.prismsw.lampshade.database;

import java.util.ArrayList;
import java.util.List;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

/** Wrapper for common database functions */
public class ArticlesSource {
	
	protected SQLiteDatabase database;
	protected SavedArticlesHelper helper;
	protected String[] infoColumns = {SavedArticlesHelper.ARTICLES_COLUMN_ID, SavedArticlesHelper.ARTICLES_COLUMN_TITLE, SavedArticlesHelper.ARTICLES_COLUMN_URL};
	
	public ArticlesSource(SavedArticlesHelper helper) {
		this.helper = helper;
	} 
	
	public void open() throws SQLException {
		database = helper.getWritableDatabase();
	}
	
	public void close() {
		helper.close();
	}
	
	/** Saves the title and URL, returns an object with the newly created id **/
	public ArticleItem createArticleItem (String title, Uri url) {
		ContentValues values = new ContentValues();
		values.put(SavedArticlesHelper.ARTICLES_COLUMN_TITLE, title);
		values.put(SavedArticlesHelper.ARTICLES_COLUMN_URL, url.toString());
		
		long id = database.insert(SavedArticlesHelper.TABLE_ARTICLES, null, values);
		
		return new ArticleItem(id, title, url);
	}
	
	public Boolean articleExists(Uri url) {
		return helper.recordExists(database, url);
	}
	
	public void removeArticle(ArticleItem item) {
		database.delete(SavedArticlesHelper.TABLE_ARTICLES, SavedArticlesHelper.ARTICLES_COLUMN_ID + " = " + item.id, null);
	}
	
	public ArticleItem getArticleItem(Integer id) {
		Cursor c = database.query(SavedArticlesHelper.TABLE_ARTICLES, infoColumns, SavedArticlesHelper.ARTICLES_COLUMN_ID + " = ?", new String[] {id.toString()}, null, null, null);
		c.moveToFirst();
		ArticleItem item = cursorToArticleItem(c);
		c.close();
		return item;
	}
	
	public ArticleItem getArticleItem(Uri url) {
		Cursor c = database.query(SavedArticlesHelper.TABLE_ARTICLES, infoColumns, SavedArticlesHelper.ARTICLES_COLUMN_URL + " = ?", new String[] {url.toString()}, null, null, null);
		c.moveToFirst();
		ArticleItem item = cursorToArticleItem(c);
		c.close();
		return item;
	}
	
	/** Returns all articles in the database **/
	public List<ArticleItem> getAllArticleItems() {
		List<ArticleItem> items = new ArrayList<ArticleItem>();
		
		Cursor cursor = database.query(SavedArticlesHelper.TABLE_ARTICLES, infoColumns, null, null, null, null, null);
		
		cursor.moveToFirst();
		while(!cursor.isAfterLast()) {
			ArticleItem item = cursorToArticleItem(cursor);
			items.add(item);
			cursor.moveToNext();
		}
		
		cursor.close();
		return items;
	}
	
	private ArticleItem cursorToArticleItem(Cursor cursor) {
		long id = cursor.getLong(0);
		String title = cursor.getString(1);
		String url = cursor.getString(2);
		
		ArticleItem item = new ArticleItem(id, title, Uri.parse(url));
		return item;
	}
}
