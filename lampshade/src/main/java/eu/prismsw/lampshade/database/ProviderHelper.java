package eu.prismsw.lampshade.database;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import eu.prismsw.tropeswrapper.TropesHelper;

public class ProviderHelper {
    public static void saveArticle(ContentResolver resolver, Uri contentUri, Uri url) {
        ContentValues values = new ContentValues();
        values.put(SavedArticlesHelper.ARTICLES_COLUMN_TITLE, TropesHelper.titleFromUrl(url));
        values.put(SavedArticlesHelper.ARTICLES_COLUMN_URL, url.toString());
        resolver.insert(contentUri, values);
    }

    public static void deleteArticle(ContentResolver resolver, Uri contentUri, Uri url) {
        resolver.delete(contentUri, SavedArticlesHelper.ARTICLES_COLUMN_URL + "=?", new String[]{url.toString()});
    }

    public static void deleteArticle(ContentResolver resolver, Uri contentUri, String id) {
        resolver.delete(contentUri, SavedArticlesHelper.ARTICLES_COLUMN_ID + "=?", new String[]{ id });
    }

    public static boolean articleExists(ContentResolver resolver, Uri contentUrl, Uri url) {
        String[] projection = { SavedArticlesHelper.ARTICLES_COLUMN_ID, SavedArticlesHelper.ARTICLES_COLUMN_TITLE, SavedArticlesHelper.ARTICLES_COLUMN_URL };
        Cursor c = resolver.query(contentUrl, projection, SavedArticlesHelper.ARTICLES_COLUMN_URL + "=?", new String[] {url.toString()}, null);
        if(c.getCount() > 0) {
            return true;
        }
        else {
            return false;
        }
    }

    public static Cursor getArticles(ContentResolver resolver, Uri contentUri) {
        String[] projection = { SavedArticlesHelper.ARTICLES_COLUMN_ID, SavedArticlesHelper.ARTICLES_COLUMN_TITLE, SavedArticlesHelper.ARTICLES_COLUMN_URL };
        Cursor c = resolver.query(contentUri, projection, null, null, null);
        return c;
    }
}
