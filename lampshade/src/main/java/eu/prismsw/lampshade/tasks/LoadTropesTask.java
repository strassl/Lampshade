package eu.prismsw.lampshade.tasks;

import android.net.Uri;
import android.os.AsyncTask;


import eu.prismsw.lampshade.listeners.OnInteractionListener;
import eu.prismsw.lampshade.listeners.OnLoadListener;
import eu.prismsw.tropeswrapper.TropesArticle;
import eu.prismsw.tropeswrapper.TropesArticleInfo;
import eu.prismsw.tropeswrapper.TropesArticleSettings;

/** Loads an article in a different thread */
public class LoadTropesTask extends AsyncTask<Uri, Integer, Object> {
	public OnLoadListener tLoadListener;

	public TropesArticleSettings articleSettings;
	
	public LoadTropesTask(OnLoadListener tLoadListener, TropesArticleSettings articleSettings) {
		this.tLoadListener = tLoadListener;
		this.articleSettings = articleSettings;
	}
	
	public LoadTropesTask(OnLoadListener tLoadListener) {
		this(tLoadListener, new TropesArticleSettings(false));
	}
	
	@Override
	protected void onPreExecute() {
		this.tLoadListener.onLoadStart();
	}
	
	@Override
	protected Object doInBackground(Uri... params) {
		try {
			Uri url = params[0];
			TropesArticle article = new TropesArticle(url, articleSettings);
			return article;
		} catch (Exception e) {
			return e;
		}
	}
	
	@Override
	protected void onPostExecute(Object result) {
		// If the result is a TropesArticle (no error occurred)
		if(result.getClass() == TropesArticle.class) {
			TropesArticle article = (TropesArticle) result;
			this.tLoadListener.onLoadFinish(article);
		}
		else {
			Exception e = (Exception) result;
			this.tLoadListener.onLoadError(e);
		}
	}
}
