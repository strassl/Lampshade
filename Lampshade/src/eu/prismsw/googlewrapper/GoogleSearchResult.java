package eu.prismsw.googlewrapper;

import android.net.Uri;

public class GoogleSearchResult {
	public String title;
	public Uri url;
	public String description;
	
	public GoogleSearchResult(String title, Uri url, String description) {
		this.title = title;
		this.url = url;
		this.description = description;
	}
	
	@Override
	public String toString() {
		return this.title;
	}
}
