package eu.prismsw.lampshade;

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
