package eu.prismsw.lampshade;

import eu.prismsw.lampshade.R;
import eu.prismsw.lampshade.fragments.SearchFragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

/** All other activities are accessed from this one */
public class MainActivity extends BaseActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.main_menu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
        case R.id.show_preferences:
        	application.openActivity(LampshadePreferenceActivity.class);
        	return true;
        case R.id.show_about:
        	application.openActivity(AboutActivity.class);
        	return true;
        case R.id.show_help:
        	application.openActivity(HelpActivity.class);
        	return true;
        default:
        	return super.onOptionsItemSelected(item);
        }
    }
        
    public void clickHandler(View v) {
    	switch(v.getId()) {
    	case R.id.btn_load:
    		EditText page_selection = (EditText) findViewById(R.id.et_search);
    		
			Intent searchIntent = new Intent(getApplicationContext(), SearchActivity.class);
			searchIntent.putExtra(SearchFragment.QUERY_KEY, page_selection.getText().toString());
			searchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(searchIntent);
    		break;
    	case R.id.btn_random:
    		application.loadPage(Uri.parse(TropesApplication.randomUrl));
    		break;
    	case R.id.btn_tropes:
    		application.loadPage(Uri.parse(TropesApplication.tropesUrl));
    		break;
    	case R.id.btn_saved:
    		application.openActivity(SavedArticlesActivity.class);
    		break;
    	}
    }
}