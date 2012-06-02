package eu.prismsw.lampshade.fragments;

import java.util.List;

import android.app.Activity;
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

import com.actionbarsherlock.app.SherlockFragment;

import eu.prismsw.googlewrapper.GoogleSearch;
import eu.prismsw.googlewrapper.GoogleSearchResult;
import eu.prismsw.lampshade.R;
import eu.prismsw.lampshade.TropesApplication;
import eu.prismsw.lampshade.listeners.OnInteractionListener;
import eu.prismsw.lampshade.listeners.OnLoadListener;

/** Shows the results of a search in a ListView **/
public class SearchFragment extends SherlockFragment {
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
				GoogleSearch search = new GoogleSearch(query, 2);
				return search;
			} catch (Exception e) {
				return e;
			}
		}
		
		@Override
		protected void onPostExecute(Object result) {
			
			if(result.getClass() == GoogleSearch.class) {
				GoogleSearch search = (GoogleSearch) result;
				
				// Fix the titles (necessary for TvTropes) and show them in the ListView
				ArrayAdapter<GoogleSearchResult> searchAdapter = new ArrayAdapter<GoogleSearchResult>(getActivity(), android.R.layout.simple_list_item_1, enhanceSearchTitles(search.results));
				ListView lv = (ListView) getActivity().findViewById(R.id.lv_search);
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
		
		private List<GoogleSearchResult> enhanceSearchTitles(List<GoogleSearchResult> results) {
			for(GoogleSearchResult result : results) {
				String articleTitle = result.title;
				String shortTitle = articleTitle.split(" - ")[0];
				
				List<String> pathSegments = result.url.getPathSegments();
				
				if(pathSegments.get(1).equalsIgnoreCase("pmwiki.php")) {
					String prefix = pathSegments.get(pathSegments.size() - 2) + "/";
					
					if(shortTitle.contains(prefix)) {
						articleTitle = shortTitle;
					}
					else {
						articleTitle =  prefix + shortTitle;
					}
				}
				
				result.title = articleTitle;
			}
			
			return results;
		}
	}

}
