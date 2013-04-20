package eu.prismsw.lampshade;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.text.ClipboardManager;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.ShareActionProvider;
import eu.prismsw.lampshade.database.ArticleItem;
import eu.prismsw.lampshade.fragments.AlertDialogFragment;
import eu.prismsw.lampshade.fragments.ArticleFragment;
import eu.prismsw.lampshade.fragments.IndexFragment;
import eu.prismsw.lampshade.fragments.TropesFragment;
import eu.prismsw.lampshade.listeners.OnInteractionListener;
import eu.prismsw.lampshade.listeners.OnLoadListener;
import eu.prismsw.lampshade.listeners.OnRemoveListener;
import eu.prismsw.lampshade.listeners.OnSaveListener;
import eu.prismsw.lampshade.tasks.RemoveArticleTask;
import eu.prismsw.lampshade.tasks.SaveArticleTask;
import eu.prismsw.tools.ListFunctions;
import eu.prismsw.tools.android.UIFunctions;
import eu.prismsw.tropeswrapper.TropesArticleInfo;
import eu.prismsw.tropeswrapper.TropesHelper;

import java.util.List;

/** Shows a single TvTropes article */
public class ArticleActivity extends BaseActivity implements OnLoadListener, OnInteractionListener, OnSaveListener, OnRemoveListener {
	static final int DIALOG_SUBPAGES_ID = 1;
	static final int DIALOG_LOAD_FAILED = 2;
	
	TropesFragment fragment;
	ProgressDialog loadDialog;

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
		this.saveActionMode = new SaveActionMode(this, application.savedArticlesSource);
		this.removeActionMode = new RemoveActionMode(this, application.savedArticlesSource);
		
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
    	if(trueUrl != null) {
    		// Switch between Remove/Save
    		application.savedArticlesSource.open();
	    	if(application.savedArticlesSource.articleExists(trueUrl)) {
	    		menu.findItem(R.id.save_article).setTitle(R.string.article_remove);
	    	}
	    	else {
	    		menu.findItem(R.id.save_article).setTitle(R.string.article_save);
	    	}
	    	application.savedArticlesSource.close();
	    	
	    	application.favoriteArticlesSource.open();
	    	if(application.favoriteArticlesSource.articleExists(trueUrl)) {
	    		menu.findItem(R.id.favorite_article).setTitle(R.string.article_unfavorite);
	    	}
	    	else {
	    		menu.findItem(R.id.favorite_article).setTitle(R.string.article_favorite);
	    	}
	    	application.favoriteArticlesSource.close();
    	}
    	
    	return super.onPrepareOptionsMenu(menu);
    }
    
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            startActivity(new Intent(this, MainActivity.class));
			return true;
		} else if (item.getItemId() == R.id.refresh_article) {
			application.loadPage(passedUrl);
			return true;
		} else if (item.getItemId() == R.id.save_article) {
			application.savedArticlesSource.open();
			if(application.savedArticlesSource.articleExists(trueUrl)) {
				removeArticle(trueUrl);
			}
			else {
				saveArticle(trueUrl);
			}
			application.savedArticlesSource.close();
			return true;
		} else if (item.getItemId() == R.id.favorite_article) {
			application.favoriteArticlesSource.open();
				if(application.favoriteArticlesSource.articleExists(trueUrl)) {
					unfavoriteArticle(trueUrl);
				}
				else {
					favoriteArticle(trueUrl);
				}
			application.favoriteArticlesSource.close();
			return true;
		} else if (item.getItemId() == R.id.info_article) {
			showDialogFragment(createInfoDialog(articleInfo.title, trueUrl, passedUrl));
			return true;
		} else if (item.getItemId() == R.id.browser_article) {
			application.loadWebsite(this.trueUrl);
			return true;
		} else if (item.getItemId() == R.id.clipboard_article) {
			copyUrlToClipboard(this.trueUrl);
			return true;
		} else if (item.getItemId() == R.id.subpages_article) {
			showDialog(DIALOG_SUBPAGES_ID);
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
    	case DIALOG_SUBPAGES_ID:
    		List<String> subpageStringList = ListFunctions.listToStringList(articleInfo.subpages);
    		String[] subpageStringArray = subpageStringList.toArray(new String[subpageStringList.size()]);
    		
    		builder.setTitle(R.string.article_subpages);
    		builder.setItems(subpageStringArray,  new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					application.loadWebsite(articleInfo.subpages.get(which).url);
				}
			});
    		builder.setCancelable(true);
    		builder.setPositiveButton(R.string.dialog_dismiss, new OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
    		dialog = builder.create();
    		break;
    	case DIALOG_LOAD_FAILED:
    		builder.setTitle(R.string.dialog_load_failed_title);
    		builder.setMessage(R.string.dialog_load_failed_message);
    		
    		builder.setPositiveButton(R.string.dialog_reload, new OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					fragment.loadTropes(passedUrl);
				}
    		});
    		
    		builder.setNeutralButton("Copy url", new OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					copyUrlToClipboard(passedUrl);
					finish();
				}
    		});
    		
    		builder.setNegativeButton(R.string.dialog_close, new OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					finish();
				}
    		});
    		
    		dialog = builder.create();
    		break;
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
    
    private void copyUrlToClipboard(Uri url) {
    	// Kinda bugs me, but backward compatibility demands sacrifices
    	ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
    	clipboard.setText(url.toString());
		UIFunctions.showToast(getResources().getString(R.string.article_clipboard_copied) + url.toString(), this);
    }
    
    private void favoriteArticle(Uri url) {
    	new SaveArticleTask(application.favoriteArticlesSource, this).execute(url);
    }
    
    private void unfavoriteArticle(Uri url) {
    	new RemoveArticleTask(application.favoriteArticlesSource, this).execute(url);
    }
    
    private void saveArticle(Uri url) {
    	new SaveArticleTask(application.savedArticlesSource, this).execute(url);
    }
    
    private void removeArticle(Uri url) {
    	new RemoveArticleTask(application.savedArticlesSource, this).execute(url);
    }
	
	public void onLinkSelected(Uri url) {
		application.savedArticlesSource.open();
		if(application.savedArticlesSource.articleExists(url)) {
			this.removeActionMode.startActionMode(url);
		}
		else {
			this.saveActionMode.startActionMode(url);
		}
		application.savedArticlesSource.close();
	}

	public void onLoadError(Exception e) {
		closeProgressDialog();
		e.printStackTrace();
		showDialog(DIALOG_LOAD_FAILED);
	}

	public void onLinkClicked(Uri url) {
		if(TropesHelper.isTropesLink(url)) {
			application.loadPage(url);
        }
		else {
			application.loadWebsite(url);
		}
	}

	public void onLoadStart() {
		this.loadDialog = ProgressDialog.show(this, "", getResources().getString(R.string.dialog_article_loading), true);
	}

	public void onLoadFinish(Object result) {
		TropesArticleInfo info = (TropesArticleInfo) result;
		this.articleInfo = info;
		this.trueUrl = info.url;
		
		
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		Boolean historyEnabled = preferences.getBoolean("preference_history_enable", true);
		
		if(historyEnabled) {
			// Add the page to the list of recent articles
			application.recentArticlesSource.open();
			
			// This is a horribly ugly and inefficient way of doing this
			List<ArticleItem> recentArticles = application.recentArticlesSource.getAllArticleItems();
			if(recentArticles.size() >= TropesApplication.maxRecentArticles) {
				// Remove the oldest (=first) item
				application.recentArticlesSource.removeArticle(recentArticles.get(0));
			}
			
			application.recentArticlesSource.createArticleItem(TropesHelper.titleFromUrl(this.trueUrl), this.trueUrl);
			
			application.recentArticlesSource.close();
		}
		
		getSupportActionBar().setTitle(info.title);
		setShareIntent();
		
		closeProgressDialog();
	}
	
	private void closeProgressDialog() {
		if(this.loadDialog != null && this.loadDialog.isShowing()) {
			this.loadDialog.dismiss();
		}
	}

	@Override
	public void onRemoveSuccess(ArticleItem item) {
		invalidateOptionsMenu();
		UIFunctions.showToast(getResources().getString(R.string.article_removed) + item.title, this);
	}

	@Override
	public void onRemoveError() {
		UIFunctions.showToast(getResources().getString(R.string.article_remove_failed),  this);
	}

	@Override
	public void onSaveSuccess(ArticleItem item) {
		invalidateOptionsMenu();
		UIFunctions.showToast(getResources().getString(R.string.article_saved) + item.title, this);
	}

	@Override
	public void onSaveError() {
		UIFunctions.showToast(getResources().getString(R.string.article_save_failed), this);
	}
}
