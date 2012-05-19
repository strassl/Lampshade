package com.syn3rgy.lampshade.fragments;

import com.syn3rgy.tropeswrapper.TropesArticleInfo;

import android.net.Uri;

public interface IArticleFragment {
	public void loadArticle(Uri url);
	
	public Uri getTrueUrl();
	public Uri getPassedUrl();
	public TropesArticleInfo getArticleInfo();
}
