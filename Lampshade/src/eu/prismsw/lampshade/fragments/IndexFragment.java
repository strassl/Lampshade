package eu.prismsw.lampshade.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import eu.prismsw.lampshade.R;
import eu.prismsw.lampshade.listeners.OnInteractionListener;
import eu.prismsw.lampshade.listeners.OnLoadListener;
import eu.prismsw.lampshade.tasks.LoadTropesTask;
import eu.prismsw.tropeswrapper.TropesArticleInfo;
import eu.prismsw.tropeswrapper.TropesHelper;
import eu.prismsw.tropeswrapper.TropesIndex;
import eu.prismsw.tropeswrapper.TropesIndexSelector;
import eu.prismsw.tropeswrapper.TropesLink;

public class IndexFragment extends TropesFragment{
	
	public static IndexFragment newInstance(Uri url) {
		IndexFragment f = new IndexFragment();
		Bundle bundle = new Bundle(2);
		bundle.putParcelable(PASSED_URL, url);
		bundle.putParcelable(TRUE_URL, url);
		f.setArguments(bundle);
		return f;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup group, Bundle bundle) {
		return inflater.inflate(R.layout.index_fragment, group, false);
	}
	
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    	inflater.inflate(R.menu.index_menu, menu);
    }
	
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.index_as_article) {
			application.loadArticle(this.trueUrl);
			return true;
		} else {
			return super.onOptionsItemSelected(item);
		}
    }
    
	
    /** Loads an index in a different thread */
	public class LoadIndexTask extends LoadTropesTask {
		
		public LoadIndexTask(OnLoadListener tLoadListener, OnInteractionListener tInteractionListener) {
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

				ArrayAdapter<TropesLink> tropeAdapter = new ArrayAdapter<TropesLink>(getActivity(), android.R.layout.simple_list_item_1, tropesIndex.tropes);
				ListView lv = (ListView) getActivity().findViewById(R.id.lv_tropes);
				lv.setAdapter(tropeAdapter);
				
				lv.setOnItemClickListener(new OnItemClickListener() {
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						TropesLink item = (TropesLink) parent.getItemAtPosition(position);
						tInteractionListener.onLinkClicked(item.url);
					}
				});
		
				lv.setOnItemLongClickListener(new OnItemLongClickListener() {
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

	@Override
	public void loadTropes(Uri url) {
		new LoadIndexTask(this.loadListener, this.interactionListener).execute(url);
	}
}
