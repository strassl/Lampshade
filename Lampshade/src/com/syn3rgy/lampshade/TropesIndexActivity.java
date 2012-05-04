package com.syn3rgy.lampshade;

import java.io.IOException;

import com.syn3rgy.tropeswrapper.TropesLink;
import com.syn3rgy.tropeswrapper.TropesHelper;
import com.syn3rgy.tropeswrapper.TropesIndex;
import com.syn3rgy.tropeswrapper.TropesIndexSelector;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class TropesIndexActivity extends ListActivity {
	TropesApplication application;
	Uri passedUrl;
	Uri trueUrl;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		ActionBar ab = getActionBar();
		ab.setHomeButtonEnabled(true);
		ab.setDisplayHomeAsUpEnabled(true);
				
		application = (TropesApplication) getApplication();
		
		Uri data = getIntent().getData();
		if(data != null) {
			this.passedUrl = data;
			new loadTropesIndexTask(this).execute(this.passedUrl);
		}
	}
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.index_menu, menu);
        return true;
    }
	
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
        case android.R.id.home:
        	application.openActivity(MainActivity.class);
        	return true;
        case R.id.index_as_article:
        	application.loadArticle(this.trueUrl.toString());
        	return true;
        default:
        	return super.onOptionsItemSelected(item);
        }
    }
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		TropesLink item = (TropesLink) getListAdapter().getItem(position);
		application.loadPage(item.url.toString());
	}
	
	
    /** Loads an article in a different thread */
	public class loadTropesIndexTask extends AsyncTask<Uri, Integer, TropesIndex> {
		public loadTropesIndexTask(Activity activity) {
			this.activity = activity;  
		}
		
		private ProgressDialog pDialog = null;
		private Activity activity;
		
		@Override
		protected void onPreExecute() {
			this.pDialog = ProgressDialog.show(this.activity, "", "Loading tropes...", true);
			pDialog.show();
		}
		
		@Override
		protected TropesIndex doInBackground(Uri... params) {
			TropesIndex tropesIndex = null;
			try {
				Uri url = params[0];
				TropesIndexSelector selector = TropesHelper.findMatchingSelector(application.indexPages, url);
				tropesIndex = new TropesIndex(url, selector);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return tropesIndex;
		}
		
		@Override
		protected void onPostExecute(TropesIndex tropesIndex) {
			if(pDialog.isShowing()) {
				pDialog.dismiss();
			}
			if(tropesIndex != null) {
				activity.getActionBar().setTitle(tropesIndex.title);
				setTrueUrl(Uri.parse(tropesIndex.url));

				ArrayAdapter<TropesLink> tropeAdapter = new ArrayAdapter<TropesLink>(activity, android.R.layout.simple_list_item_activated_1, tropesIndex.tropes);
				setListAdapter(tropeAdapter);
			}
		}
	}
	
	private void setTrueUrl(Uri url) {
		this.trueUrl = url;
	}
}
