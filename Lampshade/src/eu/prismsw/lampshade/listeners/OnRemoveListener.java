package eu.prismsw.lampshade.listeners;

import eu.prismsw.lampshade.ArticleItem;

public interface OnRemoveListener {
	public void onRemoveSuccess(ArticleItem item);
	public void onRemoveError();
}
