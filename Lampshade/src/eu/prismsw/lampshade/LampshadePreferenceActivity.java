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
	}
	
	public void switchTheme() {
		String theme = getThemeName();
		
		if(theme.equalsIgnoreCase("HoloDark")) {
			this.setTheme(android.R.style.Theme_Holo);
		}
	}
	
	public String getThemeName() {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		String theme = preferences.getString("preference_theme", "HoloLight");
		return theme;
	}
}
