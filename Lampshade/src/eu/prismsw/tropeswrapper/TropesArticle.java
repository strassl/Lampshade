package eu.prismsw.tropeswrapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.net.Uri;

/** Wrapper for a TvTropes article */
public class TropesArticle {
	public final static String ICS_BRIGHT_BLUE = "#33B5E5";
	public final static String PURE_BLACK = "#000000";
	public final static String PURE_WHITE = "#FFFFFF";
	public final static String TRANSPARENT = "transparent";
	
	public final static Integer TIMEOUT = 0;
	
	public Uri url = null;
	
	public String title;
	public Element content;
	
	public List<TropesLink> subpages;
	
	public String textColor;
	public String linkColor;
	public String spoilerColor;
	public String backgroundColor;
	
	public TropesArticle(Uri url) throws Exception {
		this(url, PURE_BLACK, ICS_BRIGHT_BLUE, PURE_BLACK, TRANSPARENT);
	}
	
	public TropesArticle(Uri url, String textColor, String linkColor, String spoilerColor, String backgroundColor) throws Exception {
		this.textColor = textColor;
		this.linkColor = linkColor;
		this.spoilerColor = spoilerColor;
		this.backgroundColor = backgroundColor;
		
		Document doc = loadArticle(url);
		this.title = getTitle(doc);
		this.content = getContent(doc);
		this.subpages = getSubpages(doc);
		
		manipulateStyle(this.content, textColor, linkColor, spoilerColor, backgroundColor);
	}
	
	/** Returns the Jsoup document of the url */
	protected Document loadArticle(Uri url) throws IOException {
		Response resp = Jsoup.connect(url.toString()).timeout(TIMEOUT).execute();
		// We can only set this here due to possible redirects
		this.url = Uri.parse(resp.url().toString());
		Document doc = resp.parse();
				
		return doc;
	}
	
	/** Extracts the article's title from the document **/
	protected String getTitle(Document doc) throws TropesArticleParseException{
		try {
			Element wikibody = doc.getElementById("wikibody");
			
			Element title = wikibody.getElementById("wikititle").getElementsByClass("pagetitle").first().getElementsByTag("span").first();
			return title.text();
		}
		catch (Exception e) {
			throw new TropesArticleParseException("getTitle");
		}
	}
	
	/** Extracts the article's content from the document **/
	protected Element getContent(Document doc) throws TropesArticleParseException{
		try {
			Element wikibody = doc.getElementById("wikibody");
			Element content = wikibody.getElementById("wikitext");
			
			return content;
		}
		catch (Exception e) {
			throw new TropesArticleParseException("getContent");
		}
	}
	
	/** Performs all the necessary actions to make the page look pretty **/
	public void manipulateStyle(Element content, String textColor, String linkColor, String spoilerColor, String backgroundColor) throws TropesArticleParseException{
		try {
			addMainJS(content);
			
			ArrayList<String> selectors = new ArrayList<String>();
			
			selectors.addAll(createBackgroundStyle(backgroundColor));
			selectors.addAll(createTextStyle(textColor));
			selectors.addAll(createLinkStyle(linkColor));
			selectors.addAll(createSpoilerStyle(linkColor, spoilerColor));
			selectors.addAll(createFolderStyle(linkColor, textColor));
			
			insertStylesheet(content, selectors);
		}
		catch (Exception e) {
			throw new TropesArticleParseException("manipulateStyle");
		}
	}
	
	/** Extracts the subpages from the document */
	protected List<TropesLink> getSubpages(Document doc) throws TropesArticleParseException{
		try {
			Element wikititle = doc.getElementById("wikititle");
			Element buttons = wikititle.getElementsByClass("namespacebuttons").first();
			Elements links = buttons.getElementsByTag("a");
			
			ArrayList<TropesLink> subpages =  new ArrayList<TropesLink>();
			
			for(Element link : links) {
				String title = link.text().trim();
				
				if(title.trim().equals("")) {
					title = link.attr("title").trim();
				}
				
				if(title.trim().equals("")) {
					Element img = link.getElementsByTag("img").first();
					title = img.attr("title");
				}
				String url = link.attr("href");
				subpages.add(new TropesLink(title, Uri.parse(url)));
			}
			
			return subpages;
		}
		catch(Exception e) {
			throw new TropesArticleParseException("parseSubpages");
		}
	}
	
	
	/** Combines a List of css selectors into a stylesheet and inserts it into the page */
	protected void insertStylesheet(Element element, List<String> selectors) {
		String style = "";
		for(String selector : selectors) {
			style += selector;
		}
		
		String style_tag = "<style type=\"text/css\">" + style + "</style>";
		element.prepend(style_tag);
	}
	
	/** Combines alist of functions and inserts them before the element */
	protected void insertScript(Element element, List<String> functions) {
		String script = "";
		
		for(String function : functions) {
			script += function;
		}
		
		String scriptTag = "<script type=\"text/javascript\">" + script + "</script>";
		
		element.prepend(scriptTag);
	}
	
	/** Inserts an external JavaScript file into the page */
	protected void insertExternalScript(Element element, Uri url) {
		String scriptTag = "<script type=\"text/javascript\" src=\"" + url.toString() + "\">" + "</script>";
		element.prepend(scriptTag);
	}
	
	/** Inserts an external stylesheet into the page */
	protected void insertExternalStylesheet(Element element, Uri url) {
		String styleTag = "<link rel=\"stylesheet\" type=\"text/css\" href=\"" + url.toString() + "\" />";
		element.prepend(styleTag);
	}
	
	protected List<String> createBackgroundStyle(String backgroundColor) {
		ArrayList<String> selectors = new ArrayList<String>();
		selectors.add("body { background-color:" + backgroundColor + ";" + " }");
		
		return selectors;
	}
	
	protected List<String> createTextStyle(String textColor) {
		ArrayList<String> selectors = new ArrayList<String>();
		selectors.add("body { color:" + textColor + ";" + " }");
		
		return selectors;
	}
	
	/** Inserts a stylesheet that changes the colour of links */
	protected List<String> createLinkStyle(String linkColor) {
		ArrayList<String> selectors = new ArrayList<String>();
		selectors.add("a { color:" + linkColor + ";" + " }");
		
		return selectors;
	}
	
	/** Prettier folders */
	protected List<String> createFolderStyle(String linkColor, String lineColor) {
		ArrayList<String> selectors = new ArrayList<String>();
		selectors.add(".folderlabel { " + "color:" + linkColor + ";" + "width:100%;" + "height:1.5em;" + "margin-top:2em;" + "border-bottom:1px solid " + lineColor + ";" + "}");
		selectors.add(".folderlabelopen { color:" + linkColor + ";" + "font-weight:bold" + " }");
		
		return selectors;
	}
	
	/** Inserts the main.js file from tvtropes.org */
	protected void addMainJS(Element content) {
		Uri mainJSUrl = Uri.parse("http://static.tvtropes.org/main.js");
		insertExternalScript(content, mainJSUrl);
	}
	
	/** Modifies the hover state of .spoiler elements */
	protected List<String> createSpoilerStyle(String linkColor, String spoilerColor) {
		// The hover style is triggered on touch and thus a viable workaround for onClick
		ArrayList<String> selectors = new ArrayList<String>();
		selectors.add(".spoiler { background-color:" + spoilerColor + ";" + "color:" + spoilerColor + "; }");
		selectors.add(".spoiler a { color:" + spoilerColor + "; }");
		selectors.add(".spoiler:hover { background-color:transparent; }");
		selectors.add(".spoiler:hover a { color:" + linkColor + "; }");
		
		return selectors;
	}
}
