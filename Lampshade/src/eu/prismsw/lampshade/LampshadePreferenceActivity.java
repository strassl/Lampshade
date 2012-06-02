package eu.prismsw.lampshade;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class LampshadePreferenceActivity extends PreferenceActivity {
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
