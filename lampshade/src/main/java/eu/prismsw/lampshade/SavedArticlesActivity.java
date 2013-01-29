package eu.prismsw.lampshade;

import java.util.List;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.MenuItem;

import eu.prismsw.lampshade.database.ArticleItem;
import eu.prismsw.lampshade.listeners.OnRemoveListener;
import eu.prismsw.lampshade.tasks.RemoveArticleTask;
import eu.prismsw.tools.android.UIFunctions;


/** Shows a list of saved articles */
public class SavedArticlesActivity extends BaseActivity implements OnRemoveListener {
	RemoveActionMode removeActionMode;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.saved_articles_activity);
		
		ActionBar ab = getSupportActionBar();
		ab.setHomeButtonEnabled(true);
		ab.setDisplayHomeAsUpEnabled(true);
		
		// Prepare the ListView
		loadArticles();
		ListView lv = getListView();
		
		lv.setOnItemClickListener(new OnItemClickListener() {
			
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// Remove the article from the database and load it
				ListView lv = getListView();
				ArticleItem item = (ArticleItem) lv.getAdapter().getItem(position);
				new RemoveArticleTask(application.savedArticlesSource, SavedArticlesActivity.this).execute(item.url);
				application.loadPage(item.url);
			}
			
		});
		
		this.removeActionMode = new RemoveActionMode(this, application.savedArticlesSource);
		
		registerForContextMenu(lv);
		lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		
		lv.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				// Start the removeActionMode for the selected item
				ArticleItem item = (ArticleItem) getListView().getAdapter().getItem(position);
				removeActionMode.startActionMode(item.url);
				return true;
			}
			
		});
	}
	
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
        case android.R.id.home:
        	application.openActivity(MainActivity.class);
        	return true;
        default:
        	return super.onOptionsItemSelected(item);
        }
    }
    
    public ListView getListView() {
		ListView lv = (ListView) findViewById(R.id.lv_saved_articles);
		return lv;
    }
	
	/** Loads the saved articles from the database and sets the ArrayAdapter */
	private void loadArticles() {
		application.savedArticlesSource.open();
		List<ArticleItem> savedArticles = application.savedArticlesSource.getAllArticleItems();
		application.savedArticlesSource.close();

		ArrayAdapter<ArticleItem> adapter = new ArrayAdapter<ArticleItem>(this, android.R.layout.simple_list_item_1, savedArticles);
		getListView().setAdapter(adapter);
	}

	@Override
	public void onRemoveSuccess(ArticleItem item) {
		loadArticles();
		UIFunctions.showToast(getResources().getString(R.string.article_removed) + item.title, this);
	}

	@Override
	public void onRemoveError() {
		loadArticles();
		UIFunctions.showToast(getResources().getString(R.string.article_remove_failed),  this);
	}
}
