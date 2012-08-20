package eu.prismsw.lampshade.database;

import android.database.Cursor;
import android.net.Uri;
import eu.prismsw.lampshade.OfflineArticleItem;

public class OfflineSource extends ArticlesSource {

	public OfflineSource(SavedArticlesHelper helper) {
		super(helper);
	}
	
	public OfflineArticleItem getOfflineArticle(Uri url) {
		Cursor c = database.query(OfflineHelper.TABLE_ARTICLES, null, "url = ?", new String[] { url.toString() }, null, null, null);
		OfflineArticleItem article = cursorToOfflineArticle(c);
		return article;
	}
	
	private OfflineArticleItem cursorToOfflineArticle(Cursor c) {
		long id = c.getLong(0);
		String title = c.getString(1);
		Uri url = Uri.parse(c.getString(2));
		String html = c.getString(3);
		
		return new OfflineArticleItem(id, title, url, html);
	}

}
