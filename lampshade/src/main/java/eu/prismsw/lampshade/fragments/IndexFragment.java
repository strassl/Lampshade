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
import android.widget.RelativeLayout;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.ShareActionProvider;
import eu.prismsw.lampshade.BaseActivity;
import eu.prismsw.lampshade.R;
import eu.prismsw.lampshade.listeners.OnLoadListener;
import eu.prismsw.lampshade.tasks.LoadTropesTask;
import eu.prismsw.tropeswrapper.TropesArticleInfo;
import eu.prismsw.tropeswrapper.TropesIndex;
import eu.prismsw.tropeswrapper.TropesLink;

public class IndexFragment extends TropesFragment {
	
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
        inflater.inflate(R.menu.tropes_fragment_menu, menu);
        inflater.inflate(R.menu.index_fragment_menu, menu);

        MenuItem shareItem = menu.findItem(R.id.share_article);
        shareProvider = (ShareActionProvider) shareItem.getActionProvider();
    }
	
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(!loadingIsFinished()) {
            return true;
        }
        else if (item.getItemId() == R.id.index_as_article) {
            ((BaseActivity) getActivity()).loadArticle(trueUrl);
			return true;
		} else {
			return super.onOptionsItemSelected(item);
		}
    }

    @Override
    public void onLoadFinish(Object result) {
        RelativeLayout rl = (RelativeLayout) getView().findViewById(R.id.rl_progress_wrapper);
        rl.setVisibility(RelativeLayout.GONE);

        TropesIndex index = (TropesIndex) result;

        setupIndex(index);

        TropesArticleInfo info = new TropesArticleInfo(index.title, index.url, index.subpages);
        trueUrl = index.url;
        setShareIntent(trueUrl);

        loadListener.onLoadFinish(info);
    }

    private void setupIndex(TropesIndex index) {
        ArrayAdapter<TropesLink> tropeAdapter = new ArrayAdapter<TropesLink>(getActivity(), android.R.layout.simple_list_item_1, index.tropes);
        ListView lv = (ListView) getActivity().findViewById(R.id.lv_tropes);
        lv.setAdapter(tropeAdapter);

        lv.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TropesLink item = (TropesLink) parent.getItemAtPosition(position);
                interactionListener.onLinkClicked(item.url);
            }
        });

        lv.setOnItemLongClickListener(new OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                TropesLink link = (TropesLink) parent.getItemAtPosition(position);
                interactionListener.onLinkSelected(link.url);
                return true;
            }
        });

        articleInfo = new TropesArticleInfo(index.title, index.url, index.subpages);
    }


    /** Loads an index in a different thread */
	public class LoadIndexTask extends LoadTropesTask {
		
		public LoadIndexTask(OnLoadListener tLoadListener) {
			super(tLoadListener);
		}

		@Override
		protected Object doInBackground(Uri... params) {
			try {
				Uri url = params[0];
				TropesIndex tropesIndex = new TropesIndex(url);
				return tropesIndex;
			} catch (Exception e) {
				return e;
			}
		}
		
		@Override
		protected void onPostExecute(Object result) {
			
			if(result instanceof TropesIndex) {
                TropesIndex index = (TropesIndex) result;
                tLoadListener.onLoadFinish(index);
			}
			else {
				Exception e = (Exception) result;
				tLoadListener.onLoadError();
			}
		}
	}

	@Override
	public void loadTropes(Uri url) {
		new LoadIndexTask(this).execute(url);
	}
}
