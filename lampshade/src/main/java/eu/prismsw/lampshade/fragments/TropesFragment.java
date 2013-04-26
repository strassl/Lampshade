package eu.prismsw.lampshade.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.text.ClipboardManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import eu.prismsw.lampshade.R;
import eu.prismsw.lampshade.TropesApplication;
import eu.prismsw.lampshade.database.ArticleItem;
import eu.prismsw.lampshade.database.ProviderHelper;
import eu.prismsw.lampshade.listeners.OnInteractionListener;
import eu.prismsw.lampshade.listeners.OnLoadListener;
import eu.prismsw.lampshade.listeners.OnRemoveListener;
import eu.prismsw.lampshade.listeners.OnSaveListener;
import eu.prismsw.lampshade.providers.ArticleProvider;
import eu.prismsw.lampshade.tasks.LoadTropesTask;
import eu.prismsw.tools.ListFunctions;
import eu.prismsw.tools.android.UIFunctions;
import eu.prismsw.tropeswrapper.TropesArticleInfo;

import java.util.List;

/** Contains common functionality for Fragments that show a TvTropes article. This Fragment is not supposed to be used, only its subclasses **/
public class TropesFragment extends SherlockFragment implements OnLoadListener, OnSaveListener, OnRemoveListener {
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
			this.passedUrl = savedInstanceState.getParcelable(PASSED_URL);
			this.trueUrl = savedInstanceState.getParcelable(TRUE_URL);
		}
		else {
			this.passedUrl = getArguments().getParcelable(PASSED_URL);
			this.trueUrl = getArguments().getParcelable(TRUE_URL);
		}

        loadTropes(this.trueUrl);
	}


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Integer id = item.getItemId();

        if (id == R.id.save_article) {
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
        else if(id == R.id.clipboard_article) {
            copyUrlToClipboard(this.trueUrl);
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
            if(ProviderHelper.articleExists(getActivity().getContentResolver(), ArticleProvider.SAVED_URI, trueUrl)) {
                menu.findItem(R.id.save_article).setTitle(R.string.article_remove);
            }
            else {
                menu.findItem(R.id.save_article).setTitle(R.string.article_save);
            }

            if(ProviderHelper.articleExists(getActivity().getContentResolver(), ArticleProvider.FAV_URI, trueUrl)) {
                menu.findItem(R.id.favorite_article).setTitle(R.string.article_unfavorite);
            }
            else {
                menu.findItem(R.id.favorite_article).setTitle(R.string.article_favorite);
            }
        }
    }
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		// We need to save the true url, so we end up on the same page when the article is restored
		outState.putParcelable(PASSED_URL, this.passedUrl);
		outState.putParcelable(TRUE_URL, this.trueUrl);
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
        ProviderHelper.saveArticle(getActivity().getContentResolver(), ArticleProvider.FAV_URI, url);
    }

    private void unfavoriteArticle(Uri url) {
        ProviderHelper.deleteArticle(getActivity().getContentResolver(), ArticleProvider.FAV_URI, url);
    }

    private void saveArticle(Uri url) {
        ProviderHelper.saveArticle(getActivity().getContentResolver(), ArticleProvider.SAVED_URI, url);
    }

    private void removeArticle(Uri url) {
        ProviderHelper.deleteArticle(getActivity().getContentResolver(), ArticleProvider.SAVED_URI, url);
    }

	
	public void loadTropes(Uri url) {
		new LoadTropesTask(this).execute(url);
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
    // Could be useful later though

    @Override
    public void onLoadStart() {
        loadListener.onLoadStart();
    }

    @Override
    public void onLoadFinish(Object result) {
        loadListener.onLoadFinish(result);
    }

    @Override
    public void onLoadError() {
        showLoadingFailedDialog();
    }

    @Override
    public void onRemoveSuccess(ArticleItem item) {
        removeListener.onRemoveSuccess(item);
    }

    @Override
    public void onRemoveError() {
        removeListener.onRemoveError();
    }

    @Override
    public void onSaveSuccess(ArticleItem item) {
        saveListener.onSaveSuccess(item);
    }

    @Override
    public void onSaveError() {
        saveListener.onSaveError();
    }
}
