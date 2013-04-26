package eu.prismsw.lampshade.fragments;

import android.os.Bundle;
import eu.prismsw.lampshade.providers.ArticleProvider;

public class FavoriteArticlesFragment extends SavedArticlesFragment {
    public static FavoriteArticlesFragment newInstance() {
        FavoriteArticlesFragment f = new FavoriteArticlesFragment();
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        contentUri = ArticleProvider.FAV_URI;
    }
}
