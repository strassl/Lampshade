package eu.prismsw.lampshade;

import android.os.Bundle;
import android.view.View;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import eu.prismsw.lampshade.fragments.SyncDialogFragment;

public class DebugActivity extends SherlockFragmentActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.debug_activity);
    }

    public void clickHandler(View v) {
        if(v.getId() == R.id.btn_sync)
            sync();
    }

    public void sync() {
        SyncDialogFragment s = SyncDialogFragment.newInstance();
        s.show(getSupportFragmentManager(), "sync_dialog");
    }
}
