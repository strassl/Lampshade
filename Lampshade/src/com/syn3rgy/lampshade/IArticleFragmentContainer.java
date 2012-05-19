package com.syn3rgy.lampshade;

import android.net.Uri;

import com.syn3rgy.tropeswrapper.TropesArticleInfo;

public interface IArticleFragmentContainer {
	public Uri getUrl();
	
	public void onLoadFinished(TropesArticleInfo info);
	public void onLoadError(Exception e);
	public void onLinkSelected(Uri url);
	public void onLinkClicked(Uri url);
}
