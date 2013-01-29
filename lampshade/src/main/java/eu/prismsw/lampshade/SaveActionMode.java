package eu.prismsw.lampshade;

import android.net.Uri;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import eu.prismsw.lampshade.database.ArticleItem;
import eu.prismsw.lampshade.database.ArticlesSource;
import eu.prismsw.lampshade.listeners.OnSaveListener;
import eu.prismsw.lampshade.tasks.SaveArticleTask;
import eu.prismsw.tropeswrapper.TropesHelper;

/** Wraps an ActionMode for selected links (that can be saved) into a nice handy package **/
public class SaveActionMode implements OnSaveListener {
	public SherlockFragmentActivity activity; 
	
	public ActionMode mActionMode;
	public Uri selectedLink;
	
	public ArticlesSource articlesSource;
	
	public SaveActionMode(SherlockFragmentActivity activity, ArticlesSource articlesSource) {
		this.activity = activity;
		this.articlesSource = articlesSource;
	}
	
	/** Starts a "new" ActionMode for the passed url **/
	public void startActionMode(Uri url) {
		// Finish any old ActionMode
    	if (mActionMode != null) {
    		mActionMode.finish();
        }
    	
    	selectedLink = url;

    	// Start the new ActionMode
        mActionMode = activity.startActionMode(mActionModeCallback);
	}
	
	private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {
		
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			return false;
		}
		
		public void onDestroyActionMode(ActionMode mode) {
			// Unset all ActionMode specific variables
			mActionMode = null;
			selectedLink = null;
		}
		
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			MenuInflater inflater = mode.getMenuInflater();
			
			// Switch, depending on the theme color, to the light or dark icons
			if(((TropesApplication)activity.getApplication()).isDarkTheme()) {
		        inflater.inflate(R.menu.article_action_menu_dark, menu);
			}
			else {
		        inflater.inflate(R.menu.article_action_menu_light, menu);
			}
			
			// Set the ActionMode title
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
		new SaveArticleTask(articlesSource, this).execute(url);
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
