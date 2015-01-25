package eu.prismsw.lampshade;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import eu.prismsw.lampshade.fragments.SyncDialogFragment;

public class DebugActivity extends Activity {
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
        s.show(getFragmentManager(), "sync_dialog");
    }
}
