package eu.prismsw.lampshade.tasks;

import eu.prismsw.lampshade.ArticleItem;
import eu.prismsw.lampshade.ArticlesSource;
import eu.prismsw.lampshade.listeners.OnSaveListener;
import eu.prismsw.tropeswrapper.TropesHelper;
import android.net.Uri;
import android.os.AsyncTask;

public class SaveArticleTask extends AsyncTask<Uri, Integer, ArticleItem> {
	ArticlesSource articlesSource;
	OnSaveListener saveListener; 
	
	public SaveArticleTask(ArticlesSource articlesSource, OnSaveListener saveListener) {
		this.articlesSource = articlesSource;
		this.saveListener = saveListener;
	}

	@Override
	protected ArticleItem doInBackground(Uri... params) {
		Uri url = params[0];
		
		if(TropesHelper.isTropesLink(url)) {
			articlesSource.open();
			ArticleItem item = articlesSource.createArticleItem(TropesHelper.titleFromUrl(url), url);
			articlesSource.close();
			return item;
		}
		else {
			return null;
		}
	}
	
	@Override
	protected void onPostExecute(ArticleItem item) {
		if(item != null) {
			if(saveListener != null) {
				saveListener.onSaveSuccess(item);
			}
		}
		else {
			if(saveListener != null) {
				saveListener.onSaveError();
			}
		}
	}
}