package eu.prismsw.lampshade.fragments;

import android.app.Activity;

public class RecentArticlesFragment extends SavedArticlesFragment {

    public static RecentArticlesFragment newInstance() {
        RecentArticlesFragment f = new RecentArticlesFragment();
        return f;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.source = application.recentArticlesSource;
    }
}
