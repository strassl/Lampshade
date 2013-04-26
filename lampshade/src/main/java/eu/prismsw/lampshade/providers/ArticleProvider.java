package eu.prismsw.lampshade.providers;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import eu.prismsw.lampshade.database.SavedArticlesHelper;

public class ArticleProvider extends ContentProvider {
    private static final String AUTHORITY = "eu.prismsw.lampshade.providers.articleprovider";
    private static final String ARTICLES_BASE_PATH = "articles";

    public static final int ARTICLES_SAVED = 10;
    public static final int ARTICLES_SAVED_ID = 11;
    public static final int ARTICLES_SAVED_TITLE = 12;

    public static final int ARTICLES_FAV = 20;
    public static final int ARTICLES_FAV_ID = 21;
    public static final int ARTICLES_FAV_TITLE = 22;

    public static final int ARTICLES_RECENT = 30;
    public static final int ARTICLES_RECENT_ID = 31;
    public static final int ARTICLES_RECENT_TITLE = 32;

    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + ARTICLES_BASE_PATH);
    public static final Uri SAVED_URI = CONTENT_URI.buildUpon().appendPath("saved").build();
    public static final Uri FAV_URI = CONTENT_URI.buildUpon().appendPath("favorites").build();
    public static final Uri RECENT_URI = CONTENT_URI.buildUpon().appendPath("recent").build();

    private static UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        matcher.addURI(AUTHORITY, ARTICLES_BASE_PATH + "/saved", ARTICLES_SAVED);
        matcher.addURI(AUTHORITY, ARTICLES_BASE_PATH + "/saved/id/#", ARTICLES_SAVED_ID);
        matcher.addURI(AUTHORITY, ARTICLES_BASE_PATH + "/saved/title/*", ARTICLES_SAVED_TITLE);

        matcher.addURI(AUTHORITY, ARTICLES_BASE_PATH + "/favorites", ARTICLES_FAV);
        matcher.addURI(AUTHORITY, ARTICLES_BASE_PATH + "/favorites/id/#", ARTICLES_FAV_ID);
        matcher.addURI(AUTHORITY, ARTICLES_BASE_PATH + "/favorites/title/*", ARTICLES_FAV_TITLE);

        matcher.addURI(AUTHORITY, ARTICLES_BASE_PATH + "/recent", ARTICLES_RECENT);
        matcher.addURI(AUTHORITY, ARTICLES_BASE_PATH + "/recent/id/#", ARTICLES_RECENT_ID);
        matcher.addURI(AUTHORITY, ARTICLES_BASE_PATH + "/favorites/title/*", ARTICLES_RECENT_TITLE);
    }

    private SavedArticlesHelper helper;

    @Override
    public boolean onCreate() {
        helper = new SavedArticlesHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        int type = matcher.match(uri);

        switch(type) {
            case ARTICLES_SAVED:
                queryBuilder.setTables(SavedArticlesHelper.TABLE_SAVED);
                break;
            case ARTICLES_SAVED_ID:
                queryBuilder.setTables(SavedArticlesHelper.TABLE_SAVED);
                queryBuilder.appendWhere(SavedArticlesHelper.ARTICLES_COLUMN_ID + "=" + uri.getLastPathSegment());
                break;
            case ARTICLES_SAVED_TITLE:
                queryBuilder.setTables(SavedArticlesHelper.TABLE_SAVED);
                queryBuilder.appendWhere(SavedArticlesHelper.ARTICLES_COLUMN_TITLE + "=" + uri.getLastPathSegment());
                break;
            case ARTICLES_FAV:
                queryBuilder.setTables(SavedArticlesHelper.TABLE_FAVORITE);
                break;
            case ARTICLES_FAV_ID:
                queryBuilder.setTables(SavedArticlesHelper.TABLE_FAVORITE);
                queryBuilder.appendWhere(SavedArticlesHelper.ARTICLES_COLUMN_ID + "=" + uri.getLastPathSegment());
                break;
            case ARTICLES_FAV_TITLE:
                queryBuilder.setTables(SavedArticlesHelper.TABLE_FAVORITE);
                queryBuilder.appendWhere(SavedArticlesHelper.ARTICLES_COLUMN_TITLE + "=" + uri.getLastPathSegment());
                break;
            case ARTICLES_RECENT:
                queryBuilder.setTables(SavedArticlesHelper.TABLE_RECENT);
                break;
            case ARTICLES_RECENT_ID:
                queryBuilder.setTables(SavedArticlesHelper.TABLE_RECENT);
                queryBuilder.appendWhere(SavedArticlesHelper.ARTICLES_COLUMN_ID + "=" + uri.getLastPathSegment());
                break;
            case ARTICLES_RECENT_TITLE:
                queryBuilder.setTables(SavedArticlesHelper.TABLE_RECENT);
                queryBuilder.appendWhere(SavedArticlesHelper.ARTICLES_COLUMN_TITLE + "=" + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri.toString());
        }

        Cursor c= queryBuilder.query(helper.getReadableDatabase(), projection, selection, selectionArgs, null, null, sortOrder);
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int type = matcher.match(uri);
        SQLiteDatabase db = helper.getWritableDatabase();
        Uri newUri = null;
        long id = 0;

        switch(type) {
            case ARTICLES_SAVED:
                id = db.insert(SavedArticlesHelper.TABLE_SAVED, null, values);
                SAVED_URI.buildUpon().appendPath(String.valueOf(id)).build();
                break;
            case ARTICLES_FAV:
                id = db.insert(SavedArticlesHelper.TABLE_FAVORITE, null, values);
                FAV_URI.buildUpon().appendPath(String.valueOf(id)).build();
                break;
            case ARTICLES_RECENT:
                id = db.insert(SavedArticlesHelper.TABLE_RECENT, null, values);
                RECENT_URI.buildUpon().appendPath(String.valueOf(id)).build();
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri.toString());
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return newUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int type = matcher.match(uri);
        SQLiteDatabase db = helper.getWritableDatabase();
        int rowsAffected = 0;

        switch(type) {
            case ARTICLES_SAVED:
                rowsAffected = db.delete(SavedArticlesHelper.TABLE_SAVED, selection, selectionArgs);
                break;
            case ARTICLES_SAVED_ID:
                rowsAffected = deleteById(SavedArticlesHelper.TABLE_SAVED, uri, selection, selectionArgs);
                break;
            case ARTICLES_FAV:
                rowsAffected = db.delete(SavedArticlesHelper.TABLE_FAVORITE, selection, selectionArgs);
                break;
            case ARTICLES_FAV_ID:
                rowsAffected = deleteById(SavedArticlesHelper.TABLE_FAVORITE, uri, selection, selectionArgs);
                break;
            case ARTICLES_RECENT:
                rowsAffected = db.delete(SavedArticlesHelper.TABLE_RECENT, selection, selectionArgs);
                break;
            case ARTICLES_RECENT_ID:
                rowsAffected = deleteById(SavedArticlesHelper.TABLE_RECENT, uri, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri.toString());
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return rowsAffected;
    }

    private int deleteById(String table, Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = helper.getWritableDatabase();
        String id = uri.getLastPathSegment();
        int rowsAffected = 0;

        if (TextUtils.isEmpty(selection)) {
            rowsAffected = db.delete(table, SavedArticlesHelper.ARTICLES_COLUMN_ID + "=" + id, null);
        }
        else {
            rowsAffected = db.delete(table, selection + " and " + SavedArticlesHelper.ARTICLES_COLUMN_ID + "=" + id, selectionArgs);
        }
         return rowsAffected;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }
}
