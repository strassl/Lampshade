package eu.prismsw.lampshade;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class LampshadePreferenceActivity extends PreferenceActivity {
	TropesApplication application;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
        this.application = (TropesApplication) getApplication(); 
        application.switchTheme(this);
        
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
	}
}
