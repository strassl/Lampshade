package eu.prismsw.lampshade;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;
import com.actionbarsherlock.view.MenuItem;

public class AboutActivity extends BaseActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about_activity);
		
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        addExtraInfo();
		makeLinksClickable();
	}
	
	private void makeLinksClickable() {
		TextView tvCopyright = (TextView) findViewById(R.id.tv_copyright);
		tvCopyright.setMovementMethod(LinkMovementMethod.getInstance());
		
		TextView tvLinks = (TextView) findViewById(R.id.tv_links);
		tvLinks.setMovementMethod(LinkMovementMethod.getInstance());
	}

    private void addExtraInfo() {
        TextView tvSubtitle = (TextView) findViewById(R.id.tv_subtitle);
        String versionString = "";
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            versionString = "\nv" + pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        tvSubtitle.setText(tvSubtitle.getText() + versionString);
    }
	
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
        case android.R.id.home:
            startActivity(new Intent(this, MainActivity.class));
        	return true;
        default:
        	return super.onOptionsItemSelected(item);
        }
    }
}
