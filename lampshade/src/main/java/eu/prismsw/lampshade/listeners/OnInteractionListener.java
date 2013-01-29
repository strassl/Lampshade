package eu.prismsw.lampshade.listeners;

import android.net.Uri;

/** 
 * Must be implement by any activity that wishes to use a subclass of TropesFragment 
 * Handles interaction with the article's content (mainly links)
 **/
public interface OnInteractionListener {
	/** Is called when a link is selected (long press) **/
	public void onLinkSelected(Uri url);
	
	/** Is called when a link is clicked (short press) **/
	public void onLinkClicked(Uri url);
}
