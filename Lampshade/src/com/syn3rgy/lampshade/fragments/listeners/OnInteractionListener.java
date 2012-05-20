package com.syn3rgy.lampshade.fragments.listeners;

import android.net.Uri;

public interface OnInteractionListener {
	public void onLinkSelected(Uri url);
	public void onLinkClicked(Uri url);
}
