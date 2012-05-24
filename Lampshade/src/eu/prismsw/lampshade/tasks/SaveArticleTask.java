package eu.prismsw.lampshade.tasks;

import eu.prismsw.lampshade.ArticleItem;
import eu.prismsw.lampshade.TropesApplication;
import eu.prismsw.tools.android.UIFunctions;
import eu.prismsw.tropeswrapper.TropesHelper;
import android.net.Uri;
import android.os.AsyncTask;

public class SaveArticleTask extends AsyncTask<Uri, Integer, ArticleItem> {
	TropesApplication application;
	
	public SaveArticleTask(TropesApplication application) {
		this.application = application;
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
			UIFunctions.showToast("Added " + item.title, application);
		}
		else {
			UIFunctions.showToast("Could not add this link (not a tvtropes link?)", application);
		}
	}
}