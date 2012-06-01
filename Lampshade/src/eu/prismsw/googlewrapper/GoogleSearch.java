package eu.prismsw.googlewrapper;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.Connection.Response;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.net.Uri;

public class GoogleSearch {
	public static final String googleBaseUrl="http://google.com/search";
	
	public String query;
	
	public List<GoogleSearchResult> results;
	
	public GoogleSearch(String query, Integer pages) throws IOException {
		this.query = query;
		this.results = new ArrayList<GoogleSearchResult>();
		
		String searchUrl = googleBaseUrl + "?q=" + URLEncoder.encode(query, "utf-8");
		
		for(int i = 0; i < pages; i++) {
			String pageUrl = searchUrl + "&start=" + Integer.toString(i * 10);
			List<GoogleSearchResult> pageResults = getSearchResults(Uri.parse(pageUrl));
			
			this.results.addAll(pageResults);
		}
	}
	
	public List<GoogleSearchResult> getSearchResults(Uri searchUrl) throws IOException {
		Document doc = loadSearch(searchUrl);
		return parseSearchResults(doc);
	}
	
	public Document loadSearch(Uri url) throws IOException {
		Response resp = Jsoup.connect(url.toString()).execute();
		Document doc = resp.parse();
		return doc;
	}
	
	public List<GoogleSearchResult> parseSearchResults(Document doc) {
		Element rso = doc.getElementById("rso");
		Elements resultItems = rso.getElementsByClass("vsc");
		
		List<GoogleSearchResult> results = new ArrayList<GoogleSearchResult>();
		for(Element vsc : resultItems) {
			results.add(parseSingleResult(vsc));
		}
		
		return results;
	}
	
	public GoogleSearchResult parseSingleResult(Element vsc) {
		Element titleElement = vsc.getElementsByTag("a").first();
		
		String title = titleElement.text();
		Uri url = Uri.parse(titleElement.attr("href"));
		String description = vsc.getElementsByClass("st").first().text();
		
		return new GoogleSearchResult(title, url, description);
	}
}
