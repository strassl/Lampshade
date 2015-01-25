package eu.prismsw.lampshade.fragments;

import android.app.*;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.ClipboardManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.*;
import android.widget.RelativeLayout;
import android.widget.ShareActionProvider;
import eu.prismsw.lampshade.BaseActivity;
import eu.prismsw.lampshade.R;
import eu.prismsw.lampshade.TropesApplication;
import eu.prismsw.lampshade.database.ProviderHelper;
import eu.prismsw.lampshade.listeners.OnInteractionListener;
import eu.prismsw.lampshade.listeners.OnLoadListener;
import eu.prismsw.lampshade.listeners.OnRemoveListener;
import eu.prismsw.lampshade.listeners.OnSaveListener;
import eu.prismsw.lampshade.providers.ArticleProvider;
import eu.prismsw.tools.ListFunctions;
import eu.prismsw.tools.android.UIFunctions;
import eu.prismsw.tropeswrapper.TropesArticle;
import eu.prismsw.tropeswrapper.TropesArticleInfo;
import eu.prismsw.tropeswrapper.TropesArticleSettings;
import eu.prismsw.tropeswrapper.TropesHelper;

import java.util.List;

/** Contains common functionality for Fragments that show a TvTropes article. This Fragment is not supposed to be used, only its subclasses **/
public class TropesFragment extends Fragment implements OnLoadListener, OnSaveListener, OnRemoveListener {
	public static String PASSED_URL = "PASSED_URL";
	public static String TRUE_URL = "TRUE_URL";
	
	TropesApplication application;
	OnLoadListener loadListener;
	OnInteractionListener interactionListener;
    OnSaveListener saveListener;
    OnRemoveListener removeListener;


	TropesArticleInfo articleInfo;
	Uri passedUrl;
	Uri trueUrl;

    public ShareActionProvider shareProvider;
	
	public static TropesFragment newInstance(Uri url) {
		TropesFragment f = new TropesFragment();
		Bundle bundle = new Bundle(2);
		bundle.putParcelable(PASSED_URL, url);
		bundle.putParcelable(TRUE_URL, url);
		f.setArguments(bundle);
		return f;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setHasOptionsMenu(true);
		
		if(savedInstanceState != null) {
			passedUrl = savedInstanceState.getParcelable(PASSED_URL);
			trueUrl = savedInstanceState.getParcelable(TRUE_URL);
		}
		else {
			passedUrl = getArguments().getParcelable(PASSED_URL);
			trueUrl = getArguments().getParcelable(TRUE_URL);
		}

	}

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadTropes(trueUrl);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Integer id = item.getItemId();
        getActivity().invalidateOptionsMenu();

        if(!loadingIsFinished()) {
            return true;
        }
        else if (id == R.id.random_article) {
            ((BaseActivity) getActivity()).loadPage(Uri.parse(TropesHelper.randomUrl));
            return true;
        }
        else if (id == R.id.save_article) {
            if(ProviderHelper.articleExists(getActivity().getContentResolver(), ArticleProvider.SAVED_URI, trueUrl)) {
                removeArticle(trueUrl);
            }
            else {
                saveArticle(trueUrl);
            }
            return true;
        }
        else if (id == R.id.favorite_article) {
            if(ProviderHelper.articleExists(getActivity().getContentResolver(), ArticleProvider.FAV_URI, trueUrl)) {
                unfavoriteArticle(trueUrl);
            }
            else {
                favoriteArticle(trueUrl);
            }
            return true;
        }
        else if (id == R.id.info_article) {
            showInfoDialog();
            return true;
        }
        else if(id == R.id.clipboard_article) {
            copyUrlToClipboard(this.trueUrl);
            return true;
        }
        else if (id == R.id.browser_article) {
            ((BaseActivity) getActivity()).loadWebsite(trueUrl);
            return true;
        }
        else if (id == R.id.subpages_article) {
            showSubpagesDialog();
            return true;
        }
        else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        if(trueUrl != null) {
            // Switch between Remove/Save
            MenuItem saveItem = menu.findItem(R.id.save_article);
            MenuItem favItem = menu.findItem(R.id.favorite_article);
            if(ProviderHelper.articleExists(getActivity().getContentResolver(), ArticleProvider.SAVED_URI, trueUrl)) {
                saveItem.setTitle(R.string.article_remove);
                saveItem.setIcon(attrToResId(R.attr.deleteIcon));
            }
            else {
                saveItem.setTitle(R.string.article_save);
                saveItem.setIcon(attrToResId(R.attr.saveIcon));
            }

            if(ProviderHelper.articleExists(getActivity().getContentResolver(), ArticleProvider.FAV_URI, trueUrl)) {
                favItem.setTitle(R.string.article_unfavorite);
                favItem.setIcon(attrToResId(R.attr.unfavoriteIcon));
            }
            else {
                favItem.setTitle(R.string.article_favorite);
                favItem.setIcon(attrToResId(R.attr.favoriteIcon));
            }
        }
    }

    private int attrToResId(int attr) {
        TypedValue typedValue= new TypedValue();
        getActivity().getTheme().resolveAttribute(attr, typedValue, true);
        return typedValue.resourceId;
    }

    public void setShareIntent(Uri url) {
        if(shareProvider != null) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, url.toString());
            shareProvider.setShareIntent(intent);
        }
        else {
            Log.e("lampshade", "shareProvider is null");
        }
    }


	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		// We need to save the true url, so we end up on the same page when the article is restored
		outState.putParcelable(PASSED_URL,passedUrl);
		outState.putParcelable(TRUE_URL, trueUrl);
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		this.application = (TropesApplication) activity.getApplication();
		
		this.loadListener = (OnLoadListener) activity;
		this.interactionListener = (OnInteractionListener) activity;
        this.saveListener = (OnSaveListener) activity;
        this.removeListener = (OnRemoveListener) activity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup group, Bundle bundle) {
		return inflater.inflate(R.layout.tropes_fragment, group, false);
	}

    private void copyUrlToClipboard(Uri url) {
        // Kinda bugs me, but backward compatibility demands sacrifices
        ClipboardManager clipboard = (ClipboardManager) application.getSystemService(TropesApplication.CLIPBOARD_SERVICE);
        clipboard.setText(url.toString());
        UIFunctions.showToast(getResources().getString(R.string.article_clipboard_copied) + url.toString(), getActivity());
    }

    private void showSubpagesDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        List<String> subpageStringList = ListFunctions.listToStringList(articleInfo.subpages);
        String[] subpageStringArray = subpageStringList.toArray(new String[subpageStringList.size()]);

        builder.setTitle(R.string.article_subpages);
        builder.setItems(subpageStringArray,  new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                interactionListener.onLinkClicked(articleInfo.subpages.get(which).url);
            }
        });
        builder.setCancelable(true);
        builder.setPositiveButton(R.string.dialog_dismiss, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        Dialog dialog = builder.create();
        dialog.show();
    }

    private void showInfoDialog() {
        FragmentManager fm = getActivity().getFragmentManager();
        DialogFragment dialog = createInfoDialog(articleInfo.title, trueUrl, passedUrl);
        dialog.show(fm, "dialog");
    }

    private DialogFragment createInfoDialog(String title, Uri trueUrl, Uri passedUrl) {
        String info = "";
        info += "Title: " + title + "<br /><br />";
        info += "Url: " + trueUrl.toString() + "<br /><br />";
        info += "Passed Url: " + passedUrl.toString();

        AlertDialogFragment f = AlertDialogFragment.newInstance("Info", info);
        return f;
    }


    private void showLoadingFailedDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(R.string.dialog_load_failed_title);
        builder.setMessage(R.string.dialog_load_failed_message);

        builder.setPositiveButton(R.string.dialog_reload, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                loadTropes(passedUrl);
            }
        });

        builder.setNeutralButton("Copy url", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                loadListener.onLoadError();
                copyUrlToClipboard(passedUrl);
            }
        });

        builder.setNegativeButton(R.string.dialog_close, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                loadListener.onLoadError();
                dialog.dismiss();
            }
        });

        Dialog dialog = builder.create();

        dialog.show();
    }

    private void favoriteArticle(Uri url) {
        Uri newUrl = ProviderHelper.saveArticle(getActivity().getContentResolver(), ArticleProvider.FAV_URI, url);
        saveListener.onSaveFinish(newUrl);
    }

    private void unfavoriteArticle(Uri url) {
        int affected = ProviderHelper.deleteArticle(getActivity().getContentResolver(), ArticleProvider.FAV_URI, url);
        removeListener.onRemoveFinish(affected);
    }

    private void saveArticle(Uri url) {
        Uri newUrl = ProviderHelper.saveArticle(getActivity().getContentResolver(), ArticleProvider.SAVED_URI, url);
        saveListener.onSaveFinish(newUrl);
    }

    private void removeArticle(Uri url) {
        int affected = ProviderHelper.deleteArticle(getActivity().getContentResolver(), ArticleProvider.SAVED_URI, url);
        removeListener.onRemoveFinish(affected);
    }

    public boolean loadingIsFinished() {
        if(articleInfo != null) {
            return true;
        }
        else {
            return false;
        }
    }

    public void hideLoadingCircle() {
        RelativeLayout rl = (RelativeLayout) getView().findViewById(R.id.rl_progress_wrapper);
        rl.setVisibility(RelativeLayout.GONE);
    }
	
	public void loadTropes(Uri url) {
	}

	public Uri getTrueUrl() {
		return this.trueUrl;
	}

	public Uri getPassedUrl() {
		return this.passedUrl;
	}

	public TropesArticleInfo getArticleInfo() {
		return this.articleInfo;
	}

    // For the time being we only pass these on to the activity

    @Override
    public void onLoadStart() {
        loadListener.onLoadStart();
    }

    public TropesArticleSettings createDefaultSettings() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(application);
        Integer fontSize = preferences.getInt("preference_font_size", 12);
        String fontSizeStr = fontSize.toString() + "pt";

        TropesArticleSettings articleSettings;
        if(((BaseActivity) getActivity()).isDarkTheme()) {
            articleSettings = new TropesArticleSettings(true);
        }
        else {
            articleSettings = new TropesArticleSettings(false);
        }
        articleSettings.fontSize = fontSizeStr;
        articleSettings.toggleSpoilerOnHover = preferences.getBoolean("preference_spoiler_hover", false);

        return articleSettings;
    }

    @Override
    public void onLoadFinish(Object result) {
        TropesArticle article = (TropesArticle) result;
        articleInfo = new TropesArticleInfo(article.title, article.url, article.subpages);
        trueUrl = articleInfo.url;

        setShareIntent(trueUrl);

        hideLoadingCircle();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        Boolean historyEnabled = preferences.getBoolean("preference_history_enable", true);

        if(historyEnabled) {
            // Add the page to the list of recent articles

            Cursor c = ProviderHelper.getArticles(getActivity().getContentResolver(), ArticleProvider.RECENT_URI);

            // Prevent the list from growing infinitely
            if(c.getCount() > TropesApplication.maxRecentArticles) {
                c.moveToFirst();
                long id = c.getLong(0);
                c.close();
                ProviderHelper.deleteArticle(getActivity().getContentResolver(), ArticleProvider.RECENT_URI, String.valueOf(id));
            }

            ProviderHelper.saveArticle(getActivity().getContentResolver(), ArticleProvider.RECENT_URI, trueUrl);
        }

        loadListener.onLoadFinish(articleInfo);
    }

    @Override
    public void onLoadError() {
        showLoadingFailedDialog();
    }

    @Override
    public void onRemoveFinish(int affected) {
        removeListener.onRemoveFinish(affected);
    }

    @Override
    public void onSaveFinish(Uri url) {
        saveListener.onSaveFinish(url);
    }
}
