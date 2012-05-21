package com.syn3rgy.lampshade.fragments;

import com.syn3rgy.lampshade.R;
import com.syn3rgy.lampshade.fragments.listeners.OnArticleLoadListener;
import com.syn3rgy.lampshade.fragments.listeners.OnInteractionListener;
import com.syn3rgy.tropeswrapper.TropesArticle;
import com.syn3rgy.tropeswrapper.TropesArticleInfo;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebView.HitTestResult;
import android.webkit.WebViewClient;

public class ArticleFragment extends TropesFragment {
	
	public static ArticleFragment newInstance(Uri url) {
		ArticleFragment f = new ArticleFragment();
		Bundle bundle = new Bundle(2);
		bundle.putParcelable(PASSED_URL, url);
		bundle.putParcelable(TRUE_URL, url);
		f.setArguments(bundle);
		return f;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup group, Bundle bundle) {
		return inflater.inflate(R.layout.article_fragment, group, false);
	}
	
    /** Loads an article in a different thread */
	public class LoadArticleTask extends LoadTropesTask {
		
		public LoadArticleTask(OnArticleLoadListener tLoadListener, OnInteractionListener tInteractionListener) {
			super(tLoadListener, tInteractionListener);
		}
		
		@Override
		protected void onPostExecute(Object result) {
			if(result.getClass() == TropesArticle.class) {
				TropesArticle article = (TropesArticle) result;
				
				WebView wv = (WebView) getView().findViewById(R.id.wv_content);
				wv.getSettings().setJavaScriptEnabled(true);
				wv.getSettings().setLoadsImagesAutomatically(true);
				wv.loadData(article.content.html(), "text/html", null);
				
				wv.setOnLongClickListener(new OnLongClickListener() {
					public boolean onLongClick(View v) {
						WebView wv = (WebView) v;
						HitTestResult hr = wv.getHitTestResult();
						
						// If the clicked element is a link
						if(hr.getType() == HitTestResult.SRC_ANCHOR_TYPE) {
							// hr.getExtra() is the link's target
							tInteractionListener.onLinkSelected(Uri.parse(hr.getExtra()));
						}
						return true;
					}
				});
				
				wv.setWebViewClient(new WebViewClient() {
					@Override
					public boolean shouldOverrideUrlLoading(WebView view, String url) {
						tInteractionListener.onLinkClicked(Uri.parse(url));
						return true;
					}
				});
				
				TropesArticleInfo tArticleInfo = new TropesArticleInfo(article.title, article.url, article.subpages);
				trueUrl = article.url;
				articleInfo = tArticleInfo;
				tLoadListener.onLoadFinish(tArticleInfo);
			}
			else {
				Exception e = (Exception) result;
				this.tLoadListener.onLoadError(e);
			}
		}
	}
	
	public void loadTropes(Uri url) {
		new LoadArticleTask(this.loadListener, this.interactionListener).execute(url);
	}
}
