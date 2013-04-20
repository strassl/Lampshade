package eu.prismsw.lampshade.fragments;

import android.app.Activity;
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
import eu.prismsw.lampshade.R;
import eu.prismsw.lampshade.RemoveActionMode;
import eu.prismsw.lampshade.TropesApplication;
import eu.prismsw.lampshade.database.ArticleItem;
import eu.prismsw.lampshade.database.ArticlesSource;
import eu.prismsw.lampshade.listeners.OnRemoveListener;
import eu.prismsw.lampshade.tasks.RemoveArticleTask;

import java.util.List;


public class SavedArticlesFragment extends SherlockFragment {
    ArticlesSource source;

    public RemoveActionMode removeActionMode;

    public TropesApplication application;
    public OnRemoveListener removeListener;

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
        this.source = application.savedArticlesSource;
        this.removeListener = (OnRemoveListener) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup group, Bundle bundle) {
        return inflater.inflate(R.layout.saved_articles_fragment, group, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        reloadList();
    }

    public void reloadList() {
        List<ArticleItem> savedArticles = loadArticles();
        ListView lv = getListView();

        ArrayAdapter<ArticleItem> adapter = new ArrayAdapter<ArticleItem>(getSherlockActivity(), android.R.layout.simple_list_item_1, savedArticles);
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Remove the article from the database and load it
                ListView lv = getListView();
                ArticleItem item = (ArticleItem) lv.getAdapter().getItem(position);
                new RemoveArticleTask(source, removeListener).execute(item.url);
                application.loadPage(item.url);
            }

        });

        this.removeActionMode = new RemoveActionMode(getSherlockActivity(), source);

        registerForContextMenu(lv);
        lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        lv.setOnItemLongClickListener(new OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                ArticleItem item = (ArticleItem) getListView().getAdapter().getItem(position);
                removeActionMode.startActionMode(item.url);
                return true;
            }

        });
    }

    private List<ArticleItem> loadArticles() {
        TropesApplication application = (TropesApplication) getSherlockActivity().getApplication();

        source.open();
        List<ArticleItem> savedArticles = source.getAllArticleItems();
        source.close();

        return savedArticles;
    }

    private ListView getListView() {
        ListView lv = (ListView) getSherlockActivity().findViewById(R.id.lv_saved_articles);
        return lv;
    }
}
