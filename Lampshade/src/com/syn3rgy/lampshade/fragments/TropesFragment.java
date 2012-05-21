package com.syn3rgy.lampshade.fragments;

import com.syn3rgy.lampshade.R;
import com.syn3rgy.lampshade.TropesApplication;
import com.syn3rgy.lampshade.fragments.listeners.OnArticleLoadListener;
import com.syn3rgy.lampshade.fragments.listeners.OnInteractionListener;
import com.syn3rgy.tropeswrapper.TropesArticleInfo;

import android.app.Activity;
import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class TropesFragment extends Fragment {
	public static String PASSED_URL = "PASSED_URL";
	public static String TRUE_URL = "TRUE_URL";
	
	TropesApplication application;
	OnArticleLoadListener loadListener;
	OnInteractionListener interactionListener;
	
	TropesArticleInfo articleInfo;
	Uri passedUrl;
	Uri trueUrl;
	
	public static TropesFragment newInstance(Uri url) {
		TropesFragment f = new TropesFragment();
		Bundle bundle = new Bundle(2);
		bundle.putParcelable(PASSED_URL, url);
		bundle.putParcelable(TRUE_URL, url);
		f.setArguments(bundle);
		return f;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setHasOptionsMenu(true);
		
		if(savedInstanceState != null) {
			this.passedUrl = savedInstanceState.getParcelable(PASSED_URL);
			this.trueUrl = savedInstanceState.getParcelable(TRUE_URL);
		}
		else {
			this.passedUrl = getArguments().getParcelable(PASSED_URL);
			this.trueUrl = getArguments().getParcelable(TRUE_URL);
		}
		
		loadTropes(this.trueUrl);
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putParcelable(PASSED_URL, this.passedUrl);
		outState.putParcelable(TRUE_URL, this.trueUrl);
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		this.application = (TropesApplication) activity.getApplication();
		
		this.loadListener = (OnArticleLoadListener) activity;
		this.interactionListener = (OnInteractionListener) activity;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup group, Bundle bundle) {
		return inflater.inflate(R.layout.tropes_fragment, group, false);
	}
    
	
	public void loadTropes(Uri url) {
		new LoadTropesTask(this.loadListener, this.interactionListener).execute(url);
	}

	public Uri getTrueUrl() {
		return this.trueUrl;
	}

	public Uri getPassedUrl() {
		return this.passedUrl;
	}

	public TropesArticleInfo getArticleInfo() {
		return this.articleInfo;
	}

}
