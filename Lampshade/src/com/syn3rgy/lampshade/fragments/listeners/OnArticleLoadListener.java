package com.syn3rgy.lampshade.fragments.listeners;

import com.syn3rgy.tropeswrapper.TropesArticleInfo;

public interface OnArticleLoadListener {
	public void onLoadStart();
	public void onLoadFinish(TropesArticleInfo info);
	public void onLoadError(Exception e);
}