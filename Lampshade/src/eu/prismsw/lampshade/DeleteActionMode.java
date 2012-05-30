package eu.prismsw.lampshade;


import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;


public class DeleteActionMode {
	public SherlockFragmentActivity activity; 
	
	public ActionMode mActionMode;
	public ArticleItem selectedItem;
	
	public DeleteActionMode(SherlockFragmentActivity activity) {
		this.activity = activity;
	}
	
	public void startActionMode(ArticleItem item) {
    	if (mActionMode != null) {
    		mActionMode.finish();
        }
    	
    	selectedItem = item;

        mActionMode = activity.startActionMode(mActionModeCallback);
	}
	
	private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {
		
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			return false;
		}
		
		public void onDestroyActionMode(ActionMode mode) {
			mActionMode = null;
			selectedItem = null;
		}
		
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			MenuInflater inflater = mode.getMenuInflater();
			if(((TropesApplication)activity.getApplication()).getThemeName().equalsIgnoreCase("HoloDark")) {
		        inflater.inflate(R.menu.saved_action_menu_dark, menu);
			}
			else {
		        inflater.inflate(R.menu.saved_action_menu_light, menu);
			}
			if(selectedItem != null) {
				mode.setTitle(selectedItem.title);
			}
			return true;
		}
		
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			if(item.getItemId() == R.id.saved_action_delete) {
				if(selectedItem != null) {
					deleteArticle(selectedItem);
					mode.finish();
					return true;
				}
				else {
					return false;
				}
			}
			else {
				return false;
			}
		}
	};
	
	private void deleteArticle(ArticleItem item) {
		SavedArticlesActivity sActivity = (SavedArticlesActivity) activity;
		sActivity.removeArticle(item);
	}
}
