package eu.prismsw.lampshade;

import eu.prismsw.lampshade.fragments.SearchFragment;
import eu.prismsw.lampshade.fragments.listeners.OnInteractionListener;
import eu.prismsw.lampshade.fragments.listeners.OnLoadListener;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;

public class SearchActivity extends Activity implements OnLoadListener, OnInteractionListener{
	
	TropesApplication application;
	SearchFragment fragment;
	
	String query;
	
	ProgressDialog loadDialog;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.article_activity);
		
		ActionBar ab = getActionBar();
		ab.setDisplayHomeAsUpEnabled(true);
		ab.setHomeButtonEnabled(true);
		
		this.application = (TropesApplication) getApplication();
		
		Bundle extras = getIntent().getExtras();
		if(extras != null) {
			String query = extras.getString(SearchFragment.QUERY_KEY);
			 
			if(query != null) {
				query += " " + "site:tvtropes.org";
				this.query = query;
				if(savedInstanceState == null) {
					this.fragment = SearchFragment.newInstance(this.query);
						
					getFragmentManager().beginTransaction().add(android.R.id.content, (Fragment) fragment).commit();
				}
			}
		}
	}

	public void onLinkSelected(Uri url) {
		// TODO Auto-generated method stub
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
