package eu.prismsw.lampshade;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.MenuItem;
import eu.prismsw.lampshade.database.ArticleItem;
import eu.prismsw.lampshade.fragments.ArticleFragment;
import eu.prismsw.lampshade.fragments.SavedArticlesFragment;
import eu.prismsw.lampshade.listeners.OnInteractionListener;
import eu.prismsw.lampshade.listeners.OnLoadListener;
import eu.prismsw.lampshade.listeners.OnRemoveListener;
import eu.prismsw.lampshade.listeners.OnSaveListener;
import eu.prismsw.tools.android.UIFunctions;

public class SavedArticlesActivity extends BaseActivity implements OnLoadListener, OnSaveListener, OnRemoveListener, OnInteractionListener {
    SavedArticlesFragment listFragment;

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

        if(savedInstanceState == null) {
            addFragments();
        }
    }

    // The other activities have to override this to change the fragment type
    public void addFragments() {
        listFragment = SavedArticlesFragment.newInstance();
        getSupportFragmentManager().beginTransaction().add(R.id.list_container, listFragment).commit();

        if(isTablet()) {
            ArticleFragment articleFragment = ArticleFragment.newInstance(Uri.parse("http://tvtropes.org"));
            getSupportFragmentManager().beginTransaction().add(R.id.article_container, articleFragment).commit();
        }
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
        // Can't call this from the list fragment, would cause a lot of problems
    }

    @Override
    public void onLinkClicked(Uri url) {
        if(isTablet()) {
            ArticleFragment f = ArticleFragment.newInstance(url);
            getSupportFragmentManager().beginTransaction().replace(R.id.article_container, f).commit();
        }
        else {
            application.loadArticle(url);
        }
    }

    @Override
    public void onLoadStart() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onLoadFinish(Object result) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onLoadError(Exception e) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onRemoveSuccess(ArticleItem item) {
        invalidateOptionsMenu();
        listFragment.reloadList();
        UIFunctions.showToast(getResources().getString(R.string.article_removed) + item.title, this);
    }

    @Override
    public void onRemoveError() {
        UIFunctions.showToast(getResources().getString(R.string.article_remove_failed),  this);
    }

    @Override
    public void onSaveSuccess(ArticleItem item) {
        invalidateOptionsMenu();
        listFragment.reloadList();
        UIFunctions.showToast(getResources().getString(R.string.article_saved) + item.title, this);
    }

    @Override
    public void onSaveError() {
        UIFunctions.showToast(getResources().getString(R.string.article_save_failed), this);
    }
}
