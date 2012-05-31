package eu.prismsw.lampshade;

import java.util.List;

import android.net.Uri;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import eu.prismsw.lampshade.listeners.OnSaveListener;
import eu.prismsw.lampshade.tasks.SaveArticleTask;
import eu.prismsw.tropeswrapper.TropesHelper;

public class LinkActionMode implements OnSaveListener {
	public SherlockFragmentActivity activity; 
	
	public ActionMode mActionMode;
	public Uri selectedLink;
	
	public LinkActionMode(SherlockFragmentActivity activity) {
		this.activity = activity;
	}
	
	public void startActionMode(Uri url) {
    	if (mActionMode != null) {
    		mActionMode.finish();
        }
    	
    	selectedLink = url;

        mActionMode = activity.startActionMode(mActionModeCallback);
	}
	
	private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {
		
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			return false;
		}
		
		public void onDestroyActionMode(ActionMode mode) {
			mActionMode = null;
			selectedLink = null;
		}
		
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			MenuInflater inflater = mode.getMenuInflater();
			if(((TropesApplication)activity.getApplication()).getThemeName().equalsIgnoreCase("HoloDark")) {
		        inflater.inflate(R.menu.article_action_menu_dark, menu);
			}
			else {
		        inflater.inflate(R.menu.article_action_menu_light, menu);
			}
			if(selectedLink != null) {
				
				if(TropesHelper.isTropesLink(selectedLink)) {
					String title = TropesHelper.titleFromUrl(selectedLink);
					mode.setTitle(title);
				}
				else {
					String host = selectedLink.getHost();
					mode.setTitle(host);
				}
			}
			return true;
		}
		
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			if(item.getItemId() == R.id.article_action_save) {
				if(selectedLink != null) {
					saveArticle(selectedLink);
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
	
	private void saveArticle(Uri url) {
		new SaveArticleTask((TropesApplication) activity.getApplication(), this).execute(url);
	}
	
	@Override
	public void onSaveSuccess(ArticleItem item) {
		OnSaveListener saveListener = (OnSaveListener) activity;
		saveListener.onSaveSuccess(item);
	}

	@Override
	public void onSaveError() {
		OnSaveListener saveListener = (OnSaveListener) activity;
		saveListener.onSaveError();
	}
}
