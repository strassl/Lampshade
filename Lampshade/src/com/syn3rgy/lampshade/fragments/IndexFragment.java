package com.syn3rgy.lampshade.fragments;

import com.syn3rgy.lampshade.R;
import com.syn3rgy.lampshade.TropesApplication;
import com.syn3rgy.lampshade.fragments.listeners.OnArticleLoadListener;
import com.syn3rgy.lampshade.fragments.listeners.OnInteractionListener;
import com.syn3rgy.tropeswrapper.TropesArticleInfo;
import com.syn3rgy.tropeswrapper.TropesHelper;
import com.syn3rgy.tropeswrapper.TropesIndex;
import com.syn3rgy.tropeswrapper.TropesIndexSelector;
import com.syn3rgy.tropeswrapper.TropesLink;

import android.app.Activity;
import android.app.ListFragment;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class IndexFragment extends ListFragment implements IArticleFragment{
	public static String EXTRA_URL = "URL";
	
	TropesApplication application;
	OnArticleLoadListener loadListener;
	OnInteractionListener interactionListener;
	
	TropesArticleInfo articleInfo;
	Uri passedUrl;
	Uri trueUrl;
	
	public static IndexFragment newInstance(Uri url) {
		IndexFragment f = new IndexFragment();
		Bundle bundle = new Bundle(1);
		bundle.putParcelable(EXTRA_URL, url);
		f.setArguments(bundle);
		return f;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setHasOptionsMenu(true);
		
		Uri url = getArguments().getParcelable(EXTRA_URL);
		this.passedUrl = url;
		
		loadArticle(this.passedUrl);
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.application = (TropesApplication) activity.getApplication();
		
		this.loadListener = (OnArticleLoadListener) activity;
		this.interactionListener = (OnInteractionListener) activity;
	}
	
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    	inflater.inflate(R.menu.index_menu, menu);
    }
	
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
        case R.id.index_as_article:
        	application.loadArticle(this.trueUrl);
        	return true;
        default:
        	return super.onOptionsItemSelected(item);
        }
    }
    
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		TropesLink item = (TropesLink) getListAdapter().getItem(position);
		interactionListener.onLinkClicked(item.url);
	}
	
    /** Loads an index in a different thread */
	public class LoadIndexTask extends LoadTropesTask {
		
		public LoadIndexTask(OnArticleLoadListener tLoadListener, OnInteractionListener tInteractionListener) {
			super(tLoadListener, tInteractionListener);
		}

		@Override
		protected Object doInBackground(Uri... params) {
			try {
				Uri url = params[0];
				TropesIndexSelector selector = TropesHelper.findMatchingSelector(application.indexPages, url);
				TropesIndex tropesIndex = new TropesIndex(url, selector);
				return tropesIndex;
			} catch (Exception e) {
				return e;
			}
		}
		
		@Override
		protected void onPostExecute(Object result) {
			
			if(result.getClass() == TropesIndex.class) {
				TropesIndex tropesIndex = (TropesIndex) result;

				ArrayAdapter<TropesLink> tropeAdapter = new ArrayAdapter<TropesLink>(getActivity(), android.R.layout.simple_list_item_activated_1, tropesIndex.tropes);
				setListAdapter(tropeAdapter);
		
				getListView().setOnItemLongClickListener(new OnItemLongClickListener() {
					public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
						TropesLink link = (TropesLink) parent.getItemAtPosition(position);
						tInteractionListener.onLinkSelected(link.url);
						return true;
					}
				});
				
				TropesArticleInfo tArticleInfo = new TropesArticleInfo(tropesIndex.title, tropesIndex.url, tropesIndex.subpages);
				articleInfo = tArticleInfo;
				
				tLoadListener.onLoadFinish(articleInfo);
			}
			else {
				Exception e = (Exception) result;
				tLoadListener.onLoadError(e);
			}
		}
	}

	public void loadArticle(Uri url) {
		new LoadIndexTask(this.loadListener, this.interactionListener).execute(url);
	}

	public Uri getTrueUrl() {
		return this.trueUrl;
	}

	public Uri getPassedUrl() {
		return this.passedUrl;
	}

	public TropesArticleInfo getArticleInfo() {
		return this.articleInfo;
	}
}
