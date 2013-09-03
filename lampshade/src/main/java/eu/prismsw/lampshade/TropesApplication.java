package eu.prismsw.lampshade;

import android.app.Application;
import org.apache.commons.io.IOUtils;

import java.io.FileInputStream;


/** Provides cross-activity data and functionality */
public class TropesApplication extends Application {
	public static final String loadAsArticle = "ASARTICLE";

    public static final String MAIN_JS_FILE = "main.js";

	public static final String remoteUrl = "http://lampshade.prismsw.eu/";
	public static final String versionUrl = "http://lampshade.prismsw.eu/version.xml";
	public static final String helpUrl = "http://lampshade.prismsw.eu/help.html";
	
	public static final Integer maxRecentArticles = 15;

    private String mainJS = "";

	@Override
	public void onCreate() {
        readMainJS();
	}

    private void readMainJS() {
        try {
            FileInputStream fis = openFileInput(MAIN_JS_FILE);
            mainJS = IOUtils.toString(fis);
        }
        catch (Exception e) {
            e.printStackTrace();
            mainJS = "";
        }
    }

    public Boolean resourcesExist() {
        if(mainJS == "")
            return false;
        return true;
    }

    public String getMainJS() {
        if(mainJS == "")
            readMainJS();
        return mainJS;
    }
}
