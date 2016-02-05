package sk.gryfonnlair.priznaj.view.search;

import java.util.ArrayList;
import java.util.List;

import sk.gryfonnlair.priznaj.PriznajApplication;
import sk.gryfonnlair.priznaj.R;
import sk.gryfonnlair.priznaj.control.activities.SearchDroid;
import sk.gryfonnlair.priznaj.control.util.ApiVersionUtil;
import sk.gryfonnlair.priznaj.control.util.FontUtil;
import sk.gryfonnlair.priznaj.control.util.ScreenUtil;
import sk.gryfonnlair.priznaj.model.specific.AdmissionType;
import sk.gryfonnlair.priznaj.view.core.tools.CoreElement;
import sk.gryfonnlair.priznaj.view.search.tools.SearchElementAdapter;
import sk.gryfonnlair.priznaj.view.search.tools.SearchTitleFragment;
import sk.gryfonnlair.priznaj.view.ui.PProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


//TODO UPDATES I/O MEMORY pocet instancii aktivity je 2+ po otoceni
/*
 * CO sa tyka debugu tam si android pre debugging zobere 20MB z pamete co sa neskr prejavy,
 * samotna tato aktivita proste pracuje cely cas s dvoma a viac instanciami aktivity a tie sa recylkuju
 * potom. Cize v debuggingu aplikacia spadne koli pameti ktora ltt nestaci, ale v RUN AS to ide
 * v pohode vykryva s pametou. Hoc v debuggu sem tam padne 1/100 pripadov po dlhom otacani stale
 * ale v RUN to asi nebude padat
 * 
 * Otazne bude ci vykryje pamet ak bude viac ako tri reklamne bitmapy, ktore drzi LruCache, ale
 * to je problem aj celkovo vsetkych listviewov
 */


/**
 * Aktivita pre vyhladavanie, obycajna aktivita s viewpagerom ako selektorom
 * typu admission
 * 
 * @author gryfonn
 * 
 */
public class SearchAdmissionActivity extends ActionBarActivity implements OnScrollListener {

	static final String KEY_LIMITX = "SearchAdmissionActivity_limitX";
	static final String KEY_SEARCH_WORD = "SearchAdmissionActivity_searchWord";
	static final String KEY_PAGE_POSITION = "SearchAdmissionActivity_page_position";
	static final String KEY_FOOTER_VISIBLE = "SearchAdmissionActivity_footer";
	static final String KEY_NO_ANOTHER_SAS = "SearchAdmissionActivity_noAnotherSAs";
	static final String KEY_SEARCH_TYPE = "SearchAdmissionActivity_searchType";
	static final String KEY_SCROLL_POSITION = "SearchAdmissionActivity_scrollY";
	static final String KEY_VIEW_PAGER_VISIBILITY = "SearchAdmissionActivity_viewPager";

	private String savedWord = "";
	private int savedLimitX = 0;
	private int savedScrollPosition;
	/**
	 * Keby user vymazal slovo z editText (searchBox) tak v tejto premmenne je
	 * uchovane a moze fungovat scroll a dalej vyhladavat slovo ktore raz zadal
	 */
	private String searchWordForScroll = "";
	/**
	 * Rozhoduje o spusteni searchTasku na scroll, je zbytocne selectovat ak uz
	 * raz mi prisiel null a nic v db nieje
	 */
	boolean noAnotherSAs = false;
	/**
	 * Typ priznani ktore hladat, seektuje sa viewPagerom
	 */
	public AdmissionType searchType;
	PProgressDialog progressDialog;
	/**
	 * adapter pre listView
	 */
	SearchElementAdapter listViewAdapter;
	View listViewFooter;
	/**
	 * premenna vlakna preto aby som checkoval aktualne spustene vlakno pomocou
	 * ready property v nom, a nespustil dve anonymne objekty vlakna naraz
	 */
	SearchAsyncTask searchAsyncTask;
	/**
	 * EditText v actionbare na vyhladavanie
	 */
	private EditText searchBox;
	/**
	 * selektor typu priznani
	 */
	private ViewPager viewPager;
	/**
	 * vytiahnuty koli setovaniu scrollPosition
	 */
	private ListView listView;
	/**
	 * pager v rohu listviewu
	 */
	TextView pager;
	private InputMethodManager inputMethodManager;

	@Override
	protected void onCreate(final Bundle bundle) {
		super.onCreate(bundle);
		ScreenUtil.setScreenOrientation(this);
		setContentView(R.layout.activity_search);

		progressDialog = new PProgressDialog(this);
		//klavesnica manazer
		inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

		//ActioBar
		final ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setCustomView(R.layout.activity_search_action_bar);
		final ImageButton searchButton = (ImageButton) actionBar.getCustomView().findViewById(R.id.activity_search_button);
		searchButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(final View v) {
				searchFromSearchBox();
			}
		});
		searchBox = (EditText) actionBar.getCustomView().findViewById(R.id.activity_search_box);
		searchBox.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(final TextView v, final int actionId, final KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_SEARCH) {
					searchFromSearchBox();
					return false;
				}
				return false;
			}
		});
		searchBox.setOnFocusChangeListener(new View.OnFocusChangeListener() {

			@Override
			public void onFocusChange(final View v, final boolean hasFocus) {
				if (hasFocus && v.equals(searchBox)) {
					viewPager.setVisibility(View.VISIBLE);
				}
			}
		});
		//ViewPager
		viewPager = (ViewPager) findViewById(R.id.activity_search_viewpager);
		viewPager.setVisibility(bundle == null ? View.VISIBLE : bundle.getInt(KEY_VIEW_PAGER_VISIBILITY, View.VISIBLE));
		viewPager.setAdapter(new SearchViewPagerAdapter(getSupportFragmentManager()));
		viewPager.setCurrentItem(bundle == null ? 1 : bundle.getInt(KEY_PAGE_POSITION, 1));
		viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

			@Override
			public void onPageSelected(final int pagePosition) {
				searchBox.clearFocus();
				switch (pagePosition) {
					case 0:
						searchType = AdmissionType.GIRLS_BOYS;
						break;
					case 1:
						searchType = AdmissionType.UNIVERSITY;
						break;
					case 2:
						searchType = AdmissionType.HIGH_SCHOOL;
						break;
					default:
						break;
				}
				if (PriznajApplication.D) {
					Log.d(PriznajApplication.DEBUG_TAG, "SearchAdmissionActivity>onPageSelected" + searchType.toString());
				}
				cleanActivity(true);
			}

			@Override
			public void onPageScrolled(final int arg0, final float arg1, final int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(final int arg0) {
			}
		});
		//footer
		listViewFooter = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.activity_search_footer_layout, null, false);
		listViewFooter.setVisibility(bundle == null ? View.GONE : bundle.getInt(KEY_FOOTER_VISIBLE, View.GONE));
		noAnotherSAs = bundle == null ? false : bundle.getBoolean(KEY_NO_ANOTHER_SAS, false);
		FontUtil.setOswaldRegularFont(searchBox, (TextView) listViewFooter.findViewById(R.id.activity_search_footer_text));
		listView = (ListView) findViewById(R.id.activity_search_listview);
		listView.addFooterView(listViewFooter);
		listViewAdapter = new SearchElementAdapter(this, 0, new ArrayList<CoreElement>(1));
		listView.setAdapter(listViewAdapter);
		listView.setOnScrollListener(this);

		pager = (TextView) findViewById(R.id.activity_search_pager);
		pager.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(final View v) {
				Toast.makeText(v.getContext(), pager.getText() + ". strana", Toast.LENGTH_SHORT).show();
			}
		});

		savedWord = bundle == null ? "" : bundle.getString(KEY_SEARCH_WORD);
		searchBox.setText(savedWord);
		savedLimitX = bundle == null ? 0 : bundle.getInt(KEY_LIMITX, 0);
		SearchDroid.limitX = savedLimitX;
		searchType = bundle == null ? AdmissionType.UNIVERSITY : AdmissionType.fromIdentifier(bundle.getInt(KEY_SEARCH_TYPE, 2));
		savedScrollPosition = bundle == null ? 0 : bundle.getInt(KEY_SCROLL_POSITION, 0);
	}

	@Override
	protected void onResume() {
		/**
		 * inak povedane ak bolo v predoslej aktivite limit zdvihnuty a hladalo
		 * sa to znamena ze treba restore aktivitu, nezrozumitelny check ale je
		 * to tak
		 * 
		 * isEmpty je preto lebo ako v favorite performance hack
		 */
		if (listViewAdapter.isEmpty() && savedLimitX > 0) {
			searchWordForScroll = savedWord != null ? savedWord : "";
			SearchDroid.limitX = savedLimitX;
			if (PriznajApplication.D) {
				Log.d(PriznajApplication.DEBUG_TAG, "SearchAdmissionActivity>onResume obnovil som , searchWordForScroll:" + searchWordForScroll + ", limitX:"
						+ SearchDroid.limitX + ", savedScrollPosition:" + savedScrollPosition);
			}
			listViewAdapter.setHighlightWord(searchWordForScroll);
			searchAsyncTask = new SearchAsyncTask(true, true, searchType, this);
			if (ApiVersionUtil.hasHoneycomb()) {
				searchAsyncTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, searchWordForScroll);
			}
			else {
				searchAsyncTask.execute(searchWordForScroll);
			}
		}
		super.onResume();
	}

	@Override
	protected void onSaveInstanceState(final Bundle outState) {
		outState.putInt(KEY_PAGE_POSITION, viewPager.getCurrentItem());
		outState.putInt(KEY_LIMITX, SearchDroid.limitX);
		outState.putString(KEY_SEARCH_WORD, searchWordForScroll);
		outState.putInt(KEY_FOOTER_VISIBLE, listViewFooter.getVisibility());
		outState.putBoolean(KEY_NO_ANOTHER_SAS, noAnotherSAs);
		outState.putInt(KEY_SEARCH_TYPE, searchType.getIdentifier());
		outState.putInt(KEY_VIEW_PAGER_VISIBILITY, viewPager.getVisibility());
		outState.putInt(KEY_SCROLL_POSITION, listView.getFirstVisiblePosition());
		if (PriznajApplication.D) {
			Log.d(PriznajApplication.DEBUG_TAG, "SearchAdmissionActivity>onSaveInstanceState ukladam ViewPager pozicia:" + viewPager.getCurrentItem() +
					", limitX:" + SearchDroid.limitX + ", searchWordForScroll:" + searchWordForScroll + ", footerVisibility:" + listViewFooter.getVisibility() +
					", searchType:" + searchType + ", scrollPosition:" + listView.getFirstVisiblePosition() + ", noAnotherSAs:" + noAnotherSAs);
		}
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onStop() {
		if (searchAsyncTask != null) {
			searchAsyncTask.disconnectActivity();
			searchAsyncTask.cancel(true);
		}
		if (progressDialog != null) {
			progressDialog.cancel();
		}
		super.onStop();
	}

	/**
	 * Volam to ak sa zmeni viewPager,cize ked selektnem search z inej tabulky
	 * (parameter true), alebo ako searchbox sa aktivuje hladanie
	 * <p>
	 * zastavim search vlakono ak bezi, vycistim searchBox podla parametru,
	 * searchWordForScrol, listViewAdapter, limitX setnem na 0, footer skryjem
	 * 
	 * @param includeSearchBox true ak ma premazat searchBox, false ak nie, napr
	 *        ked kliknem hladat nechcem zmazat searchBox aby user videl co
	 *        hlada
	 */
	private void cleanActivity(final boolean includeSearchBox) {
		if (searchAsyncTask != null) {
			searchAsyncTask.disconnectActivity();
			searchAsyncTask.cancel(true);
		}
		if (includeSearchBox) {
			searchBox.setText("");
		}
		pager.setVisibility(View.GONE);
		searchWordForScroll = "";
		savedScrollPosition = 0;
		noAnotherSAs = false;
		SearchDroid.limitX = 0;
		listViewAdapter.clear();
		listViewAdapter.notifyDataSetChanged();
		listViewFooter.setVisibility(View.GONE);
		if (PriznajApplication.D) {
			Log.d(PriznajApplication.DEBUG_TAG, "SearchAdmissionActivity>cleanActivity" + searchType);
		}
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

	/**
	 * Interface, listener pre listView kt spusta search vlano
	 */
	@Override
	public void onScroll(final AbsListView view, final int firstVisibleItem, final int visibleItemCount, final int totalItemCount) {
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
		if (listViewAdapter.isEmpty()) {
			return;
		}
		final int lastItem = firstVisibleItem + visibleItemCount + PriznajApplication.LIST_VIEW_LOADING_BOTTOM_BORDER;
		if (lastItem >= totalItemCount && searchAsyncTask != null && searchAsyncTask.ready && !noAnotherSAs) {
			searchAsyncTask = new SearchAsyncTask(false, false, searchType, this);
			if (ApiVersionUtil.hasHoneycomb()) {
				searchAsyncTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, searchWordForScroll);
			}
			else {
				searchAsyncTask.execute(searchWordForScroll);
			}
		}
	}

	/**
	 * interface, nepotrebne
	 */
	@Override
	public void onScrollStateChanged(final AbsListView view, final int scrollState) {
		//nic neni tu treba
	}

	/**
	 * Spusti hladanie so searchBoxu na imeOption alebo na imageButton click,
	 * riesi duplicitu kodu
	 */

	private void searchFromSearchBox() {
		final String searchWord = searchBox.getText().toString().trim();
		if (searchWord == null || searchWord.length() < 3) {
			Toast.makeText(this, getString(R.string.activity_search_searchbox_msg_invalid), Toast.LENGTH_LONG).show();
			return;
		} else {
			//skryt klavesnicu
			inputMethodManager.hideSoftInputFromWindow(searchBox.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);

			searchBox.clearFocus();
			viewPager.setVisibility(View.GONE);
			cleanActivity(false);
			searchWordForScroll = searchWord;
			listViewAdapter.setHighlightWord(searchWordForScroll);
			searchAsyncTask = new SearchAsyncTask(false, true, searchType, this);
			if (ApiVersionUtil.hasHoneycomb()) {
				searchAsyncTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, searchWordForScroll);
			}
			else {
				searchAsyncTask.execute(searchWordForScroll);
			}
		}
	}

	/**
	 * Vlakno ktore riesi aj init aj bottomFetch
	 * 
	 * @author gryfonn
	 * 
	 */
	static class SearchAsyncTask extends AsyncTask<String, Void, List<CoreElement>> {
		private SearchAdmissionActivity activity;
		/**
		 * indikator isRunning, aby som nespustil na scrolle dva krat vlakno
		 * pokial jedno ide, pre bottomFetch je to pouzitelne hlavne
		 */
		volatile boolean ready = true;
		/**
		 * indikator ci sa bude jedna o init select, posuva sa to do Droida kt
		 * rozhoduje podla toho o limitoch a spusta prislusnu metodu v
		 * DatabaseUtil
		 */
		private final boolean initSelect;
		/**
		 * Rozhoduje o tom ci chcem spustit progress dialog alebo nie, napriklad
		 * chcme pre button click a orientationChange, preto sa nerozhodujem len
		 * polda initu
		 */
		private final boolean withProgressDialog;
		/**
		 * posuva sa do Droida a DAOa,ake typy priznani hladam
		 */
		private final AdmissionType fragmentType;

		public SearchAsyncTask(final boolean initSelect, final boolean withProgressDialog,
				final AdmissionType fragmentType, final SearchAdmissionActivity activity) {
			this.initSelect = initSelect;
			this.withProgressDialog = withProgressDialog;
			this.fragmentType = fragmentType;
			this.activity = activity;
		}

		public void disconnectActivity() {
			activity = null;
		}

		@Override
		protected void onPreExecute() {
			ready = false;
			if (withProgressDialog) {
				activity.progressDialog.show();
			}
			super.onPreExecute();
		}

		@Override
		protected List<CoreElement> doInBackground(final String... params) {
			if (PriznajApplication.D) {
				Log.d(PriznajApplication.DEBUG_TAG, "SearchAdmissionActivity>SearchAsyncTask START Init=" + initSelect +
						" spustam task pre fragmentType:" + fragmentType + ", hladam slovo:" + params[0]);
			}
			return SearchDroid.searchAdmissions(initSelect, fragmentType, params[0]);
		}

		@Override
		protected void onPostExecute(final List<CoreElement> result) {
			if (result == null || isCancelled()) {

			} else if (result.isEmpty()) {
				activity.noAnotherSAs = true;
				activity.listViewFooter.setVisibility(View.VISIBLE);
				if (PriznajApplication.D) {
					Log.d(PriznajApplication.DEBUG_TAG, "SearchAdmissionActivity: SearchAsyncTask KONEC Init=" + initSelect +
							" vybral elementov:" + result.size() + ", limitX v DROIDe: " + SearchDroid.limitX + ",nullOnScroll=" + activity.noAnotherSAs);
				}
			} else {
				for (final CoreElement elementToAdd : result) {
					activity.listViewAdapter.add(elementToAdd);
				}
				activity.listViewAdapter.notifyDataSetChanged();
				if (!initSelect) {
					activity.noAnotherSAs =
							result.size() < (PriznajApplication.LIST_VIEW_LOADING_LIMIT_Y) + result.size() / PriznajApplication.LIST_VIEW_AD_OCCURRENCE;
					activity.listViewFooter.setVisibility(activity.noAnotherSAs ? View.VISIBLE : View.GONE);
				}
				if (PriznajApplication.D) {
					Log.d(PriznajApplication.DEBUG_TAG, "SearchAdmissionActivity: SearchAsyncTask KONEC Init=" + initSelect +
							" vybral elementov:" + result.size() + ", limitX v DROIDe: " + SearchDroid.limitX + ",nullOnScroll=" + activity.noAnotherSAs);
				}

				activity.pager.setVisibility(View.VISIBLE);
				activity.pager.setText("1");
			}
			if (initSelect) {
				activity.listView.setSelection(activity.savedScrollPosition > 0 ? activity.savedScrollPosition + 1 : activity.savedScrollPosition);
			}
			if (withProgressDialog) {
				activity.progressDialog.cancel();
			}
			ready = true;
			activity = null;
		}

		/**
		 * Ak dam v kode cancel tak pre istotu ready na true setujem
		 */
		@Override
		protected void onCancelled() {
			ready = true;
			activity = null;
			super.onCancelled();
		}
	}

	/**
	 * Adapter pre ViewPager v search aktivite na vyberanie typu priznani pre
	 * hladanie, v konstruktore len posuniem text pre TextView aky maju ukazovat
	 * 
	 * @author gryfonn
	 * 
	 */
	static class SearchViewPagerAdapter extends FragmentStatePagerAdapter {

		public SearchViewPagerAdapter(final FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(final int arg0) {
			final Fragment fragment = new SearchTitleFragment();
			final Bundle bundle = new Bundle();
			switch (arg0) {
				case 0:
					bundle.putString(SearchTitleFragment.KEY_TITLE, "GIRLS/BOYS.PRIZNAJ.SK");
					break;
				case 1:
					bundle.putString(SearchTitleFragment.KEY_TITLE, "PRIZNAJ.SK");
					break;
				case 2:
					bundle.putString(SearchTitleFragment.KEY_TITLE, "STREDNE.PRIZNAJ.SK");
					break;
				default:
					bundle.putString(SearchTitleFragment.KEY_TITLE, "ERROR");
					break;
			}
			fragment.setArguments(bundle);
			return fragment;
		}

		@Override
		public int getCount() {
			return 3;
		}
	}
}
