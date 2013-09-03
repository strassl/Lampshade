package eu.prismsw.tropeswrapper;

import android.net.Uri;
import android.util.Log;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/** Wrapper for a TvTropes article */
public class TropesArticle {
	public final static Integer TIMEOUT = 0;
	public final static String MAIN_JS_URL = "http://static.tvtropes.org/main.js";
	
	public Uri url = null;
	public String title;
	public Element content;
	public List<TropesLink> subpages;
	
	public TropesArticleSettings settings;
	
	public TropesArticle() {
    }


	public void parseArticle(Document doc, TropesArticleSettings settings) throws Exception {
		String mainJS = loadTextFile(Uri.parse(MAIN_JS_URL));
		TropesArticleResources res = new TropesArticleResources(mainJS);
		parseArticle(doc, settings, res);
	}
	
	/** Extracts the bits of information from the article and changes the style  **/
	public void parseArticle(Document doc, TropesArticleSettings settings, TropesArticleResources ressources) throws Exception {
		this.settings = settings;
		this.title = getTitle(doc);
		this.content = getContent(doc);
		this.subpages = getSubpages(doc);
		
		inlineJS(this.content, ressources.mainJS);
        inlineJS(content, ressources.noteJS);
		
		manipulateStyle(this.content, settings);
	}

    //TODO Needs a cleaner and more general solution

    public void loadArticle(Uri url) throws Exception {
        loadArticle(url, new TropesArticleSettings());
    }

    public void loadArticle(Uri url, TropesArticleSettings settings) throws Exception {
        try {
            Document doc = loadPage(url);
            parseArticle(doc, settings);
        }
        catch (Exception e) {
            try {
                Document doc = loadProtectedPage(url);
                parseArticle(doc, settings);
            }
            catch (Exception ex) {
                throw e;
            }
        }
    }

    public void loadArticle(String html, Uri articleUrl, TropesArticleSettings settings, TropesArticleResources ressources) throws Exception{
        Document doc = loadPage(html, articleUrl);
        parseArticle(doc, settings, ressources);
    }
	
	/** Primary means of loading the article **/
	private Document loadPage(Uri url) throws IOException {
		Response resp = Jsoup.connect(url.toString()).timeout(TIMEOUT).execute();
		// We can only set this here due to possible redirects
		this.url = Uri.parse(resp.url().toString());
		Document doc = resp.parse();

		return doc;
	}

    /** Loads an article even if the weird cookie protection is activated
     * Has an impact on performance, only use if necessary **/
    private Document loadProtectedPage(Uri url) throws IOException {
        Document noJS = Jsoup.connect(url.toString()).timeout(TIMEOUT).execute().parse();

        String cookieScript = noJS.head().getElementsByTag("script").first().html();
        cookieScript = cookieScript.substring(cookieScript.lastIndexOf('}'));

        Integer openParen = cookieScript.indexOf('(');
        Integer closeParen = cookieScript.indexOf(')');
        String cookieInfo = cookieScript.substring(openParen + 1, closeParen).replaceAll("'", "").replaceAll(" ", "");

        String[] parts = cookieInfo.split(",");
        String cName = parts[0];
        String cIP = parts[1];

        Log.i("CookieName", cName);
        Log.i("CookieIP", cIP);

        Response resp = Jsoup.connect(url.toString()).timeout(TIMEOUT).cookie(cName, cIP).execute();
        // We can only set this here due to possible redirects
        this.url = Uri.parse(resp.url().toString());
        Document doc = resp.parse();

        return doc;
    }
	
	/** Return the Jsoup document for the provided html */
	protected Document loadPage(String html, Uri url) {
		// We still need to have all the information about the article, thus the url has to be provided
		this.url = url;
		Document doc = Jsoup.parse(html);
		return doc;
	}
	
	/** Loads a simple text file (e.g. JavaScript file) */
	protected String loadTextFile(Uri url) throws IOException {
		Response resp = Jsoup.connect(url.toString()).timeout(TIMEOUT).ignoreContentType(true).execute();
		String body = resp.body();
		
		return body;
	}
	
	/** Extracts the article's title from the document */
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
	
	/** Extracts the article's content from the document */
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
	
	/** Performs all the necessary actions to make the page look pretty */
	protected void manipulateStyle(Element content, TropesArticleSettings settings) throws TropesArticleParseException {
		ArrayList<String> selectors = new ArrayList<String>();
		
		selectors.addAll(createBackgroundStyle(settings.backgroundColor));
		selectors.addAll(createTextStyle(settings.textColor, settings.fontSize));
		selectors.addAll(createLinkStyle(settings.linkColor));
		selectors.addAll(createSpoilerStyle(settings.linkColor, settings.spoilerColor));
		selectors.addAll(createFolderStyle(settings.linkColor, settings.textColor));
		
		insertStylesheet(content, selectors);
		
		ArrayList<String> functions = new ArrayList<String>();
		functions.addAll(createSpoilerScript());
		insertScript(content, functions);
		
		prepareSpoilers(content.getElementsByClass("spoiler"), settings.toggleSpoilerOnHover);
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
		
		element.prepend(wrapStyleTag(style));
	}
	
	protected String wrapStyleTag(String style) {
		String styleTag = "<style type=\"text/css\">" + style + "</style>";
		return styleTag;
	}
	
	/** Combines a list of functions and inserts them before the element */
	protected void insertScript(Element element, List<String> functions) {
		String script = "";
		
		for(String function : functions) {
			script += function;
		}
		
		element.prepend(wrapScriptTag(script));
	}
	
	protected String wrapScriptTag(String script) {
		String scriptTag = "<script type=\"text/javascript\">" + script + "</script>";
		return scriptTag;
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
	
	protected List<String> createTextStyle(String textColor, String fontSize) {
		ArrayList<String> selectors = new ArrayList<String>();
		selectors.add("body { color:" + textColor + ";" + "font-size:" + fontSize + ";" + "}");
		
		return selectors;
	}
	
	/** Creates a stylesheet that changes the colour of links */
	protected List<String> createLinkStyle(String linkColor) {
		ArrayList<String> selectors = new ArrayList<String>();
		selectors.add("a { color:" + linkColor + ";" + " }");
		
		return selectors;
	}
	
	protected List<String> createSpoilerScript() {
		ArrayList<String> functions = new ArrayList<String>();
		
		String makeLinksClickable = "function makeLinksClickable(element) { var links = element.getElementsByTagName('a'); for(i = 0; i < links.length; i++) { links[i].onclick = function() { return true; }; } }";
		functions.add(makeLinksClickable);
		String makeLinksUnclickable = "function makeLinksUnclickable(element) { var links = element.getElementsByTagName('a'); for(i = 0; i < links.length; i++) { links[i].onclick = function() { return false; }; } }";
		functions.add(makeLinksUnclickable);
		String toggleSpoiler = "function toggleSpoiler(element) { if(element.className.indexOf('visible') != -1) { element.className = \"spoiler\"; makeLinksUnclickable(element); } else { element.className=\"spoiler visible\"; makeLinksClickable(element); } }";
		functions.add(toggleSpoiler);
		
		return functions;
	}
	
	protected void prepareSpoilers(Elements spoilers, Boolean toggleOnHover) {
		for(Element spoiler : spoilers) {
			if(toggleOnHover) {
				spoiler.attr("onmouseover", "toggleSpoiler(this);");
				spoiler.attr("onmouseout", "toggleSpoiler(this);");
			}
			else {
				spoiler.attr("onclick", "toggleSpoiler(this);");
			}
			
			Elements links = spoiler.getElementsByTag("a");
			
			for(Element link : links) {
				link.attr("onclick", "return false;");
			}
		}
	}
	
	/** Prettier folders */
	protected List<String> createFolderStyle(String linkColor, String lineColor) {
		ArrayList<String> selectors = new ArrayList<String>();
		selectors.add(".folderlabel { " + "color:" + linkColor + ";" + "width:100%;" + "height:1.5em;" + "margin-top:2em;" + "border-bottom:1px solid " + lineColor + ";" + "}");
		selectors.add(".folderlabelopen { color:" + linkColor + ";" + "font-weight:bold" + " }");
		
		return selectors;
	}
	
	/** Inlines a preloaded JavaScript file */
	protected void inlineJS(Element content, String script) {
		String scriptTag = wrapScriptTag(script);
		content.prepend(scriptTag);
	}
	
	/** Modifies the hover state of .spoiler elements */
	protected List<String> createSpoilerStyle(String linkColor, String spoilerColor) {
		// The hover style is triggered on touch and thus a viable workaround for onClick
		ArrayList<String> selectors = new ArrayList<String>();
		selectors.add(".spoiler { background-color:" + spoilerColor + ";" + "color:" + spoilerColor + "; }");
		selectors.add(".spoiler a { color:" + spoilerColor + "; }");
		selectors.add(".visible { background-color:transparent; }");
		selectors.add(".visible a { color:" + linkColor + "; }");
		
		return selectors;
	}
}
