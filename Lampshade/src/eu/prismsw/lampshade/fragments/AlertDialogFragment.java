package eu.prismsw.lampshade.fragments;

import com.actionbarsherlock.app.SherlockDialogFragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Html;

public class AlertDialogFragment extends SherlockDialogFragment {
	static final String KEY_TITLE = "title";
	static final String KEY_CONTENT = "content";
	String title;
	String content;
	
	public static AlertDialogFragment newInstance(String title, String content) {
		AlertDialogFragment f = new AlertDialogFragment();

        Bundle args = new Bundle();
        args.putString(KEY_TITLE, title);
        args.putString(KEY_CONTENT, content);
        f.setArguments(args);

        return f;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		String title = getArguments().getString(KEY_TITLE);
		String content = getArguments().getString(KEY_CONTENT);
		
		this.title = title;
		this.content = content;
	}
	
	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        
        return new AlertDialog.Builder(getActivity())
                .setTitle(this.title)
                .setMessage(Html.fromHtml(this.content))
                .setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                        	dialog.dismiss();
                        }
                    }
                )
                .create();
    }
}


