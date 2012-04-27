package com.syn3rgy.lampshade;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

/** All other activities are accessed from this one */
public class MainActivity extends Activity {
	TropesApplication application;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        this.application = (TropesApplication) getApplication(); 
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
        case R.id.show_saved_articles:
        	openActivity(SavedArticlesActivity.class);
        	return true;
        default:
        	return super.onOptionsItemSelected(item);
        }
    }
        
    public void clickHandler(View v) {
    	switch(v.getId()) {
    	case R.id.btn_load:
    		// Constructs the url of the article
    		EditText page_selection = (EditText) findViewById(R.id.et_enter_page);
    		String url = application.baseUrl + page_selection.getText().toString();
    		loadPage(url);
    		break;
    	case R.id.btn_random:
    		loadPage(application.randomUrl);
    		break;
    	case R.id.btn_tropes:
    		loadPage(application.tropesUrl);
    	}
    }
    
    //TODO Copy&Paste is a horrible way to share these functions, I need a better way
    
    private void loadPage(String url) {
    	Intent pageIntent = new Intent(getApplicationContext(), ArticleActivity.class);
    	pageIntent.setData(Uri.parse(url));
    	startActivity(pageIntent);
    }
    
    private void openActivity(Class cls) {
    	Intent intent = new Intent(getApplicationContext(), cls);
    	startActivity(intent);
    }
}