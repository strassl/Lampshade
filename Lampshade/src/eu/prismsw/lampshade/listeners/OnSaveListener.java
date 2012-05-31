package eu.prismsw.lampshade.listeners;

import eu.prismsw.lampshade.ArticleItem;

public interface OnSaveListener {
	public void onSaveSuccess(ArticleItem item);
	public void onSaveError();
}
