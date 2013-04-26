package eu.prismsw.lampshade.fragments;

import android.os.Bundle;
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
}
