package eu.prismsw.lampshade.fragments;

import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import eu.prismsw.googlewrapper.GoogleSearch;
import eu.prismsw.googlewrapper.GoogleSearchResult;
import eu.prismsw.lampshade.R;
import eu.prismsw.lampshade.TropesApplication;
import eu.prismsw.lampshade.fragments.listeners.OnLoadListener;
import eu.prismsw.lampshade.fragments.listeners.OnInteractionListener;

public class SearchFragment extends Fragment {
	public static final String QUERY_KEY = "QUERY";
	
	public String query;
	
	public TropesApplication application;
	public OnLoadListener loadListener;
	public OnInteractionListener interactionListener;
	
	public static SearchFragment newInstance(String query) {
		SearchFragment f = new SearchFragment();
		Bundle bundle = new Bundle(1);
		bundle.putString(QUERY_KEY, query);
		f.setArguments(bundle);
		return f;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setHasOptionsMenu(true);
		
		if(savedInstanceState != null) {
			this.query = savedInstanceState.getString(QUERY_KEY);
		}
		else {
			this.query = getArguments().getString(QUERY_KEY);
		}
		
		loadSearch(this.query);
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString(QUERY_KEY, this.query);
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		this.application = (TropesApplication) activity.getApplication();
		
		this.loadListener = (OnLoadListener) activity;
		this.interactionListener = (OnInteractionListener) activity;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup group, Bundle bundle) {
		return inflater.inflate(R.layout.search_fragment, group, false);
	}
    
	
	public void loadSearch(String query) {
		new LoadSearchTask(this.loadListener, this.interactionListener).execute(query);
	}
	
	public class LoadSearchTask extends AsyncTask<String, Integer, Object> {
		public OnLoadListener tLoadListener;
		public OnInteractionListener tInteractionListener;
		
		public LoadSearchTask(OnLoadListener tLoadListener, OnInteractionListener tInteractionListener) {
			this.tLoadListener = tLoadListener;
			this.tInteractionListener = tInteractionListener;
		}
		
		@Override
		protected void onPreExecute() {
			this.tLoadListener.onLoadStart();
		}
		
		@Override
		protected Object doInBackground(String... params) {
			try {
				String query = params[0];
				GoogleSearch search = new GoogleSearch(query);
				return search;
			} catch (Exception e) {
				return e;
			}
		}
		
		@Override
		protected void onPostExecute(Object result) {
			
			if(result.getClass() == GoogleSearch.class) {
				GoogleSearch search = (GoogleSearch) result;
				
				ArrayAdapter<GoogleSearchResult> searchAdapter = new ArrayAdapter<GoogleSearchResult>(getActivity(), android.R.layout.simple_list_item_activated_1, fixSearchTitles(search.results));
				ListView lv = (ListView) getView();
				lv.setAdapter(searchAdapter);
				
				lv.setOnItemClickListener(new OnItemClickListener() {
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						GoogleSearchResult item = (GoogleSearchResult) parent.getItemAtPosition(position);
						tInteractionListener.onLinkClicked(item.url);
					}
				});
		
				lv.setOnItemLongClickListener(new OnItemLongClickListener() {
					public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
						GoogleSearchResult item = (GoogleSearchResult) parent.getItemAtPosition(position);
						tInteractionListener.onLinkSelected(item.url);
						return true;
					}
				});
				
				tLoadListener.onLoadFinish(null);
			}
			else {
				Exception e = (Exception) result;
				tLoadListener.onLoadError(e);
			}
		}
		
		private List<GoogleSearchResult> fixSearchTitles(List<GoogleSearchResult> results) {
			for(GoogleSearchResult result : results) {
				result.title = result.title.split(" - ")[0];
			}
			
			return results;
		}
	}

}
