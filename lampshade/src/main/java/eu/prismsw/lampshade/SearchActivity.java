package eu.prismsw.lampshade;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import eu.prismsw.lampshade.fragments.SearchFragment;
import eu.prismsw.lampshade.listeners.OnInteractionListener;
import eu.prismsw.lampshade.listeners.OnLoadListener;
import eu.prismsw.lampshade.listeners.OnSaveListener;
import eu.prismsw.lampshade.providers.ArticleProvider;

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
		
		ActionBar ab = getActionBar();
		ab.setDisplayHomeAsUpEnabled(true);
		ab.setHomeButtonEnabled(true);
		
		this.saveActionMode = new SaveActionMode(this, ArticleProvider.SAVED_URI);
		
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
						
					getFragmentManager().beginTransaction().add(android.R.id.content, fragment).commit();
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
		loadPage(url);
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

    @Override
    public void onSaveFinish(Uri url) {
    }

	private void closeProgressDialog() {
		if(this.loadDialog != null && this.loadDialog.isShowing()) {
			this.loadDialog.dismiss();
		}
	}
}
