package eu.prismsw.lampshade.database;

import android.database.Cursor;
import android.net.Uri;

import eu.prismsw.tropeswrapper.*;

/** Represents a saved article from the database */
public class ArticleItem {
	public Long id;
	public String title;
	public Uri url;
	
	public ArticleItem(Long id, String title, Uri url) {
		this.id = id;
		this.title = title;
		this.url = url;
	}
	
	public ArticleItem(Long id, Uri url) {
		this.id = id;
		this.title = TropesHelper.titleFromUrl(url);
		this.url = url;
	}

    public ArticleItem(Cursor c) {
        this.id = c.getLong(0);
        this.title = c.getString(1);
        this.url = Uri.parse(c.getString(2));
    }
	
	
	// Returns the title for the ListView
	@Override
	public String toString() {
		return this.title;
	}
}
