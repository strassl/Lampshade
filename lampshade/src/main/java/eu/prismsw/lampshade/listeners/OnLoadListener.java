package eu.prismsw.lampshade.listeners;

import eu.prismsw.tropeswrapper.TropesArticle;


/** 
 * Must be implement by any activity that wishes to use a subclass of TropesFragment 
 * Handles any events related to the loading of the article
 **/
public interface OnLoadListener {
	/** Is called before the actual process of loading the page begins **/
	public void onLoadStart();
	
	/**
	 *  Is called if the loading completed successfully 
	 *  The objects contains whatever information should be returned
	 **/
	public void onLoadFinish(Object result);
	
	/** 
	 * Is called if an error occurred 
	 * The exception holds more information 
	 **/
	public void onLoadError(Exception e);
}