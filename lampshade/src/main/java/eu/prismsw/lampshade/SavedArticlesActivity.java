package eu.prismsw.lampshade;

import android.content.Intent;
import android.os.Bundle;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.MenuItem;
import eu.prismsw.lampshade.database.ArticleItem;
import eu.prismsw.lampshade.fragments.SavedArticlesFragment;
import eu.prismsw.lampshade.listeners.OnRemoveListener;
import eu.prismsw.tools.android.UIFunctions;

public class SavedArticlesActivity extends BaseActivity implements OnRemoveListener {
    SavedArticlesFragment listFragment;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.saved_articles_activity);
		
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

        getSupportFragmentManager().beginTransaction().add(android.R.id.content, listFragment).commit();
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
	public void onRemoveSuccess(ArticleItem item) {
		UIFunctions.showToast(getResources().getString(R.string.article_removed) + item.title, this);
        listFragment.reloadList();
	}

	@Override
	public void onRemoveError() {
		UIFunctions.showToast(getResources().getString(R.string.article_remove_failed),  this);
	}
}
