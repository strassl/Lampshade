package eu.prismsw.lampshade;

import com.actionbarsherlock.app.SherlockFragmentActivity;

import android.os.Bundle;

public class BaseActivity extends SherlockFragmentActivity {
	TropesApplication application;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
        this.application = (TropesApplication) getApplication(); 
        this.switchTheme();
		
		super.onCreate(savedInstanceState);
	}
	
	public void switchTheme() {
		String theme = application.getThemeName();
		
		if(theme.equalsIgnoreCase("HoloDark")) {
			this.setTheme(com.actionbarsherlock.R.style.Theme_Sherlock);
		}
	}
}
