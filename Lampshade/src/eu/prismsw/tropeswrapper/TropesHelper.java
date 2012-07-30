package eu.prismsw.tropeswrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.net.Uri;

/** Provides helper functions*/
public class TropesHelper {
	public static final String randomUrl = "http://tvtropes.org/pmwiki/randomitem.php?p=1";
	public static final String baseUrl = "http://tvtropes.org/pmwiki/";
	public static final String mainUrl = "http://tvtropes.org/pmwiki/pmwiki.php/Main/";
	public static final String tropesUrl = "http://tvtropes.org/pmwiki/pmwiki.php/Main/Tropes";
	
	/** Gets the page's title from the url */
	public static String titleFromUrl(Uri url) {
		List<String> segments = url.getPathSegments();
		String title = segments.get(segments.size() - 1);
		
		if(title.indexOf("?") != -1) {
			title = title.substring(0, title.indexOf("?"));
		}
		
		return title;
	}

	/** Gets the page's title from the url (in String form) **/
	public static String titleFromUrl(String url) {
		return titleFromUrl(Uri.parse(url));
	}
	
	public static Boolean isTropesLink(Uri url) {
		String host = url.getHost();
		Pattern pattern = Pattern.compile("([.*]\\.)?tvtropes\\.org");
		Matcher matcher = pattern.matcher(host);
		
		return matcher.matches();
	}
	
	public static Boolean isIndex(String title) {
		//Dirty, but it works and the failure rate is pretty low
		//If it should fail, the user can still view the page as an article
		if(title.matches(".*(Index|index|Tropes|tropes).*")) {
			return true;
		}
    	return false;
	}
	
	/** Converts a list of TropesLinks into List of <a> tags **/
	public static List<String> linkListToHtmlList(List<TropesLink> links) {
		List<String> tags = new ArrayList<String>();
		
		for(TropesLink link : links) {
			tags.add(linkToHtml(link));
		}
		
		return tags;
	}
	
	/** Converts a TropesLink into an <a> tag **/
	public static String linkToHtml(TropesLink link) {
		return createHtmlLink(link.title, link.url.toString());
	}
	
	public static String createHtmlLink(String title, String url) {
		String html = "<a href =\"" + url + "\" >" + title + "</a>";
		return html;
	}
}
