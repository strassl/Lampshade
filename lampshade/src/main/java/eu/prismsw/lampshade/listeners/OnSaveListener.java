package eu.prismsw.lampshade.listeners;

import android.net.Uri;

/** 
 * Must be implement by any Activity/Fragment/whatever that wishes to use a SaveArticleTask
 **/
public interface OnSaveListener {
	public void onSaveFinish(Uri url);
}
