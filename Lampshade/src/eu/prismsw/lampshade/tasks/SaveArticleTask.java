package eu.prismsw.lampshade.tasks;

import eu.prismsw.lampshade.ArticleItem;
import eu.prismsw.lampshade.TropesApplication;
import eu.prismsw.lampshade.listeners.OnSaveListener;
import eu.prismsw.tropeswrapper.TropesHelper;
import android.net.Uri;
import android.os.AsyncTask;

public class SaveArticleTask extends AsyncTask<Uri, Integer, ArticleItem> {
	TropesApplication application;
	OnSaveListener saveListener; 
	
	public SaveArticleTask(TropesApplication application, OnSaveListener saveListener) {
		this.application = application;
		this.saveListener = saveListener;
	}

	@Override
	protected ArticleItem doInBackground(Uri... params) {
		Uri url = params[0];
		
		if(TropesHelper.isTropesLink(url)) {
			application.articlesSource.open();
			ArticleItem item = application.articlesSource.createArticleItem(TropesHelper.titleFromUrl(url), url);
			application.articlesSource.close();
			return item;
		}
		else {
			return null;
		}
	}
	
	@Override
	protected void onPostExecute(ArticleItem item) {
		if(item != null) {
			saveListener.onSaveSuccess(item);
		}
		else {
			saveListener.onSaveError();
		}
	}
}