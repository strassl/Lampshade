package eu.prismsw.googlewrapper;

import android.net.Uri;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/** Handles the loading and parsing of a Google search **/
public class GoogleSearch {
	public static final String GOOGLE_BASE_URL="http://google.com/search";
	public static final Integer DEFAULT_PAGES = 1;
	
	private String query;
	private Integer pages;
	private List<GoogleSearchResult> results;
	
	public GoogleSearch(String query, Integer pages) throws IOException {
		this.query = query;
		this.pages = pages;
		this.results = doSearch(query, pages);
	}
	
	public GoogleSearch(String query) throws IOException {
		this(query, DEFAULT_PAGES);
	}
	
	public String getQuery() {
		return this.query;
	}
	
	public void setQuery(String query) {
		this.query = query;
	}
	
	public List<GoogleSearchResult> getResults() {
		return this.results;
	}
	
	public void reloadResults() throws IOException {
		this.results = doSearch(this.query, this.pages);
	}
	
	private List<GoogleSearchResult> doSearch(String query, Integer pages) throws IOException {
		String searchUrl = GOOGLE_BASE_URL + "?q=" + URLEncoder.encode(query, "utf-8");
		
		List<GoogleSearchResult> searchResults = new ArrayList<GoogleSearchResult>();
		
		for(int i = 0; i < pages; i++) {
			// Google pages are numbered (page*10)
			// e.g 0*10=0=page1, 1*10=10=page1
			String pageUrl = searchUrl + "&start=" + Integer.toString(i * 10);
			List<GoogleSearchResult> pageResults = getSearchResults(Uri.parse(pageUrl));
			
			searchResults.addAll(pageResults);
		}
		
		return searchResults;
	}
	
	/** Returns a list of results for the passed url **/
	private List<GoogleSearchResult> getSearchResults(Uri searchUrl) throws IOException {
		Response resp = Jsoup.connect(searchUrl.toString()).execute();
		Document doc = resp.parse();
		return parseSearchResults(doc);
	}
	
	/** Splits the Document into a List of GoogleSearchResult **/
	private List<GoogleSearchResult> parseSearchResults(Document doc) {
		Element rso = doc.getElementById("rso");
		Elements resultItems = rso.getElementsByClass("rc");

		List<GoogleSearchResult> results = new ArrayList<GoogleSearchResult>();
		for(Element vsc : resultItems) {
			results.add(parseSingleResult(vsc));
		}
		
		return results;
	}
	
	/** Organises the information from the vsc(Google's naming scheme) Element **/
	private GoogleSearchResult parseSingleResult(Element e) {
		Element titleElement = e.getElementsByTag("a").first();
		
		String title = titleElement.text();
		Uri url = Uri.parse(titleElement.attr("href"));
		String description = e.getElementsByClass("st").first().text();

		return new GoogleSearchResult(title, url, description);
	}
}
