package eu.prismsw.lampshade;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.ShareActionProvider;
import eu.prismsw.lampshade.database.ProviderHelper;
import eu.prismsw.lampshade.fragments.AlertDialogFragment;
import eu.prismsw.lampshade.fragments.ArticleFragment;
import eu.prismsw.lampshade.fragments.IndexFragment;
import eu.prismsw.lampshade.fragments.TropesFragment;
import eu.prismsw.lampshade.listeners.OnInteractionListener;
import eu.prismsw.lampshade.listeners.OnLoadListener;
import eu.prismsw.lampshade.listeners.OnRemoveListener;
import eu.prismsw.lampshade.listeners.OnSaveListener;
import eu.prismsw.lampshade.providers.ArticleProvider;
import eu.prismsw.tropeswrapper.TropesArticleInfo;
import eu.prismsw.tropeswrapper.TropesHelper;

/** Shows a single TvTropes article */
public class ArticleActivity extends BaseActivity implements OnLoadListener, OnInteractionListener, OnSaveListener, OnRemoveListener {
	static final int DIALOG_LOAD_FAILED = 2;
	
	TropesFragment fragment;

	// Information about the article, needs less memory than the full article
	TropesArticleInfo articleInfo;
	// The url that was passed to the activity
	Uri passedUrl;
	// Where we actually ended up
	Uri trueUrl;
	
	SaveActionMode saveActionMode;
	RemoveActionMode removeActionMode;
	ShareActionProvider shareProvider;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.article_activity);
		
		// Prepare the ActionBar
		ActionBar ab = getSupportActionBar();
		ab.setDisplayHomeAsUpEnabled(true);
		ab.setHomeButtonEnabled(true);
		
		// The ActionMode objects need only be created once and can then be reused
		// In fact they should only be created once because it prevents conflicts between multiple ActionModes
		this.saveActionMode = new SaveActionMode(this, ArticleProvider.SAVED_URI);
		this.removeActionMode = new RemoveActionMode(this, ArticleProvider.SAVED_URI);
		
		// Get the url we are supposed to load
		Uri data = getIntent().getData();
		if(data != null) {
			this.passedUrl = data;
			
			// Check if the page is supposed to be loaded as an article
			// If this is set to true, we don't even check if it could be an index
			Boolean loadAsArticle = false;
			Bundle extras = getIntent().getExtras();
			if(extras != null) {
				loadAsArticle = getIntent().getExtras().getBoolean(TropesApplication.loadAsArticle);
			}
			
			if(savedInstanceState == null) {
				// If loadAsArticle is false and it is an index page, we create an IndexFragment
				// Otherwise we simply create an ArticleFragment
				if(!loadAsArticle && TropesHelper.isIndex(TropesHelper.titleFromUrl(data))) {
					this.fragment = IndexFragment.newInstance(this.passedUrl);
					
					getSupportFragmentManager().beginTransaction().add(android.R.id.content, (SherlockFragment) fragment).commit();
				}
				else {
					this.fragment = ArticleFragment.newInstance(this.passedUrl);
					
					getSupportFragmentManager().beginTransaction().add(android.R.id.content, (SherlockFragment) fragment).commit();
                }
			}
		}
	}
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater inflater = getSupportMenuInflater();
    	// Depending on the theme, we have to change the color of the menu icons (light/dark)
    	if(application.isDarkTheme()) {
	    	inflater.inflate(R.menu.article_menu_dark, menu);
    	}
    	else {
	    	inflater.inflate(R.menu.article_menu_light, menu);
    	}
    	
    	MenuItem shareItem = menu.findItem(R.id.share_article);
    	shareProvider = (ShareActionProvider) shareItem.getActionProvider();
        return true;
    }
    
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
    	return super.onPrepareOptionsMenu(menu);
    }
    
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            startActivity(new Intent(this, MainActivity.class));
			return true;
		} else if (item.getItemId() == R.id.refresh_article) {
			loadPage(passedUrl);
			return true;
		} else if (item.getItemId() == R.id.info_article) {
			showDialogFragment(createInfoDialog(articleInfo.title, trueUrl, passedUrl));
			return true;
		} else if (item.getItemId() == R.id.browser_article) {
			loadWebsite(this.trueUrl);
			return true;
		} else {
			return super.onOptionsItemSelected(item);
		}
    }
    
    private DialogFragment createInfoDialog(String title, Uri trueUrl, Uri passedUrl) {
    		String info = "";
    		info += "Title: " + articleInfo.title + "<br /><br />";
    		info += "Url: " + trueUrl.toString() + "<br /><br />";
    		info += "Passed Url: " + passedUrl.toString();
    		
			AlertDialogFragment f = AlertDialogFragment.newInstance("Info", info);
			return f;
    }
    
    
    @Override
    public Dialog onCreateDialog(int id) {
    	return onCreateDialog(id, null);
    }
    
    @Override
    public Dialog onCreateDialog(int id, Bundle args) {
    	Dialog dialog;
   		AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	switch(id) {
    	default:
    		dialog = null;
    	}
    	return dialog;
    }
    
    /** Sets the share intent, should only be called after the true url is known **/
    private void setShareIntent() {
    	if(shareProvider != null) {
	    	Intent intent = new Intent(Intent.ACTION_SEND);
	    	intent.setType("text/plain");
	    	intent.putExtra(Intent.EXTRA_TEXT, trueUrl.toString());
	    	shareProvider.setShareIntent(intent);
    	}
    }


	public void onLinkSelected(Uri url) {
		if(ProviderHelper.articleExists(getContentResolver(), ArticleProvider.SAVED_URI, url)) {
			this.removeActionMode.startActionMode(url);
		}
		else {
			this.saveActionMode.startActionMode(url);
		}
	}

	public void onLoadError() {
        finish();
	}

	public void onLinkClicked(Uri url) {
        loadPage(url);
	}

	public void onLoadStart() {
	}

	public void onLoadFinish(Object result) {
		TropesArticleInfo info = (TropesArticleInfo) result;
		this.articleInfo = info;
		this.trueUrl = info.url;
		
		
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		Boolean historyEnabled = preferences.getBoolean("preference_history_enable", true);
		
		if(historyEnabled) {
			// Add the page to the list of recent articles

            Cursor c = ProviderHelper.getArticles(getContentResolver(), ArticleProvider.RECENT_URI);

            // Prevent the list from growing infinitely
			if(c.getCount() > TropesApplication.maxRecentArticles) {
                c.moveToFirst();
                long id = c.getLong(0);
                c.close();
                ProviderHelper.deleteArticle(getContentResolver(), ArticleProvider.RECENT_URI, String.valueOf(id));
			}

            ProviderHelper.saveArticle(getContentResolver(), ArticleProvider.RECENT_URI, trueUrl);
		}
		
		getSupportActionBar().setTitle(info.title);
		setShareIntent();
	}

	@Override
	public void onRemoveFinish(int affected) {
		invalidateOptionsMenu();
	}

	@Override
	public void onSaveFinish(Uri url) {
		invalidateOptionsMenu();
	}
}
