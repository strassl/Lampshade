package eu.prismsw.lampshade;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.MenuItem;
import eu.prismsw.lampshade.database.ProviderHelper;
import eu.prismsw.lampshade.fragments.ArticleFragment;
import eu.prismsw.lampshade.fragments.IndexFragment;
import eu.prismsw.lampshade.fragments.SavedArticlesFragment;
import eu.prismsw.lampshade.listeners.OnInteractionListener;
import eu.prismsw.lampshade.listeners.OnLoadListener;
import eu.prismsw.lampshade.listeners.OnRemoveListener;
import eu.prismsw.lampshade.listeners.OnSaveListener;
import eu.prismsw.lampshade.providers.ArticleProvider;
import eu.prismsw.tropeswrapper.TropesArticleInfo;
import eu.prismsw.tropeswrapper.TropesHelper;

public class SavedArticlesActivity extends BaseActivity implements OnLoadListener, OnSaveListener, OnRemoveListener, OnInteractionListener {
    SavedArticlesFragment listFragment;

    public SaveActionMode saveActionMode;
    public RemoveActionMode removeActionMode;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        if(isTablet()) {
            setContentView(R.layout.dual_pane);
        }
        else {
            setContentView(R.layout.saved_articles_activity);
        }

		ActionBar ab = getSupportActionBar();
		ab.setHomeButtonEnabled(true);
		ab.setDisplayHomeAsUpEnabled(true);

        this.saveActionMode = new SaveActionMode(this, ArticleProvider.SAVED_URI);
        this.removeActionMode = new RemoveActionMode(this, ArticleProvider.SAVED_URI);

        if(savedInstanceState == null) {
            addFragments();
        }
    }

    // The other activities have to override this to change the fragment type
    public void addFragments() {
        listFragment = SavedArticlesFragment.newInstance();
        getSupportFragmentManager().beginTransaction().add(R.id.list_container, listFragment).commit();
    }
	
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                startActivity(new Intent(this, MainActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onLinkSelected(Uri url) {
        if(ProviderHelper.articleExists(getContentResolver(), ArticleProvider.SAVED_URI, url)) {
            this.removeActionMode.startActionMode(url);
        }
        else {
            this.saveActionMode.startActionMode(url);
        }
    }

    @Override
    public void onLinkClicked(Uri url) {
        loadPage(url);
    }

    @Override
    public void loadPage(Uri url) {
        if(TropesHelper.isTropesLink(url)) {
            if(isTablet()) {
                SherlockFragment f;
                if(TropesHelper.isIndex(TropesHelper.titleFromUrl(url))) {
                    f = IndexFragment.newInstance(url);
                }
                else {
                    f = ArticleFragment.newInstance(url);
                }
                FragmentTransaction t = getSupportFragmentManager().beginTransaction();
                t.replace(R.id.article_container, f);
                t.addToBackStack(null);
                t.commit();
            }
            else {
                super.loadPage(url);
            }
        }
        else {
            loadWebsite(url);
        }
    }

    @Override
    public void onLoadStart() {
    }

    @Override
    public void onLoadFinish(Object result) {
        TropesArticleInfo info = (TropesArticleInfo) result;
        getActionBar().setTitle(info.title);
        listFragment.reloadList();
    }

    @Override
    public void onLoadError() {
    }

    @Override
    public void onRemoveFinish(int affected) {
        invalidateOptionsMenu();
        listFragment.reloadList();
    }

    @Override
    public void onSaveFinish(Uri url) {
        invalidateOptionsMenu();
        listFragment.reloadList();
    }
}
