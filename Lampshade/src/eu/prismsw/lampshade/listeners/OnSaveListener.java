package eu.prismsw.lampshade.listeners;

import eu.prismsw.lampshade.database.ArticleItem;

/** 
 * Must be implement by any Activity/Fragment/whatever that wishes to use a SaveArticleTask
 **/
public interface OnSaveListener {
	/** Called after the saving succeeded **/
	public void onSaveSuccess(ArticleItem item);
	
	/** Called if an error occurred **/
	public void onSaveError();
}
