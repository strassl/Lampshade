package eu.prismsw.lampshade;

import eu.prismsw.lampshade.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebView;

/** All other activities are accessed from this one */
public class HelpActivity extends Activity {
	TropesApplication application;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
		this.application = (TropesApplication) getApplication();
		application.switchTheme(this);
		
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help_activity);
        
        this.setTitle("Help");
        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        
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