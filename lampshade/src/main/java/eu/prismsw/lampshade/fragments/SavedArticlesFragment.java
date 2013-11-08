package eu.prismsw.lampshade.fragments;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import com.actionbarsherlock.app.SherlockFragment;
import eu.prismsw.lampshade.R;
import eu.prismsw.lampshade.RemoveActionMode;
import eu.prismsw.lampshade.TropesApplication;
import eu.prismsw.lampshade.database.ArticleItem;
import eu.prismsw.lampshade.database.ProviderHelper;
import eu.prismsw.lampshade.database.SavedArticlesHelper;
import eu.prismsw.lampshade.listeners.OnInteractionListener;
import eu.prismsw.lampshade.listeners.OnRemoveListener;
import eu.prismsw.lampshade.providers.ArticleProvider;


public class SavedArticlesFragment extends SherlockFragment {
    public RemoveActionMode removeActionMode;

    public TropesApplication application;
    public OnRemoveListener removeListener;
    public OnInteractionListener interactionListener;

    public Uri contentUri = ArticleProvider.SAVED_URI;

    public static SavedArticlesFragment newInstance() {
        SavedArticlesFragment f = new SavedArticlesFragment();
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        this.application = (TropesApplication) activity.getApplication();
        this.removeListener = (OnRemoveListener) activity;
        this.interactionListener = (OnInteractionListener) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup group, Bundle bundle) {
        return inflater.inflate(R.layout.saved_articles_fragment, group, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();

        reloadList();
    }

    public void reloadList() {
        ListView lv = getListView();

        String[] uiBindFrom = { SavedArticlesHelper.ARTICLES_COLUMN_TITLE };
        int[] uiBindTo = { android.R.id.text1 };
        Cursor articles = ProviderHelper.getArticles(getActivity().getContentResolver(), contentUri);
        CursorAdapter adapter = new SimpleCursorAdapter(getActivity(), android.R.layout.simple_list_item_1, articles, uiBindFrom, uiBindTo, 0);
        lv.setAdapter(adapter);
        setUpListView(lv);
    }

    public void setUpListView(ListView lv) {
        lv.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Remove the article from the database and load it
                ListView lv = getListView();
                ArticleItem item = new ArticleItem((Cursor) lv.getAdapter().getItem(position));
                int affected = ProviderHelper.deleteArticle(getActivity().getContentResolver(), contentUri, item.url);
                removeListener.onRemoveFinish(affected);
                interactionListener.onLinkClicked(item.url);
            }
        });

        registerForContextMenu(lv);
        lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        lv.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView <?> parent, View view, int position, long id) {
                ArticleItem item = new ArticleItem((Cursor) getListView().getAdapter().getItem(position));
                new RemoveActionMode(getSherlockActivity(), contentUri).startActionMode(item.url);
                return true;
            }
        });
    }

    public ListView getListView() {
        ListView lv = (ListView) getSherlockActivity().findViewById(R.id.lv_saved_articles);
        return lv;
    }
}
