package eu.prismsw.tools.android;

import android.content.Context;
import android.widget.Toast;

public class UIFunctions {
	/** Simple way to show a short Toast **/
	public static void showToast(String message, Context context) {
		Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
	}
}
