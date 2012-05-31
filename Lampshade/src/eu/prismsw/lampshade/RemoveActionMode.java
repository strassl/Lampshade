package eu.prismsw.lampshade;


import android.net.Uri;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import eu.prismsw.lampshade.listeners.OnRemoveListener;
import eu.prismsw.lampshade.tasks.RemoveArticleTask;
import eu.prismsw.tools.android.UIFunctions;
import eu.prismsw.tropeswrapper.TropesHelper;


public class RemoveActionMode implements OnRemoveListener {
	public SherlockFragmentActivity activity; 
	
	public ActionMode mActionMode;
	public Uri selectedUrl;
	
	
	public RemoveActionMode(SherlockFragmentActivity activity) {
		this.activity = activity;
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
			if(((TropesApplication)activity.getApplication()).getThemeName().equalsIgnoreCase("HoloDark")) {
		        inflater.inflate(R.menu.saved_action_menu_dark, menu);
			}
			else {
		        inflater.inflate(R.menu.saved_action_menu_light, menu);
			}
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
		new RemoveArticleTask((TropesApplication)activity.getApplication(), this).execute(url);
	}

	@Override
	public void onRemoveSuccess(ArticleItem item) {
		OnRemoveListener removeListener = (OnRemoveListener) activity;
		removeListener.onRemoveSuccess(item);
	}

	@Override
	public void onRemoveError() {
		OnRemoveListener removeListener = (OnRemoveListener) activity;
		removeListener.onRemoveError();
	}
}
