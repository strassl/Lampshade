package eu.prismsw.lampshade;

import android.net.Uri;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import eu.prismsw.lampshade.database.SavedArticlesHelper;
import eu.prismsw.lampshade.listeners.OnRemoveListener;
import eu.prismsw.tropeswrapper.TropesHelper;

/** Wraps an ActionMode for selected links (that can be removed) into a nice handy package **/
public class RemoveActionMode {
	public SherlockFragmentActivity activity; 
	
	public ActionMode mActionMode;
	public Uri selectedUrl;

    public Uri contentUri;

	public RemoveActionMode(SherlockFragmentActivity activity, Uri contentUri) {
		this.activity = activity;
        this.contentUri = contentUri;
	}
	
	public void startActionMode(Uri url) {
    	if (mActionMode != null) {
    		mActionMode.finish();
        }
    	
    	selectedUrl = url;
    			
        mActionMode = activity.startActionMode(mActionModeCallback);
	}
	
	private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {
		
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			return false;
		}
		
		public void onDestroyActionMode(ActionMode mode) {
			mActionMode = null;
			selectedUrl = null;
		}
		
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			MenuInflater inflater = mode.getMenuInflater();

            inflater.inflate(R.menu.remove_action_menu, menu);
			if(selectedUrl != null) {
				if(TropesHelper.isTropesLink(selectedUrl)) {
					mode.setTitle(TropesHelper.titleFromUrl(selectedUrl));
				}
				else {
					mode.setTitle(selectedUrl.getHost());
				}
			}
			return true;
		}
		
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			if(item.getItemId() == R.id.saved_action_delete) {
				if(selectedUrl != null) {
					removeArticle(selectedUrl);
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
	
	private void removeArticle(Uri url) {
        int affected = activity.getContentResolver().delete(contentUri, SavedArticlesHelper.ARTICLES_COLUMN_URL + "=?", new String[] {url.toString()});
        OnRemoveListener removeListener = (OnRemoveListener) activity;
        removeListener.onRemoveFinish(affected);
	}
}
