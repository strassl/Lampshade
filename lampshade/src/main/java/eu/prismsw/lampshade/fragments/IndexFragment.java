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
import com.actionbarsherlock.widget.ShareActionProvider;
import com.koushikdutta.async.future.Future;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;
import eu.prismsw.lampshade.BaseActivity;
import eu.prismsw.lampshade.R;
import eu.prismsw.tropeswrapper.*;

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
        super.onLoadFinish(result);

        TropesIndex index = (TropesIndex) result;
        setupIndex(index);
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

	@Override
	public void loadTropes(Uri url) {
        Future<Response<String>> articleStr = Ion.with(getActivity(), url.toString())
                .asString()
                .withResponse()
                .setCallback(new FutureCallback<Response<String>>() {
                    @Override
                    public void onCompleted(Exception e, Response<String> response) {
                        if(e != null) {
                            onLoadError();
                        }
                        else {
                            Uri redirectUrl = Uri.parse(response.getRequest().getUri().toString());
                            TropesArticle article = createIndex(response.getResult(), redirectUrl);
                            onLoadFinish(article);
                        }
                    }
                });
	}

    private TropesIndex createIndex(String html, Uri url) {
        TropesArticleResources res = new TropesArticleResources(application.getMainJS());

        TropesIndex index = new TropesIndex();
        try {
            index.loadArticle(html, url, createDefaultSettings(), res);
            return index;
        }
        catch (Exception e) {
            return null;
        }
    }
}
