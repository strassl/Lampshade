package eu.prismsw.lampshade.tasks;

import java.util.List;

import eu.prismsw.lampshade.ArticleItem;
import eu.prismsw.lampshade.database.ArticlesSource;
import eu.prismsw.lampshade.listeners.OnRemoveListener;
import eu.prismsw.tropeswrapper.TropesHelper;
import android.net.Uri;
import android.os.AsyncTask;

/** Removes the article with the matching url from the database **/
public class RemoveArticleTask extends AsyncTask<Uri, Integer, ArticleItem> {
	ArticlesSource articlesSource;
	OnRemoveListener removeListener;
	
	public RemoveArticleTask(ArticlesSource articlesSource, OnRemoveListener removeListener) {
		this.articlesSource = articlesSource;
		this.removeListener = removeListener;
	}

	// Returns null if it was not in the list or is not a tvtropes link
	@Override
	protected ArticleItem doInBackground(Uri... params) {
		Uri url = params[0];
		
		if(TropesHelper.isTropesLink(url)) {
			articlesSource.open();
			
			ArticleItem matchingArticle = articlesSource.getArticleItem(url);
			// If we found a matching article, we remove it
			if(matchingArticle != null) {
				articlesSource.removeArticle(matchingArticle);
			}
			articlesSource.close();
			
			return matchingArticle;
		}
		else {
			return null;
		}
	}
	
	@Override
	protected void onPostExecute(ArticleItem item) {
		if(item != null) {
			if(this.removeListener != null) {
				this.removeListener.onRemoveSuccess(item);
			}
		}
		else {
			if(this.removeListener != null) {
				this.removeListener.onRemoveError();
			}
		}
	}
}