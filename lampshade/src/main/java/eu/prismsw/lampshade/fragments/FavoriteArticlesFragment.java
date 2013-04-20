package eu.prismsw.lampshade.fragments;

import android.app.Activity;


public class FavoriteArticlesFragment extends SavedArticlesFragment {

    public static FavoriteArticlesFragment newInstance() {
        FavoriteArticlesFragment f = new FavoriteArticlesFragment();
        return f;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.source = application.favoriteArticlesSource;
    }
}
