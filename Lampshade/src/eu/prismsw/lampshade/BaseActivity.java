package eu.prismsw.lampshade;

import android.app.Activity;
import android.os.Bundle;

public class BaseActivity extends Activity {
	TropesApplication application;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
        this.application = (TropesApplication) getApplication(); 
        application.switchTheme(this);
		
		super.onCreate(savedInstanceState);
	}
}
