package eu.prismsw.lampshade;

import android.app.Application;
import com.koushikdutta.async.future.Future;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import eu.prismsw.tropeswrapper.TropesArticle;


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
        loadMainJS();
	}

    private void loadMainJS() {
        Future<String> js = Ion.with(this, TropesArticle.MAIN_JS_URL).asString();
        js.setCallback(new FutureCallback<String>() {
            @Override
            public void onCompleted(Exception e, String s) {
                if (e != null)
                    return;
                mainJS = s;
            }
        });
    }

    public String getMainJS() {
        if(mainJS == "")
            loadMainJS();
        return mainJS;
    }
}
