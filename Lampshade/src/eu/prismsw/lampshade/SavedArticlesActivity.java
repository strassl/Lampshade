package eu.prismsw.lampshade;

import java.util.List;

import eu.prismsw.lampshade.R;

import android.app.ActionBar;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/** Shows a list of saved articles */
public class SavedArticlesActivity extends BaseActivity{
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.saved_articles_activity);
		
		prepare();
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
	
	// Function to remove the clutter from the onCreate method
	private void prepare() {
		ActionBar ab = getActionBar();
		ab.setHomeButtonEnabled(true);
		ab.setDisplayHomeAsUpEnabled(true);
		
		loadArticles();
		ListView lv = getListView();
		
		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				ListView lv = getListView();
				ArticleItem item = (ArticleItem) lv.getAdapter().getItem(position);
				removeArticle(item);
				application.loadPage(item.url);
			}
		});
		
		registerForContextMenu(lv);
		// Implement multi-selection contextual ActionBar
		lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
		lv.setMultiChoiceModeListener(new MultiChoiceModeListener() {

			public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
				switch(item.getItemId()) {
				case R.id.saved_action_delete:
					deleteSelectedArticles();
					mode.finish();
					return true;
				default:
					return false;
				}
			}

			public boolean onCreateActionMode(ActionMode mode, Menu menu) {
				MenuInflater inflater = mode.getMenuInflater();
				if(application.getThemeName().equalsIgnoreCase("HoloDark")) {
			        inflater.inflate(R.menu.saved_action_menu_dark, menu);
				}
				else {
			        inflater.inflate(R.menu.saved_action_menu_light, menu);
				}
		        mode.setTitle("Select items...");
		        return true;
			}

			public void onDestroyActionMode(ActionMode mode) {				
			}

			public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
				return false;
			}

			public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {				
			}
			
		});
	}
	
	private void deleteSelectedArticles() {
		SparseBooleanArray checked = getListView().getCheckedItemPositions();
		
		for(int i = 0; i < checked.size(); i++) {
			int key = checked.keyAt(i);
			if(checked.get(key)) {
				ArticleItem item = (ArticleItem) getListView().getItemAtPosition(key);
				if(item != null) {
					removeArticle(item);
				}
			}
		}
	}
		
	private void removeArticle(ArticleItem item) {
		application.articlesSource.open();
		application.articlesSource.removeArticle(item);
		application.articlesSource.close();
		
		loadArticles();
	}
	
	/** Loads the saved articles from the database and sets the ArrayAdapter */
	private void loadArticles() {
		application.articlesSource.open();
		List<ArticleItem> savedArticles = application.articlesSource.getAllArticles();
		application.articlesSource.close();

		ArrayAdapter<ArticleItem> adapter = new ArrayAdapter<ArticleItem>(this, android.R.layout.simple_list_item_activated_1, savedArticles);
		getListView().setAdapter(adapter);
	}
}
