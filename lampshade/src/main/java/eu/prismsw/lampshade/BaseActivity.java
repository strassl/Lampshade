package eu.prismsw.lampshade;

import com.actionbarsherlock.app.SherlockFragmentActivity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

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
	
	public void showDialogFragment(DialogFragment fragment) {
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
	    Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
	    if (prev != null) {
	        ft.remove(prev);
	    }
	    ft.addToBackStack(null);

	    fragment.show(ft, "dialog");
	}

    public boolean isTablet() {
        boolean xlarge = ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == 4);
        boolean large = ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE);
        return (xlarge || large);
    }

}
