package eu.prismsw.lampshade;

import eu.prismsw.lampshade.fragments.FavoriteArticlesFragment;

public class FavoriteArticlesActivity extends SavedArticlesActivity {

    @Override
    public void addFragments() {
        listFragment = FavoriteArticlesFragment.newInstance();

        getSupportFragmentManager().beginTransaction().add(android.R.id.content, listFragment).commit();
    }

}
