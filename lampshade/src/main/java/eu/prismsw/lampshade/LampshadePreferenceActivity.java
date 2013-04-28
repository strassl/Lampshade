package eu.prismsw.lampshade;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import com.actionbarsherlock.app.SherlockPreferenceActivity;

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
        String theme = getThemeName();

        if(theme.equalsIgnoreCase("HoloDark")) {
            setTheme(com.actionbarsherlock.R.style.Theme_Sherlock);
        }
        else if(theme.equalsIgnoreCase("HoloDarkActionBar")) {
            setTheme(R.style.Theme_Sherlock_Light_DarkActionBar);
        }
    }

    public String getThemeName() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String theme = preferences.getString("preference_theme", "HoloLight");
        return theme;
    }
}
