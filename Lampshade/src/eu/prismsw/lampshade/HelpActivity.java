package eu.prismsw.lampshade;

import android.os.Bundle;
import android.webkit.WebView;

import com.actionbarsherlock.view.MenuItem;

public class HelpActivity extends BaseActivity {
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help_activity);
        
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        
        setUpWebview();
    }
    
    private void setUpWebview() {
    	WebView wv = (WebView) findViewById(R.id.wv_help);
    	wv.loadUrl(TropesApplication.helpUrl);
    }
    
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
        	application.openActivity(MainActivity.class);
        	return true;
        default:
        	return super.onOptionsItemSelected(item);
        }
    }
}