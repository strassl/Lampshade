package eu.prismsw.lampshade.listeners;

/**
 * Must be implement by any Activity/Fragment/whatever that wishes to use a RemoveArticleTask
 **/
public interface OnRemoveListener {
	public void onRemoveFinish(int rowsAffected);
}
