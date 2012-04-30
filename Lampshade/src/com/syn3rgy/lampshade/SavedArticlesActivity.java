package com.syn3rgy.lampshade;

import java.util.List;

import android.app.ActionBar;
import android.app.ListActivity;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/** Shows a list of saved articles */
public class SavedArticlesActivity extends ListActivity{
	TropesApplication application;
	ActionMode mActionMode = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
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
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		ArticleItem item = (ArticleItem) getListAdapter().getItem(position);
		removeArticle(item);
		application.loadPage(item.url.toString());
	}
	
	// Function to remove the clutter from the onCreate method
	private void prepare() {
		ActionBar ab = getActionBar();
		ab.setHomeButtonEnabled(true);
		ab.setDisplayHomeAsUpEnabled(true);
				
		application = (TropesApplication) getApplication();
		
		loadArticles();
		registerForContextMenu(getListView());
		// Implement multi-selection contextual ActionBar
		getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
		getListView().setMultiChoiceModeListener(new MultiChoiceModeListener() {

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
		        inflater.inflate(R.menu.saved_action_menu, menu);
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
	
	private void loadArticles() {
		application.articlesSource.open();
		List<ArticleItem> savedArticles = application.articlesSource.getAllArticles();
		application.articlesSource.close();

		ArrayAdapter<ArticleItem> adapter = new ArrayAdapter<ArticleItem>(this, android.R.layout.simple_list_item_activated_1, savedArticles);
		setListAdapter(adapter);
	}
}
