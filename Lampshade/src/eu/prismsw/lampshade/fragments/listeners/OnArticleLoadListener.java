package eu.prismsw.lampshade.fragments.listeners;

import eu.prismsw.tropeswrapper.TropesArticleInfo;

/** 
 * Must be implement by any activity that wishes to use a subclass of TropesFragment 
 * Handles any events related to the loading of the article
 **/
public interface OnArticleLoadListener {
	/** Is called before the actual process of loading the page begins **/
	public void onLoadStart();
	
	/**
	 *  Is called if the loading completed successfully 
	 * The TropesArticleInfo object contains information about the article (title, url, subpages) 
	 **/
	public void onLoadFinish(TropesArticleInfo info);
	
	/** 
	 * Is called if an error occurred 
	 * The exception holds more information 
	 **/
	public void onLoadError(Exception e);
}