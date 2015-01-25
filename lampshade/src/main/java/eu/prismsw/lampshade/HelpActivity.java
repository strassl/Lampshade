package eu.prismsw.lampshade;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebView;


public class HelpActivity extends BaseActivity {
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help_activity);
        
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
            startActivity(new Intent(this, MainActivity.class));
        	return true;
        default:
        	return super.onOptionsItemSelected(item);
        }
    }
}