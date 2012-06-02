package eu.prismsw.lampshade.listeners;

import eu.prismsw.lampshade.ArticleItem;

/** 
 * Must be implement by any Activity/Fragment/whatever that wishes to use a RemoveArticleTask
 **/
public interface OnRemoveListener {
	/** Called after the removal succeeded **/
	public void onRemoveSuccess(ArticleItem item);
	
	/** Called if an error occurred **/
	public void onRemoveError();
}
