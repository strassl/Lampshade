package eu.prismsw.lampshade.database;

import android.net.Uri;

public class OfflineArticleItem extends ArticleItem {
	public String html;

	public OfflineArticleItem(long id, String title, Uri url, String html) {
		super(id, title, url);
		this.html = html;
	}
}
