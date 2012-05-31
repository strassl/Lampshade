package eu.prismsw.lampshade.tasks;

import java.util.List;

import eu.prismsw.lampshade.ArticleItem;
import eu.prismsw.lampshade.TropesApplication;
import eu.prismsw.lampshade.listeners.OnRemoveListener;
import eu.prismsw.tropeswrapper.TropesHelper;
import android.net.Uri;
import android.os.AsyncTask;

public class RemoveArticleTask extends AsyncTask<Uri, Integer, ArticleItem> {
	TropesApplication application;
	OnRemoveListener removeListener;
	
	public RemoveArticleTask(TropesApplication application, OnRemoveListener removeListener) {
		this.application = application;
		this.removeListener = removeListener;
	}

	// Returns null if it was not in the list or is not a tvtropes link
	@Override
	protected ArticleItem doInBackground(Uri... params) {
		Uri url = params[0];
		
		if(TropesHelper.isTropesLink(url)) {
			application.articlesSource.open();
			List<ArticleItem> articles = application.articlesSource.getAllArticles();
			
			ArticleItem matchingArticle = findArticleItemByUrl(articles, url);
			if(matchingArticle != null) {
				application.articlesSource.removeArticle(matchingArticle);
			}
			application.articlesSource.close();
			
			return matchingArticle;
		}
		else {
			return null;
		}
	}
	
	private ArticleItem findArticleItemByUrl(List<ArticleItem> articles, Uri url) {
		for(ArticleItem article : articles) {
			if(article.url.equals(url)) {
				return article;
			}
		}
		
		return null;
	}
	
	@Override
	protected void onPostExecute(ArticleItem item) {
		if(item != null) {
			this.removeListener.onRemoveSuccess(item);
		}
		else {
			this.removeListener.onRemoveError();
		}
	}
}