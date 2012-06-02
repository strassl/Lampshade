package eu.prismsw.lampshade;

import com.actionbarsherlock.app.SherlockFragmentActivity;

import android.os.Bundle;

/** Contains some functionality (such as theme switching) that is universal for all activities. All other activities are supposed to be subclass of this class. **/
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
