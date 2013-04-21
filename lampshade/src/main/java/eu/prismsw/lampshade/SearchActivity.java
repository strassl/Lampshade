package eu.prismsw.lampshade;

import android.content.Intent;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.MenuItem;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import eu.prismsw.lampshade.database.ArticleItem;
import eu.prismsw.lampshade.fragments.SearchFragment;
import eu.prismsw.lampshade.listeners.OnInteractionListener;
import eu.prismsw.lampshade.listeners.OnLoadListener;
import eu.prismsw.lampshade.listeners.OnSaveListener;
import eu.prismsw.tools.android.UIFunctions;

public class SearchActivity extends BaseActivity implements OnLoadListener, OnInteractionListener, OnSaveListener {
	SearchFragment fragment;
	
	String passedQuery;
	String fullQuery;
	
	ProgressDialog loadDialog;
	SaveActionMode saveActionMode;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.article_activity);
		
		ActionBar ab = getSupportActionBar();
		ab.setDisplayHomeAsUpEnabled(true);
		ab.setHomeButtonEnabled(true);
		
		this.saveActionMode = new SaveActionMode(this, application.savedArticlesSource);
		
		Bundle extras = getIntent().getExtras();
		if(extras != null) {
			String query = extras.getString(SearchFragment.QUERY_KEY);
			 
			if(query != null) {
				this.passedQuery = query;
				ab.setTitle(getResources().getString(R.string.title_search) + passedQuery);
				query += " " + "site:tvtropes.org";
				this.fullQuery = query;
				if(savedInstanceState == null) {
					this.fragment = SearchFragment.newInstance(this.fullQuery);
						
					getSupportFragmentManager().beginTransaction().add(android.R.id.content, (SherlockFragment) fragment).commit();
				}
			}
		}
		
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case android.R.id.home:
            startActivity(new Intent(this, MainActivity.class));
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}	
	}
	
	public void onLinkSelected(Uri url) {
		this.saveActionMode.startActionMode(url);
	}

	public void onLinkClicked(Uri url) {
		application.loadPage(url);
	}

	public void onLoadStart() {
		this.loadDialog = ProgressDialog.show(this, "", getResources().getString(R.string.dialog_search_loading), true);
	}

	public void onLoadFinish(Object result) {
		closeProgressDialog();
	}

	public void onLoadError() {
		closeProgressDialog();
	}

    public void onSaveSuccess(ArticleItem item) {
        UIFunctions.showToast(getResources().getString(R.string.article_saved) + item.title, this);
    }

    public void onSaveError() {
        UIFunctions.showToast(getResources().getString(R.string.article_save_failed), this);
    }
	
	private void closeProgressDialog() {
		if(this.loadDialog != null && this.loadDialog.isShowing()) {
			this.loadDialog.dismiss();
		}
	}
}
