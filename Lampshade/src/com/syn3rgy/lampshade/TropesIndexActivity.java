package com.syn3rgy.lampshade;

import java.io.IOException;

import com.syn3rgy.tropeswrapper.TropeListItem;
import com.syn3rgy.tropeswrapper.TropesIndex;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
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
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
        case android.R.id.home:
        	openActivity(MainActivity.class);
        	return true;
        default:
        	return super.onOptionsItemSelected(item);
        }
    }
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		TropeListItem item = (TropeListItem) getListAdapter().getItem(position);
		loadPage(item.url.toString());
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
				// params[0] is the URL
				tropesIndex = new TropesIndex(params[0]);
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

				ArrayAdapter<TropeListItem> tropeAdapter = new ArrayAdapter<TropeListItem>(activity, android.R.layout.simple_list_item_activated_1, tropesIndex.tropes);
				setListAdapter(tropeAdapter);
			}
		}
	}
	
	private void setTrueUrl(Uri url) {
		this.trueUrl = url;
	}

	
    private void loadPage(String url) {
    	Intent pageIntent = new Intent(getApplicationContext(), ArticleActivity.class);
    	pageIntent.setData(Uri.parse(url));
    	startActivity(pageIntent);
    }
	
    private void openActivity(Class cls) {
    	Intent intent = new Intent(getApplicationContext(), cls);
    	startActivity(intent);
    }
}
