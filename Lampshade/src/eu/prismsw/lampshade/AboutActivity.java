package eu.prismsw.lampshade;

import android.app.Activity;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.MenuItem;
import android.widget.TextView;

public class AboutActivity extends Activity {
	TropesApplication application;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about_activity);
		
		this.application = (TropesApplication) getApplication();
		
		getActionBar().setHomeButtonEnabled(true);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		makeLinksClickable();
	}
	
	private void makeLinksClickable() {
		TextView tvCopyright = (TextView) findViewById(R.id.tv_copyright);
		tvCopyright.setMovementMethod(LinkMovementMethod.getInstance());
		
		TextView tvLinks = (TextView) findViewById(R.id.tv_links);
		tvLinks.setMovementMethod(LinkMovementMethod.getInstance());
	}
	
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
        case android.R.id.home:
        	application.openActivity(MainActivity.class);
        	return true;
        default:
        	return super.onOptionsItemSelected(item);
        }
    }
}
