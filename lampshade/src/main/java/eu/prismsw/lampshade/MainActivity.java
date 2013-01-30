package eu.prismsw.lampshade;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import eu.prismsw.lampshade.fragments.SearchFragment;
import eu.prismsw.tropeswrapper.TropesHelper;

/** All other activities are accessed from this one */
public class MainActivity extends BaseActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        
        EditText search = (EditText) findViewById(R.id.et_search);
        search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			if (actionId == EditorInfo.IME_ACTION_SEARCH ||
						actionId == EditorInfo.IME_ACTION_GO ||
						actionId == EditorInfo.IME_ACTION_DONE ||
		                event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
				
					startSearch(v.getText().toString());
					return true;
				}
				return false;
			}
		});
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater inflater = this.getSupportMenuInflater();
    	inflater.inflate(R.menu.main_menu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.show_preferences) {
            startActivity(new Intent(this, LampshadePreferenceActivity.class));
			return true;
		} else if (item.getItemId() == R.id.show_about) {
            startActivity(new Intent(this, AboutActivity.class));
			return true;
		} else if (item.getItemId() == R.id.show_help) {
            startActivity(new Intent(this, HelpActivity.class));
			return true;
		} else if (item.getItemId() == R.id.show_recent) {
            startActivity(new Intent(this, RecentArticlesActivity.class));
			return true;
		} else {
			return super.onOptionsItemSelected(item);
		}
    }
        
    public void clickHandler(View v) {
    	if(v != null) {
			if (v.getId() == R.id.btn_random) {
				application.loadPage(Uri.parse(TropesHelper.randomUrl));
			} else if (v.getId() == R.id.btn_tropes) {
				application.loadPage(Uri.parse(TropesHelper.tropesUrl));
			} else if (v.getId() == R.id.btn_saved) {
                startActivity(new Intent(this, SavedArticlesActivity.class));
			} else if (v.getId() == R.id.btn_favorites) {
                startActivity(new Intent(this, FavoriteArticlesActivity.class));
			}
    	}
    }
    
    private void startSearch(String query) {
				Intent searchIntent = new Intent(getApplicationContext(), SearchActivity.class);
				searchIntent.putExtra(SearchFragment.QUERY_KEY, query);
				searchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(searchIntent);
    }
}