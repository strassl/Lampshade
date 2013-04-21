package eu.prismsw.lampshade.listeners;

/**
 * Must be implement by any activity that wishes to use a subclass of TropesFragment 
 * Handles any events related to the loading of the article
 **/
public interface OnLoadListener {
	public void onLoadStart();
	
	/**
	 *  Is called if the loading completed successfully 
	 *  The objects contains whatever information should be returned
	 **/
	public void onLoadFinish(Object result);
	
	public void onLoadError();
}