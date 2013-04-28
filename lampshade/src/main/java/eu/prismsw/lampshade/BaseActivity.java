package eu.prismsw.lampshade;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import eu.prismsw.tropeswrapper.TropesHelper;

/** Contains some functionality (such as theme switching) that is universal for all activities. All other activities are supposed to be subclass of this class. **/
public class BaseActivity extends SherlockFragmentActivity {
	TropesApplication application;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
        this.application = (TropesApplication) getApplication(); 
        this.switchTheme();
		
		super.onCreate(savedInstanceState);
	}
	
	public void switchTheme() {
		String theme = application.getThemeName();
		
		if(theme.equalsIgnoreCase("HoloDark")) {
			this.setTheme(com.actionbarsherlock.R.style.Theme_Sherlock);
		}
	}
	
	public void showDialogFragment(DialogFragment fragment) {
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
	    Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
	    if (prev != null) {
	        ft.remove(prev);
	    }
	    ft.addToBackStack(null);

	    fragment.show(ft, "dialog");
	}

    public boolean isTablet() {
        boolean xlarge = ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_XLARGE);
        boolean large = ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE);
        return (xlarge || large);
    }

    public void loadPage(Uri url) {
        if(TropesHelper.isTropesLink(url)) {
            String page = TropesHelper.titleFromUrl(url);

            if(TropesHelper.isIndex(page)) {
                loadIndex(url);
            }
            else {
                loadArticle(url);
            }
        }
        else {
            loadWebsite(url);
        }
    }

    /** Opens a page as an article, and only as an article **/
    public void loadArticle(Uri url) {
        Intent articleIntent = new Intent(getApplicationContext(), ArticleActivity.class);
        articleIntent.putExtra(TropesApplication.loadAsArticle, true);
        articleIntent.setData(url);
        articleIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(articleIntent);
    }

    public void loadIndex(Uri url) {
        Intent indexIntent = new Intent(getApplicationContext(), ArticleActivity.class);
        indexIntent.setData(url);
        indexIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(indexIntent);
    }

    public void loadWebsite(Uri url) {
        Intent websiteIntent = new Intent(Intent.ACTION_VIEW);
        websiteIntent.setData(url);
        websiteIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(websiteIntent);
    }

}
