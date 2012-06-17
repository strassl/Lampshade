package eu.prismsw.lampshade;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import eu.prismsw.lampshade.fragments.AlertDialogFragment;
import eu.prismsw.lampshade.fragments.SearchFragment;

/** All other activities are accessed from this one */
public class MainActivity extends BaseActivity {
	private static String KEY_UPDATE = "lastUpdate";
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater inflater = this.getSupportMenuInflater();
    	inflater.inflate(R.menu.main_menu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.show_preferences) {
			application.openActivity(LampshadePreferenceActivity.class);
			return true;
		} else if (item.getItemId() == R.id.show_about) {
			application.openActivity(AboutActivity.class);
			return true;
		} else if (item.getItemId() == R.id.show_help) {
			application.openActivity(HelpActivity.class);
			return true;
		} else if (item.getItemId() == R.id.show_recent) {
			application.openActivity(RecentArticlesActivity.class);
			return true;
		} else {
			return super.onOptionsItemSelected(item);
		}
    }
        
    public void clickHandler(View v) {
    	if(v != null) {
	    	if (v.getId() == R.id.btn_load) {
				EditText page_selection = (EditText) findViewById(R.id.et_search);
				Intent searchIntent = new Intent(getApplicationContext(), SearchActivity.class);
				searchIntent.putExtra(SearchFragment.QUERY_KEY, page_selection.getText().toString());
				searchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(searchIntent);
			} else if (v.getId() == R.id.btn_random) {
				application.loadPage(Uri.parse(TropesApplication.randomUrl));
			} else if (v.getId() == R.id.btn_tropes) {
				application.loadPage(Uri.parse(TropesApplication.tropesUrl));
			} else if (v.getId() == R.id.btn_saved) {
				application.openActivity(SavedArticlesActivity.class);
			}
    	}
    }
    
    private void tryUpdateCheck() {
    	SharedPreferences prefs = getPreferences(0);
        Long lastUpdate =  prefs.getLong(KEY_UPDATE, MODE_PRIVATE);
        
        Long nextUpdateTime = lastUpdate + (24 * 60 * 60 * 1000);
        Long currentTime = System.currentTimeMillis();
        if (nextUpdateTime < currentTime) {
            SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
            editor.putLong(KEY_UPDATE, currentTime);
            editor.commit();        

	        new CheckUpdateTask(this).execute(Uri.parse(TropesApplication.versionUrl));
        }

    }
    
    private class CheckUpdateTask extends AsyncTask<Uri, Integer, VersionInformation> {
    	public BaseActivity activity;
    	
    	public CheckUpdateTask(BaseActivity activity) {
    		this.activity = activity;
    	}

		@Override
		protected VersionInformation doInBackground(Uri... params) {
			VersionInformation newestVersion = getNewestVersion(params[0]);
			return newestVersion;
		}
		
		@Override
		protected void onPostExecute(VersionInformation newestVersion) {
	    	Integer currentVersion = getApplicationVersion();
	    	
	    	if(currentVersion > -1 && newestVersion.versionNumber > -1 && newestVersion.versionNumber > currentVersion) {
				AlertDialogFragment f = AlertDialogFragment.newInstance(newestVersion.versionString + " available", newestVersion.information + "<br /><br /><strong>Changelog</strong><br />" + newestVersion.changelog);
				activity.showDialogFragment(f);
			}
		}
    	
	    private Integer getApplicationVersion() {
	    	Integer versionNumber = -1;
			try {
				PackageInfo pInfo = activity.getPackageManager().getPackageInfo(getPackageName(), MODE_PRIVATE);
		    	versionNumber = pInfo.versionCode;
			} catch (Exception e) {
				e.printStackTrace();
			}
		    return versionNumber;
	    }
	    
	    private VersionInformation getNewestVersion(Uri url) {
	    	return new VersionInformation(url);
	    }
    }
}