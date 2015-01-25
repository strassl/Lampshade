package eu.prismsw.lampshade;

import eu.prismsw.lampshade.fragments.FavoriteArticlesFragment;

public class FavoriteArticlesActivity extends SavedArticlesActivity {

    @Override
    public void addFragments() {
        listFragment = FavoriteArticlesFragment.newInstance();

        getFragmentManager().beginTransaction().add(R.id.list_container, listFragment).commit();
    }
}
