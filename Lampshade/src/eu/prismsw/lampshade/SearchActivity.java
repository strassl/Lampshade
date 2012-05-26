package eu.prismsw.lampshade;

import eu.prismsw.lampshade.fragments.SearchFragment;
import eu.prismsw.lampshade.listeners.OnInteractionListener;
import eu.prismsw.lampshade.listeners.OnLoadListener;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;

public class SearchActivity extends Activity implements OnLoadListener, OnInteractionListener{
	
	TropesApplication application;
	SearchFragment fragment;
	
	String passedQuery;
	String fullQuery;
	
	ProgressDialog loadDialog;
	LinkActionMode linkActionMode;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		this.application = (TropesApplication) getApplication();
		application.switchTheme(this);
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.article_activity);
		
		ActionBar ab = getActionBar();
		ab.setDisplayHomeAsUpEnabled(true);
		ab.setHomeButtonEnabled(true);
		
		this.linkActionMode = new LinkActionMode(this);
		
		Bundle extras = getIntent().getExtras();
		if(extras != null) {
			String query = extras.getString(SearchFragment.QUERY_KEY);
			 
			if(query != null) {
				this.passedQuery = query;
				ab.setTitle("Search: " + passedQuery);
				query += " " + "site:tvtropes.org";
				this.fullQuery = query;
				if(savedInstanceState == null) {
					this.fragment = SearchFragment.newInstance(this.fullQuery);
						
					getFragmentManager().beginTransaction().add(android.R.id.content, (Fragment) fragment).commit();
				}
			}
		}
		
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case android.R.id.home:
			application.openActivity(MainActivity.class);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}	
	}
	
	public void onLinkSelected(Uri url) {
		this.linkActionMode.startActionMode(url);
	}

	public void onLinkClicked(Uri url) {
		application.loadPage(url);
	}

	public void onLoadStart() {
		this.loadDialog = ProgressDialog.show(this, "", "Loading search results...", true);
	}

	public void onLoadFinish(Object result) {
		closeProgressDialog();
	}

	public void onLoadError(Exception e) {
		
	}
	
	private void closeProgressDialog() {
		if(this.loadDialog != null && this.loadDialog.isShowing()) {
			this.loadDialog.dismiss();
		}
	}
}
