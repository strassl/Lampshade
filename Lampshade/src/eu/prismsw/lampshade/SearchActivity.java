package eu.prismsw.lampshade;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.MenuItem;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import eu.prismsw.lampshade.fragments.SearchFragment;
import eu.prismsw.lampshade.listeners.OnInteractionListener;
import eu.prismsw.lampshade.listeners.OnLoadListener;

public class SearchActivity extends BaseActivity implements OnLoadListener, OnInteractionListener{
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
		
		this.saveActionMode = new SaveActionMode(this);
		
		Bundle extras = getIntent().getExtras();
		if(extras != null) {
			String query = extras.getString(SearchFragment.QUERY_KEY);
			 
			if(query != null) {
				this.passedQuery = query;
				ab.setTitle(getResources().getString(R.string.search_title) + passedQuery);
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
			application.openActivity(MainActivity.class);
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

	public void onLoadError(Exception e) {
		
	}
	
	private void closeProgressDialog() {
		if(this.loadDialog != null && this.loadDialog.isShowing()) {
			this.loadDialog.dismiss();
		}
	}
}
