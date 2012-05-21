package com.syn3rgy.lampshade.fragments;

import com.syn3rgy.lampshade.R;
import com.syn3rgy.lampshade.TropesApplication;
import com.syn3rgy.lampshade.fragments.listeners.OnArticleLoadListener;
import com.syn3rgy.lampshade.fragments.listeners.OnInteractionListener;
import com.syn3rgy.tropeswrapper.TropesArticle;
import com.syn3rgy.tropeswrapper.TropesArticleInfo;

import android.app.Activity;
import android.app.Fragment;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebView.HitTestResult;
import android.webkit.WebViewClient;

public class ArticleFragment extends Fragment implements IArticleFragment{
	public static String PASSED_URL = "PASSED_URL";
	public static String TRUE_URL = "TRUE_URL";
	
	TropesApplication application;
	OnArticleLoadListener loadListener;
	OnInteractionListener interactionListener;
	
	TropesArticleInfo articleInfo;
	Uri passedUrl;
	Uri trueUrl;
	
	public static ArticleFragment newInstance(Uri url) {
		ArticleFragment f = new ArticleFragment();
		Bundle bundle = new Bundle(2);
		bundle.putParcelable(PASSED_URL, url);
		bundle.putParcelable(TRUE_URL, url);
		f.setArguments(bundle);
		return f;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setHasOptionsMenu(true);
		
		if(savedInstanceState != null) {
			this.passedUrl = savedInstanceState.getParcelable(PASSED_URL);
			this.trueUrl = savedInstanceState.getParcelable(TRUE_URL);
		}
		else {
			this.passedUrl = getArguments().getParcelable(PASSED_URL);
			this.trueUrl = getArguments().getParcelable(TRUE_URL);
		}
		
		loadArticle(this.trueUrl);
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putParcelable(PASSED_URL, this.passedUrl);
		outState.putParcelable(TRUE_URL, this.trueUrl);
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		this.application = (TropesApplication) activity.getApplication();
		
		this.loadListener = (OnArticleLoadListener) activity;
		this.interactionListener = (OnInteractionListener) activity;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup group, Bundle bundle) {
		return inflater.inflate(R.layout.article_fragment, group, false);
	}
	
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    }
    
    /** Loads an article in a different thread */
	public class LoadArticleTask extends AsyncTask<Uri, Integer, TropesArticle> {
		public LoadArticleTask(Activity activity) {
			this.activity = activity;  
		}
		
		private Activity activity;
		
		@Override
		protected void onPreExecute() {
			loadListener.onLoadStart();
		}
		
		@Override
		protected TropesArticle doInBackground(Uri... params) {
			Uri url = params[0];
			TropesArticle article = null;
			try {
				article = new TropesArticle(url);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return article;
		}
		
		@Override
		protected void onPostExecute(TropesArticle article) {
			if(article != null) {
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
							interactionListener.onLinkSelected(Uri.parse(hr.getExtra()));
						}
						return true;
					}
				});
				
				wv.setWebViewClient(new WebViewClient() {
					@Override
					public boolean shouldOverrideUrlLoading(WebView view, String url) {
						interactionListener.onLinkClicked(Uri.parse(url));
						return true;
					}
				});
				
				trueUrl = article.url;
				articleInfo = new TropesArticleInfo(article.title, article.url, article.subpages);
				loadListener.onLoadFinish(articleInfo);
			}
			else {
				loadListener.onLoadError(null);
			}
		}
	}
	
	public void loadArticle(Uri url) {
		new LoadArticleTask(getActivity()).execute(url);
	}

	public Uri getTrueUrl() {
		return this.trueUrl;
	}

	public Uri getPassedUrl() {
		return this.passedUrl;
	}

	public TropesArticleInfo getArticleInfo() {
		return this.articleInfo;
	}

}
