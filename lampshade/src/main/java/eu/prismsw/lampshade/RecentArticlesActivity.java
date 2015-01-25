package eu.prismsw.lampshade;

import eu.prismsw.lampshade.fragments.RecentArticlesFragment;

public class RecentArticlesActivity extends SavedArticlesActivity {

    @Override
    public void addFragments() {
        listFragment = RecentArticlesFragment.newInstance();

        getFragmentManager().beginTransaction().add(R.id.list_container, listFragment).commit();
    }

}
