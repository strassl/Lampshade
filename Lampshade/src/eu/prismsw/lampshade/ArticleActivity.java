package eu.prismsw.lampshade;

import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.ShareActionProvider;

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

/** Shows a single TvTropes article */
public class ArticleActivity extends BaseActivity implements OnLoadListener, OnInteractionListener, OnSaveListener, OnRemoveListener {
	static final int DIALOG_INFO_ID = 0;
	static final int DIALOG_SUBPAGES_ID = 1;
	static final int DIALOG_LOAD_FAILED = 2;
	
	// The active fragment
	TropesFragment fragment;
	// The dialog that is shown while the fragment is loading
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
		this.saveActionMode = new SaveActionMode(this);
		this.removeActionMode = new RemoveActionMode(this);
		
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
				if(!loadAsArticle && application.isIndex(TropesHelper.titleFromUrl(data))) {
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
    	if(application.getThemeName().equalsIgnoreCase("HoloDark")) {
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
	    	if(isArticleSaved(trueUrl)) {
	    		menu.findItem(R.id.save_article).setTitle("Remove article");
	    	}
	    	else {
	    		menu.findItem(R.id.save_article).setTitle("Save article");
	    	}
    	}
    	
    	return super.onPrepareOptionsMenu(menu);
    }
    
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
			application.openActivity(MainActivity.class);
			return true;
		} else if (item.getItemId() == R.id.refresh_article) {
			application.loadPage(passedUrl);
			return true;
		} else if (item.getItemId() == R.id.save_article) {
			if(isArticleSaved(trueUrl)) {
				removeArticle(trueUrl);
			}
			else {
				saveArticle(trueUrl);
			}
			return true;
		} else if (item.getItemId() == R.id.info_article) {
			showDialog(DIALOG_INFO_ID);
			return true;
		} else if (item.getItemId() == R.id.browser_article) {
			application.loadWebsite(this.trueUrl);
			return true;
		} else if (item.getItemId() == R.id.subpages_article) {
			showDialog(DIALOG_SUBPAGES_ID);
			return true;
		} else {
			return super.onOptionsItemSelected(item);
		}
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
    	case DIALOG_INFO_ID:
    		String info = "";
    		info += "Title: " + articleInfo.title + "<br /><br />";
    		info += "Url: " + trueUrl.toString() + "<br /><br />";
    		info += "Passed Url: " + passedUrl.toString();
    		    		
    		builder.setTitle("Info");
    		builder.setMessage(Html.fromHtml(info));
    		builder.setCancelable(true);
    		builder.setPositiveButton("Thanks!", new OnClickListener() {
    			public void onClick(DialogInterface dialog, int which) {
    				dialog.dismiss();
    			}
    		});
    		       
    		dialog = builder.create();
    		break;
    	case DIALOG_SUBPAGES_ID:
    		List<String> subpageStringList = ListFunctions.listToStringList(articleInfo.subpages);
    		String[] subpageStringArray = subpageStringList.toArray(new String[subpageStringList.size()]);
    		
    		builder.setTitle("Subpages");
    		builder.setItems(subpageStringArray,  new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					application.loadWebsite(articleInfo.subpages.get(which).url);
				}
			});
    		builder.setCancelable(true);
    		builder.setPositiveButton("Dismiss", new OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
    		dialog = builder.create();
    		break;
    	case DIALOG_LOAD_FAILED:
    		builder.setTitle("Error");
    		builder.setMessage("Could not load the page. Do you want to reload it?");
    		
    		builder.setPositiveButton("Reload", new OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					fragment.loadTropes(passedUrl);
				}
    		});
    		
    		builder.setNegativeButton("Close", new OnClickListener() {
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
    
	// This still needs more work / can be done in a nicer way
	private Boolean isArticleSaved(Uri url) {
		application.articlesSource.open();
		List<ArticleItem> articles = application.articlesSource.getAllArticles();
		
		for(ArticleItem article : articles) {
			if(article.url.equals(url)) {
				application.articlesSource.close();
				return true;
			}
		}
		
		application.articlesSource.close();
		return false;
	}
    
    private void saveArticle(Uri url) {
    	new SaveArticleTask(application, this).execute(url);
    }
    
    private void removeArticle(Uri url) {
    	new RemoveArticleTask(application, this).execute(url);
    }
	
	public void onLinkSelected(Uri url) {
		if(isArticleSaved(url)) {
			this.removeActionMode.startActionMode(url);
		}
		else {
			this.saveActionMode.startActionMode(url);
		}
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
		this.loadDialog = ProgressDialog.show(this, "", "Loading article...", true);
	}

	public void onLoadFinish(Object result) {
		TropesArticleInfo info = (TropesArticleInfo) result;
		closeProgressDialog();
		this.articleInfo = info;
		getSupportActionBar().setTitle(info.title);
		this.trueUrl = info.url;
		setShareIntent();
	}
	
	private void closeProgressDialog() {
		if(this.loadDialog != null && this.loadDialog.isShowing()) {
			this.loadDialog.dismiss();
		}
	}

	@Override
	public void onRemoveSuccess(ArticleItem item) {
		invalidateOptionsMenu();
		UIFunctions.showToast("Removed " + item.title, this);
	}

	@Override
	public void onRemoveError() {
		UIFunctions.showToast("Could not remove this link",  this);
	}

	@Override
	public void onSaveSuccess(ArticleItem item) {
		invalidateOptionsMenu();
		UIFunctions.showToast("Added " + item.title, this);
	}

	@Override
	public void onSaveError() {
		UIFunctions.showToast("Could not add this link (not a tvtropes link?)", this);
	}
}
