package eu.prismsw.lampshade.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import eu.prismsw.lampshade.database.ArticleItem;
import eu.prismsw.lampshade.providers.ArticleProvider;

public class RecentArticlesFragment extends SavedArticlesFragment {
    public static RecentArticlesFragment newInstance() {
        RecentArticlesFragment f = new RecentArticlesFragment();
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        contentUri = ArticleProvider.RECENT_URI;
    }

    @Override
    public void setUpListView(ListView lv) {
        super.setUpListView(lv);

        lv.setOnItemClickListener(new ListView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListView lv = getListView();
                ArticleItem item = new ArticleItem((Cursor) lv.getAdapter().getItem(position));
                interactionListener.onLinkClicked(item.url);
            }
        });
    }
}
