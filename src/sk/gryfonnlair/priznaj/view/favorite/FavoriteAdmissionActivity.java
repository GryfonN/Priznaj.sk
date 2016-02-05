package sk.gryfonnlair.priznaj.view.favorite;

import java.util.ArrayList;
import java.util.List;

import sk.gryfonnlair.priznaj.PriznajApplication;
import sk.gryfonnlair.priznaj.R;
import sk.gryfonnlair.priznaj.control.activities.FavoriteDroid;
import sk.gryfonnlair.priznaj.control.util.ApiVersionUtil;
import sk.gryfonnlair.priznaj.control.util.FontUtil;
import sk.gryfonnlair.priznaj.control.util.ScreenUtil;
import sk.gryfonnlair.priznaj.model.specific.FavoriteAdmission;
import sk.gryfonnlair.priznaj.view.favorite.tools.FavoriteElement;
import sk.gryfonnlair.priznaj.view.favorite.tools.FavoriteElementAdapter;
import sk.gryfonnlair.priznaj.view.favorite.tools.SwipeDismissList;
import sk.gryfonnlair.priznaj.view.ui.PProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


/**
 * Favorite aktivita s swipelistviewom v tools.
 * 
 * @author gryfonn
 * 
 */
public class FavoriteAdmissionActivity extends ActionBarActivity implements AbsListView.OnScrollListener {

	static final String KEY_LIMITX = "FavoriteAdmissionActivity_limitX";
	static final String KEY_SCROLL_POSITION = "FavoriteAdmissionActivity_scrollY";
	static final String KEY_FOOTER_VISIBILITY = "FavoriteAdmissionActivity_footerVisibility";
	static final String KEY_NO_ANOTHER_FAS = "FavoriteAdmissionActivity_noAnotherFAs";
	/**
	 * pomocna pre getFirstVisiblePosition() a nasledne setnutie az vo vlakne po
	 * nacitani priznani
	 */
	int scrollPosition = 0;
	/**
	 * Rozhoduje o spusteni bottomFetchTasku, je zbytocne selectovat ak uz raz
	 * mi prisiel null a nic v db nieje
	 */
	boolean noAnotherFAs;
	/**
	 * Custom Adapter listViewu
	 */
	FavoriteElementAdapter mAdapter;
	/**
	 * 3rd party library trieda skopcena upravena, modifikuje listview
	 */
	SwipeDismissList mSwipeList;
	/**
	 * Vlakno ktore spusta get z db ak som nascroloval bottom, musim byt hore
	 * aby som checkoval ready state
	 */
	FavoriteAsyncTask favoriteAsyncTask;
	/**
	 * ListView ktory potrebujem pre getnutie scrollpozicie,
	 * getFirstVisiblePosition()
	 */
	ListView listView;
	/**
	 * pager v rohu listviewu
	 */
	TextView pager;
	/**
	 * riesim jeho viditelnost az ked bottom narazim
	 */
	View footer;

	ArrayList<FavoriteAdmission> fasToDelete = new ArrayList<FavoriteAdmission>(3);

	private ActionBar actionBar;

	PProgressDialog dialog;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		ScreenUtil.setScreenOrientation(this);
		setContentView(R.layout.activity_favorite);
		listView = (ListView) findViewById(R.id.activity_favorite_listview);
		pager = (TextView) findViewById(R.id.activity_favorite_pager);
		pager.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(final View v) {
				Toast.makeText(v.getContext(), pager.getText() + ". strana", Toast.LENGTH_SHORT).show();
			}
		});

		dialog = new PProgressDialog(this);
		mAdapter = new FavoriteElementAdapter(this, 0, new ArrayList<FavoriteElement>());
		footer = LayoutInflater.from(this).inflate(R.layout.activity_favorite_footer_layout, null, false);
		FontUtil.setOswaldRegularFont((TextView) footer.findViewById(R.id.activity_favorite_footer_text));
		listView.addFooterView(footer);
		listView.setAdapter(mAdapter);
		mSwipeList = new SwipeDismissList(
				listView,
				new SwipeDismissList.OnDismissCallback() {
					@Override
					public SwipeDismissList.Undoable onDismiss(
							final AbsListView listView, final int position) {

						final FavoriteElement item = mAdapter.getItem(position);
						mAdapter.remove(item);
						mAdapter.notifyDataSetChanged();
						return new SwipeDismissList.Undoable() {
							@Override
							public String getTitle() {
								return item + " deleted";
							}

							@Override
							public void undo() {
								mAdapter.insert(item, position);
								mAdapter.notifyDataSetChanged();
							}

							@Override
							public void discard() {
								if (item.favoriteAdmission != null) {
									fasToDelete.add(item.favoriteAdmission);
								}
							}
						};

					}
				},
				new SwipeDismissList.OnDiscardCallback() {

					@Override
					public void onDiscrad() {
						if (!fasToDelete.isEmpty()) {
							FavoriteDroid.deleteFavoriteAdmissions(fasToDelete);
							fasToDelete.clear();
						}
					}
				},
				this);

		mSwipeList.setUndoString(getString(R.string.activity_favorite_admission_deleted));
		mSwipeList.setUndoMultipleString(getString(R.string.activity_favorite_admissions_deleted));

		//restoreFromBundle
		FavoriteDroid.limitX = savedInstanceState == null ? 0 : savedInstanceState.getInt(KEY_LIMITX, 0);
		scrollPosition = savedInstanceState == null ? 0 : savedInstanceState.getInt(KEY_SCROLL_POSITION, 0);
		noAnotherFAs = savedInstanceState == null ? false : savedInstanceState.getBoolean(KEY_NO_ANOTHER_FAS, false);
		footer.setVisibility(savedInstanceState == null ? View.GONE : savedInstanceState.getInt(KEY_FOOTER_VISIBILITY, View.GONE));
		if (PriznajApplication.D) {
			Log.d(PriznajApplication.DEBUG_TAG, "FavoriteAdmissionActivity: OnCreate hodnota limitX="
					+ FavoriteDroid.limitX + ", scrollPosition=" + scrollPosition + ",footerVisibility=" + footer.getVisibility());
		}
	}

	@Override
	protected void onResume() {
		if (PriznajApplication.D) {
			Log.d(PriznajApplication.DEBUG_TAG, "FavoriteAdmissionActivity: OnResume mAdapter size: " + mAdapter.getCount());
		}
		/*
		 * GRYFONN PERFORMANCE HINT
		 * Ak ma portrait mobil a dam len lock tak sa adapter zachova a nieje
		 * treba ho cleanovat a ani vlakno spustat
		 */
		if (mAdapter.isEmpty()) {
			favoriteAsyncTask = new FavoriteAsyncTask(true, this);
			if (ApiVersionUtil.hasHoneycomb()) {
				favoriteAsyncTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, new Void[] {});
			}
			else {
				favoriteAsyncTask.execute();
			}
		}
		super.onResume();
	}

	@Override
	protected void onSaveInstanceState(final Bundle outState) {
		outState.putInt(KEY_LIMITX, FavoriteDroid.limitX);
		outState.putInt(KEY_SCROLL_POSITION, listView.getFirstVisiblePosition());
		outState.putInt(KEY_FOOTER_VISIBILITY, footer.getVisibility());
		outState.putBoolean(KEY_NO_ANOTHER_FAS, noAnotherFAs);
		if (PriznajApplication.D) {
			Log.d(PriznajApplication.DEBUG_TAG, "FavoriteAdmissionActivity: onSaveInstanceState ukladam limitX=" + FavoriteDroid.limitX +
					", scrollPosition=" + listView.getFirstVisiblePosition() + ",footerVisibility=" + footer.getVisibility() +
					", noAnotherFAs=" + noAnotherFAs);
		}
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onPause() {
		mSwipeList.discardAll();
		if (PriznajApplication.D) {
			Log.d(PriznajApplication.DEBUG_TAG, "FavoriteAdmissionActivity: onPause mSwipeList.discardAll()");
		}
		super.onPause();
	}

	@Override
	protected void onStop() {
		/*
		 * Musi byt, vid readmy swipelistu
		 */
		if (favoriteAsyncTask != null) {
			favoriteAsyncTask.disconnectActivity();
			favoriteAsyncTask.cancel(true);
		}
		if (dialog != null) {
			dialog.cancel();
		}
		super.onStop();
	}

	@Override
	public void onScroll(final AbsListView view, final int firstVisibleItem, final int visibleItemCount, final int totalItemCount) {
		//<GRYFONN_CODE
		/**
		 * cela metoda bola povodne prazdna a celkovo onScroll interface sa
		 * riesil v swipeDismisliste triede
		 */
		if (pager != null) {
			//vypocet pagera, vsetky minus pocet reklam delene 10, lebo to je strana na servery, HINT, cca vypocet lebo reklamy nie vzdy su
			final int pageNumber = (firstVisibleItem - (totalItemCount / PriznajApplication.LIST_VIEW_AD_OCCURRENCE)) / 10 + 1;
			pager.setText(Integer.toString(pageNumber));
			//vypocet viditelnosti pagera
			if (firstVisibleItem + visibleItemCount + 1 > totalItemCount) {
				pager.setVisibility(View.GONE);
			} else {
				pager.setVisibility(View.VISIBLE);
			}
		}

		//nech sa neaktivuje search ak je prazdny list
		if (mAdapter.isEmpty()) {
			return;
		}
		final int lastItem = firstVisibleItem + visibleItemCount + PriznajApplication.LIST_VIEW_LOADING_BOTTOM_BORDER;
		if (lastItem >= totalItemCount && favoriteAsyncTask != null && favoriteAsyncTask.ready && !noAnotherFAs) {
			favoriteAsyncTask = new FavoriteAsyncTask(false, FavoriteAdmissionActivity.this);
			if (ApiVersionUtil.hasHoneycomb()) {
				favoriteAsyncTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, new Void[] {});
			}
			else {
				favoriteAsyncTask.execute();
			}
		}
		//>
	}

	@Override
	public void onScrollStateChanged(final AbsListView view, final int scrollState) {
		mSwipeList.setEnabled(scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE);
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				finish();
				break;

			default:
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	static class FavoriteAsyncTask extends AsyncTask<Void, Void, List<FavoriteElement>> {
		/**
		 * indikator isRunning, aby som nespustil na scrolle dva krat vlakno
		 * pokial jedno ide
		 */
		volatile boolean ready = false;
		private final boolean init;
		private FavoriteAdmissionActivity activity;

		public FavoriteAsyncTask(final boolean init, final FavoriteAdmissionActivity activity) {
			this.init = init;
			this.activity = activity;
		}

		public void disconnectActivity() {
			activity = null;
		}

		@Override
		protected void onPreExecute() {
			ready = false;
			if (init) {
				activity.dialog.show();
			}
			super.onPreExecute();
		}

		@Override
		protected List<FavoriteElement> doInBackground(final Void... params) {
			if (PriznajApplication.D) {
				Log.d(PriznajApplication.DEBUG_TAG, "FavoriteAdmissionActivity> FavoriteAsyncTask START Init:" + init +
						", limitX v DROIDe:" + FavoriteDroid.limitX);
			}
			return FavoriteDroid.getAdmissions(init);
		}

		@Override
		protected void onPostExecute(final List<FavoriteElement> result) {
			if (result == null || isCancelled()) {
				activity.noAnotherFAs = true;
				activity.footer.setVisibility(View.VISIBLE);
				if (PriznajApplication.D) {
					Log.d(PriznajApplication.DEBUG_TAG, "FavoriteAdmissionActivity> FavoriteAsyncTask KONIEC Init:" + init +
							", RESULT NULL, noAnotherFAs=" + activity.noAnotherFAs);
				}
				if (init) {
					((TextView) activity.footer.findViewById(R.id.activity_favorite_footer_text)).setText(R.string.activity_favorite_message_no_fas);
				}
			}
			else {
				for (final FavoriteElement favoriteElement : result) {
					activity.mAdapter.add(favoriteElement);
				}
				activity.mAdapter.notifyDataSetChanged();
				if (!init) {
					activity.noAnotherFAs =
							result.size() < (PriznajApplication.LIST_VIEW_LOADING_LIMIT_Y) + result.size() / PriznajApplication.LIST_VIEW_AD_OCCURRENCE;
					activity.footer.setVisibility(activity.noAnotherFAs ? View.VISIBLE : View.GONE);
				}
				activity.pager.setVisibility(View.VISIBLE);
				activity.pager.setText("1");
				if (PriznajApplication.D) {
					Log.d(PriznajApplication.DEBUG_TAG, "FavoriteAdmissionActivity> FavoriteAsyncTask KONIEC Init:" + init + ", Vybral som FAs count:"
							+ result.size() + ", limitX v DROIDe: " + FavoriteDroid.limitX + ",noAnotherFAs=" + activity.noAnotherFAs);
				}
			}
			if (init) {
				activity.listView.setSelection(activity.scrollPosition > 0 ? activity.scrollPosition + 1 : activity.scrollPosition);
				activity.dialog.cancel();
			}
			ready = true;
			activity = null;
		}

		@Override
		protected void onCancelled() {
			ready = true;
			activity = null;
			super.onCancelled();
		}
	}
}
