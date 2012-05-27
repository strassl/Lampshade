package eu.prismsw.lampshade;

import java.util.List;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ShareActionProvider;

import eu.prismsw.lampshade.R;

import eu.prismsw.lampshade.fragments.ArticleFragment;
import eu.prismsw.lampshade.fragments.IndexFragment;
import eu.prismsw.lampshade.fragments.TropesFragment;
import eu.prismsw.lampshade.listeners.OnInteractionListener;
import eu.prismsw.lampshade.listeners.OnLoadListener;
import eu.prismsw.lampshade.tasks.SaveArticleTask;
import eu.prismsw.tools.ListFunctions;
import eu.prismsw.tropeswrapper.TropesArticleInfo;
import eu.prismsw.tropeswrapper.TropesHelper;

/** Shows a single TvTropes article */
public class ArticleActivity extends BaseActivity implements OnLoadListener, OnInteractionListener{
	static final int DIALOG_INFO_ID = 0;
	static final int DIALOG_SUBPAGES_ID = 1;
	static final int DIALOG_LOAD_FAILED = 2;
	
	TropesFragment fragment;
	ProgressDialog loadDialog;
	
	TropesArticleInfo articleInfo;
	// The url that was passed to the activity
	Uri passedUrl;
	// Where we actually ended up
	Uri trueUrl;
	LinkActionMode linkActionMode;
	ShareActionProvider shareProvider;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.article_activity);
		
		ActionBar ab = getActionBar();
		ab.setDisplayHomeAsUpEnabled(true);
		ab.setHomeButtonEnabled(true);
		
		this.linkActionMode = new LinkActionMode(this);
		
		Uri data = getIntent().getData();
		if(data != null) {
			this.passedUrl = data;
			
			Boolean loadAsArticle = false;
			Bundle extras = getIntent().getExtras();
			if(extras != null) {
				loadAsArticle = getIntent().getExtras().getBoolean(TropesApplication.loadAsArticle);
			}
			
			if(savedInstanceState == null) {
				// There might be a better way to redirect the index pages
				if(application.isIndex(TropesHelper.titleFromUrl(data)) && !loadAsArticle) {
					this.fragment = IndexFragment.newInstance(this.passedUrl);
					
					getFragmentManager().beginTransaction().add(android.R.id.content, (Fragment) fragment).commit();
				}
				else {
					this.fragment = ArticleFragment.newInstance(this.passedUrl);
					
					getFragmentManager().beginTransaction().add(android.R.id.content, (Fragment) fragment).commit();
				}
			}
		}
	}
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater inflater = getMenuInflater();
    	if(application.getThemeName().equalsIgnoreCase("HoloDark")) {
	    	inflater.inflate(R.menu.article_menu_dark, menu);
    	}
    	else {
	    	inflater.inflate(R.menu.article_menu_light, menu);
    	}
    	shareProvider = (ShareActionProvider) menu.findItem(R.id.share_article).getActionProvider();
    	return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
        case android.R.id.home:
        	application.openActivity(MainActivity.class);
        	return true;
        case R.id.refresh_article:
        	application.loadPage(passedUrl);
        	return true;   
        case R.id.save_article:
        	saveArticle(trueUrl);
        	return true;
        case R.id.info_article:
        	showDialog(DIALOG_INFO_ID);
        	return true;
        case R.id.browser_article:
        	application.loadWebsite(this.trueUrl);
        	return true;
        case R.id.subpages_article:
        	showDialog(DIALOG_SUBPAGES_ID);
        	return true;
        default:
        	return super.onOptionsItemSelected(item);
        }
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
    	Intent intent = new Intent(Intent.ACTION_SEND);
    	intent.setType("text/plain");
    	intent.putExtra(Intent.EXTRA_TEXT, trueUrl.toString());
    	shareProvider.setShareIntent(intent);
    }
    
    private void saveArticle(Uri url) {
    	new SaveArticleTask(application).execute(url);
    }
	
	public void onLinkSelected(Uri url) {
		this.linkActionMode.startActionMode(url);
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
		getActionBar().setTitle(info.title);
		this.trueUrl = info.url;
		setShareIntent();
	}
	
	private void closeProgressDialog() {
		if(this.loadDialog != null && this.loadDialog.isShowing()) {
			this.loadDialog.dismiss();
		}
	}
}
