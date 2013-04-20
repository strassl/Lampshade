package eu.prismsw.lampshade;

import eu.prismsw.lampshade.fragments.RecentArticlesFragment;

public class RecentArticlesActivity extends SavedArticlesActivity {

    @Override
    public void addFragments() {
        listFragment = RecentArticlesFragment.newInstance();

        getSupportFragmentManager().beginTransaction().add(android.R.id.content, listFragment).commit();
    }

}
