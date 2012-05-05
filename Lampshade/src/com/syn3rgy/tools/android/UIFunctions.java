package com.syn3rgy.tools.android;

import android.content.Context;
import android.widget.Toast;

public class UIFunctions {
	public static void showToast(String message, Context context) {
		Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
	}
}
