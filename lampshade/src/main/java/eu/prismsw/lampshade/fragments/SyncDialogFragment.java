package eu.prismsw.lampshade.fragments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.widget.Toast;
import com.actionbarsherlock.app.SherlockDialogFragment;
import com.koushikdutta.async.future.Future;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import eu.prismsw.lampshade.R;
import eu.prismsw.tropeswrapper.TropesArticle;

public class SyncDialogFragment extends SherlockDialogFragment {
    Future<String> mainJS;
    ProgressDialog syncDialog;

    public static SyncDialogFragment newInstance() {
        SyncDialogFragment f = new SyncDialogFragment();
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        ProgressDialog dialog = new ProgressDialog(getActivity());

        dialog.setTitle(R.string.sync_title);
        dialog.setIndeterminate(false);
        dialog.setCancelable(true);
        dialog.setMax(100);
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                mainJS.cancel(true);
            }
        });
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                ProgressDialog d = (ProgressDialog) getDialog();
                mainJS = Ion.with(getActivity(), TropesArticle.MAIN_JS_URL)
                        .progressDialog(d)
                        .asString()
                        .setCallback(new FutureCallback<String>() {
                            @Override
                            public void onCompleted(Exception e, String s) {
                                if(e != null) {
                                    Toast.makeText(getActivity(), "Failed!", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    // TODO Debugging only, get rid of this
                                    FragmentManager fm = getActivity().getSupportFragmentManager();
                                    AlertDialogFragment f = AlertDialogFragment.newInstance("MainJS", s);
                                    f.show(fm, "result_sync");
                                }
                                dismiss();
                            }
                        });
            }
        });

        return dialog;
    }
}
