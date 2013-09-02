package eu.prismsw.tropeswrapper;

import android.net.Uri;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**  A wrapper for TvTropes articles that server mainly as an index page */
public class TropesIndex extends TropesArticle {
	private static String DEFAULT_SELECTOR = "li";
	
	public List<TropesLink> tropes;

	public TropesIndex() {
        super();
	}

    @Override
    public void loadArticle(Uri url, TropesArticleSettings settings) throws Exception {
        super.loadArticle(url, settings);
        tropes = parseTropeList(content, DEFAULT_SELECTOR);
    }

    @Override
    public void loadArticle(String html, Uri articleUrl, TropesArticleSettings settings, TropesArticleRessources ressources) throws Exception {
        super.loadArticle(html, articleUrl, settings, ressources);
        tropes = parseTropeList(content, DEFAULT_SELECTOR);
    }
	/** Finds the items matching the selector and returns the attributes of a link in those items */
	private List<TropesLink> parseTropeList(Element content, String selector) throws TropesArticleParseException {
		try {
			ArrayList<TropesLink> tropes = new ArrayList<TropesLink>();
			
			Elements items = content.select(selector);
			
			for(Element item : items) {
				Element a = item.getElementsByTag("a").first();
				if(a != null) {
					String title = a.text();
					String url = a.attr("href").toString();
					tropes.add(new TropesLink(title, Uri.parse(url)));
				}
			}
			
			return tropes;
		}
		catch(Exception e) {
			throw new TropesArticleParseException("parseTropeList");
		}
	}
}
