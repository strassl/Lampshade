package eu.prismsw.googlewrapper;

import android.net.Uri;

/** Contains the information about a single search result (title, description, url) */
public class GoogleSearchResult {
	public String title;
	public Uri url;
	public String description;
	
	public GoogleSearchResult(String title, Uri url, String description) {
		this.title = title;
		this.url = url;
		this.description = description;
	}
	
	// Needed for the ListView
	@Override
	public String toString() {
		return this.title;
	}
}
