package eu.prismsw.lampshade.fragments;

import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebView.HitTestResult;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import eu.prismsw.lampshade.BaseActivity;
import eu.prismsw.lampshade.R;
import eu.prismsw.lampshade.listeners.OnLoadListener;
import eu.prismsw.lampshade.tasks.LoadTropesTask;
import eu.prismsw.tools.android.UIFunctions;
import eu.prismsw.tropeswrapper.TropesArticle;
import eu.prismsw.tropeswrapper.TropesArticleInfo;
import eu.prismsw.tropeswrapper.TropesArticleSettings;

/** Shows an TvTropes article in a WebView **/
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
	
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    	inflater.inflate(R.menu.article_fragment_menu, menu);
    }
	
	@TargetApi(11)
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.article_find) {
			WebView wv = (WebView) getView().findViewById(R.id.wv_content);
			wv.showFindDialog("", true);
			return true;
		}
        else if(item.getItemId() == R.id.article_show_spoilers) {
        	showAllSpoilers();
        	return true;
        }
        else {
			return super.onOptionsItemSelected(item);
		}
    }
	
	@Override
	public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
		// Hide the "Find" functionality from pre Honeycomb devices because it is not available < 11
		if(Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
			menu.findItem(R.id.article_find).setVisible(false);
		}
		
		// Disable "Toggle Spoilers" because it is causing some weird hangups
		menu.findItem(R.id.article_show_spoilers).setVisible(false);
	}
    
    private void showAllSpoilers() {
        	WebView wv = (WebView) getView().findViewById(R.id.wv_content);
        	
        	// Javascript function, find all .spoiler elements and calls showSpoiler on them
        	String showSpoilers = "function() { var spoilers = document.getElementsByClassName('spoiler'); for(i = 0; i < spoilers.length; i++) { toggleSpoiler(spoilers[i]); } }";
        	wv.loadUrl("javascript:(" + showSpoilers + ")()");
    }

    @Override
    public void onLoadFinish(Object result) {
        RelativeLayout rl = (RelativeLayout) getView().findViewById(R.id.rl_progress_wrapper);
        rl.setVisibility(RelativeLayout.GONE);

        TropesArticle article = (TropesArticle) result;
        setupArticle(article);

        TropesArticleInfo info = new TropesArticleInfo(article.title, article.url, article.subpages);
        trueUrl = article.url;
        articleInfo = info;

        loadListener.onLoadFinish(info);
    }


    private void setupArticle(TropesArticle article) {

        WebView wv = (WebView) getView().findViewById(R.id.wv_content);
        wv.getSettings().setJavaScriptEnabled(true);
        wv.getSettings().setLoadsImagesAutomatically(true);
        String html = article.content.html();
        wv.loadDataWithBaseURL("tvtropes.org", html, "text/html", "utf-8", null);

        // Fix background color for older devices because otherwise a white bar appears
        if(((BaseActivity) getActivity()).isDarkTheme()) {
            wv.setBackgroundColor(Color.BLACK);
        }

        wv.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, final android.webkit.JsResult result)
            {
                UIFunctions.showToast(message, application);
                return true;
            };
        });

        wv.setOnLongClickListener(new OnLongClickListener() {
            public boolean onLongClick(View v) {
                WebView wv = (WebView) v;
                HitTestResult hr = wv.getHitTestResult();

                // If the clicked element is a link
                if (hr.getType() == HitTestResult.SRC_ANCHOR_TYPE) {
                    // hr.getExtra() is the link's target
                    interactionListener.onLinkSelected(Uri.parse(hr.getExtra()));
                } else if (hr.getType() == HitTestResult.SRC_IMAGE_ANCHOR_TYPE) {

                    Handler linkHandler = new Handler() {
                        @Override
                        public void handleMessage(Message msg) {
                            super.handleMessage(msg);
                            String src = msg.getData().getString("src");
                            String url = msg.getData().getString("url");
                            String title = msg.getData().getString("title");

                            interactionListener.onLinkSelected(Uri.parse(url));
                        }
                    };

                    Message m = linkHandler.obtainMessage();
                    m.setTarget(linkHandler);

                    wv.requestFocusNodeHref(m);
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

    }

	public class LoadArticleTask extends LoadTropesTask {
		
		public LoadArticleTask(OnLoadListener tLoadListener) {
			super(tLoadListener);
		}
		
		public LoadArticleTask(OnLoadListener tLoadListener, TropesArticleSettings articleSettings) {
			super(tLoadListener, articleSettings);
		}
		
		@Override
		protected void onPostExecute(Object result) {
			if(result instanceof TropesArticle) {
				TropesArticle article = (TropesArticle) result;

                tLoadListener.onLoadFinish(article);
			}
			else {
				Exception e = (Exception) result;
				this.tLoadListener.onLoadError();
			}
		}
	}
	
	public void loadTropes(Uri url) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(application);
		Integer fontSize = preferences.getInt("preference_font_size", 12);
		String fontSizeStr = fontSize.toString() + "pt";
		
		TropesArticleSettings articleSettings;
		if(((BaseActivity) getActivity()).isDarkTheme()) {
			articleSettings = new TropesArticleSettings(true);
		}
		else {
			articleSettings = new TropesArticleSettings(false);
		}
		articleSettings.fontSize = fontSizeStr;
		articleSettings.toggleSpoilerOnHover = preferences.getBoolean("preference_spoiler_hover", false);
		
		
		new LoadArticleTask(this, articleSettings).execute(url);
	}
}
