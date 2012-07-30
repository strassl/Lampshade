package eu.prismsw.lampshade;

import com.actionbarsherlock.app.SherlockPreferenceActivity;

import android.os.Bundle;

public class LampshadePreferenceActivity extends SherlockPreferenceActivity {
	TropesApplication application;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
        this.application = (TropesApplication) getApplication(); 
        this.switchTheme();
        
		super.onCreate(savedInstanceState);
		
		// TODO Replace this with a more modern implementation
        addPreferencesFromResource(R.xml.preferences);
	}
	
	// Unfortunately this Activity has to inherit from PreferenceActivity and thus cannot be a subclass of BaseActivity
	public void switchTheme() {
		String theme = application.getThemeName();
		
		if(theme.equalsIgnoreCase("HoloDark")) {
			this.setTheme(android.R.style.Theme_Holo);
		}
	}
}
