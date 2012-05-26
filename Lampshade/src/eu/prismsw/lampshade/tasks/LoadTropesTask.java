package eu.prismsw.lampshade.tasks;

import android.net.Uri;
import android.os.AsyncTask;


import eu.prismsw.lampshade.listeners.OnInteractionListener;
import eu.prismsw.lampshade.listeners.OnLoadListener;
import eu.prismsw.tropeswrapper.TropesArticle;
import eu.prismsw.tropeswrapper.TropesArticleInfo;

/** Loads an article in a different thread */
public class LoadTropesTask extends AsyncTask<Uri, Integer, Object> {
	public OnLoadListener tLoadListener;
	public OnInteractionListener tInteractionListener;
	public String theme;
	
	public LoadTropesTask(OnLoadListener tLoadListener, OnInteractionListener tInteractionListener, String theme) {
		this.tLoadListener = tLoadListener;
		this.tInteractionListener = tInteractionListener;
		this.theme = theme;
	}
	
	public LoadTropesTask(OnLoadListener tLoadListener, OnInteractionListener tInteractionListener) {
		this(tLoadListener, tInteractionListener, "HoloLight");
	}
	
	@Override
	protected void onPreExecute() {
		this.tLoadListener.onLoadStart();
	}
	
	@Override
	protected Object doInBackground(Uri... params) {
		try {
			Uri url = params[0];
			TropesArticle article = null;
			if(theme.equalsIgnoreCase("HoloDark")) {
				article = new TropesArticle(url,TropesArticle.PURE_WHITE, TropesArticle.ICS_BRIGHT_BLUE, TropesArticle.PURE_WHITE, TropesArticle.PURE_BLACK);
			}
			else {
				article = new TropesArticle(url);
			}
			return article;
		} catch (Exception e) {
			return e;
		}
	}
	
	@Override
	protected void onPostExecute(Object result) {
		if(result.getClass() == TropesArticle.class) {
			TropesArticle article = (TropesArticle) result;
			TropesArticleInfo articleInfo = new TropesArticleInfo(article.title, article.url, article.subpages);
			this.tLoadListener.onLoadFinish(articleInfo);
		}
		else {
			Exception e = (Exception) result;
			this.tLoadListener.onLoadError(e);
		}
	}
}
