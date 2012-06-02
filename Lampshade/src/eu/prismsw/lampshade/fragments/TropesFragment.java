package eu.prismsw.lampshade.fragments;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockFragment;

import eu.prismsw.lampshade.R;
import eu.prismsw.lampshade.TropesApplication;
import eu.prismsw.lampshade.listeners.OnInteractionListener;
import eu.prismsw.lampshade.listeners.OnLoadListener;
import eu.prismsw.lampshade.tasks.LoadTropesTask;
import eu.prismsw.tropeswrapper.TropesArticleInfo;

/** Contains common functionality for Fragments that show a TvTropes article. This Fragment is not supposed to be used, only its subclasses **/
public class TropesFragment extends SherlockFragment {
	public static String PASSED_URL = "PASSED_URL";
	public static String TRUE_URL = "TRUE_URL";
	
	TropesApplication application;
	OnLoadListener loadListener;
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
		// We need to save the true url, so we end up on the same page when the article is restored
		outState.putParcelable(PASSED_URL, this.passedUrl);
		outState.putParcelable(TRUE_URL, this.trueUrl);
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		this.application = (TropesApplication) activity.getApplication();
		
		this.loadListener = (OnLoadListener) activity;
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
