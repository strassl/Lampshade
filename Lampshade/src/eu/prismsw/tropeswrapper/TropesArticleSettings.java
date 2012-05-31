package eu.prismsw.tropeswrapper;

public class TropesArticleSettings {
	public final static String ICS_BRIGHT_BLUE = "#33B5E5";
	public final static String PURE_BLACK = "#000000";
	public final static String PURE_WHITE = "#FFFFFF";
	public final static String TRANSPARENT = "transparent";
	
	public final static String DEFAULT_FONTSIZE = "12pt";
	
	public String textColor;
	public String fontSize;
	public String linkColor;
	public String spoilerColor;
	public String backgroundColor;
	
	public TropesArticleSettings() {
			setSettings(PURE_BLACK, DEFAULT_FONTSIZE, ICS_BRIGHT_BLUE, PURE_BLACK, TRANSPARENT);
	}
	
	public TropesArticleSettings(Boolean dark) {
		if(dark) {
			setSettings(PURE_WHITE, DEFAULT_FONTSIZE, ICS_BRIGHT_BLUE, PURE_WHITE, PURE_BLACK);
		}
		else {
			setSettings(PURE_BLACK, DEFAULT_FONTSIZE, ICS_BRIGHT_BLUE, PURE_BLACK, TRANSPARENT);
		}
	}
	
	public void setSettings(String textColor, String fontSize, String linkColor, String spoilerColor, String backgroundColor) {
		this.textColor = textColor;
		this.fontSize = fontSize;
		this.linkColor = linkColor;
		this.spoilerColor = spoilerColor;
		this.backgroundColor = backgroundColor;
	}
}
