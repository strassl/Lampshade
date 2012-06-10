package eu.prismsw.lampshade;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;

import android.net.Uri;

public class VersionInformation {
	public String versionString;
	public Integer versionNumber;
	public String information;
	public String changelog;
	
	public VersionInformation(Uri url) {
		try {
			Document doc = getVersionDocument(url);
			this.versionString = getVersionString(doc);
			this.versionNumber = getVersionNumber(doc);
			this.information = getInformation(doc);
			this.changelog = getChangelog(doc);
		} catch (IOException e) {
			this.versionString = "";
			this.versionNumber = -1;
			this.information = "";
			this.changelog = "";
		}
	}
	
	public VersionInformation(String versionString, Integer versionNumber, String information, String changelog) {
		this.versionString = versionString;
		this.versionNumber = versionNumber;
		this.information = information;
		this.changelog = changelog;
	}
	
	private Document getVersionDocument(Uri url) throws IOException {
	    Document doc = Jsoup.connect(url.toString()).parser(Parser.xmlParser()).get();
	    return doc;
	}
	
	private String getVersionString(Document doc) {
	    String versionString = doc.select("versionString").first().text();
	    return versionString;
	}
	
	private Integer getVersionNumber(Document doc) {
	    String versionString = doc.select("versionNumber").first().text();
	    Integer versionNumber = Integer.parseInt(versionString);
	    return versionNumber;
	}
	
	private String getInformation(Document doc) {
	    String information = doc.select("information").first().text();
	    return information;
	}
	
	private String getChangelog(Document doc) {
	    String changelog = doc.select("changelog").first().text();
	    return changelog;
	}
}
