package eu.prismsw.lampshade;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import eu.prismsw.lampshade.fragments.SearchFragment;


/** All other activities are accessed from this one */
public class MainActivity extends BaseActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
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
			application.openActivity(LampshadePreferenceActivity.class);
			return true;
		} else if (item.getItemId() == R.id.show_about) {
			application.openActivity(AboutActivity.class);
			return true;
		} else if (item.getItemId() == R.id.show_help) {
			application.openActivity(HelpActivity.class);
			return true;
		} else {
			return super.onOptionsItemSelected(item);
		}
    }
        
    public void clickHandler(View v) {
    	if(v != null) {
	    	if (v.getId() == R.id.btn_load) {
				EditText page_selection = (EditText) findViewById(R.id.et_search);
				Intent searchIntent = new Intent(getApplicationContext(), SearchActivity.class);
				searchIntent.putExtra(SearchFragment.QUERY_KEY, page_selection.getText().toString());
				searchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(searchIntent);
			} else if (v.getId() == R.id.btn_random) {
				application.loadPage(Uri.parse(TropesApplication.randomUrl));
			} else if (v.getId() == R.id.btn_tropes) {
				application.loadPage(Uri.parse(TropesApplication.tropesUrl));
			} else if (v.getId() == R.id.btn_saved) {
				application.openActivity(SavedArticlesActivity.class);
			}
    	}
    }
}