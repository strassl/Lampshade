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
		
		// TODO Replace this with a more modern implementation, as soon as Gingerbread goes out of style, that is
        addPreferencesFromResource(R.xml.preferences);
	}
	
	// Unfortunately this Activity has to inherit from PreferenceActivity and thus cannot be a subclass of BaseActivity
    public void switchTheme() {
        String theme = getThemeName();

        if(theme.equalsIgnoreCase("HoloDark")) {
            setTheme(R.style.LampshadeDark);
        }
        else if(theme.equalsIgnoreCase("HoloDarkActionBar")) {
            setTheme(R.style.LampshadeLightDarkActionBar);
        }
        else {
            setTheme(R.style.LampshadeLight);
        }
    }

    public String getThemeName() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String theme = preferences.getString("preference_theme", "HoloLight");
        return theme;
    }
}
