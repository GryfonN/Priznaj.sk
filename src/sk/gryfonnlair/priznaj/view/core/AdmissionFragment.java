package sk.gryfonnlair.priznaj.view.core;

import java.util.ArrayList;
import java.util.List;

import sk.gryfonnlair.priznaj.PriznajApplication;
import sk.gryfonnlair.priznaj.R;
import sk.gryfonnlair.priznaj.control.AdmissionDroid;
import sk.gryfonnlair.priznaj.control.AdmissionDroid.RestResponse;
import sk.gryfonnlair.priznaj.control.rest.RestDroid;
import sk.gryfonnlair.priznaj.control.util.ApiVersionUtil;
import sk.gryfonnlair.priznaj.control.util.FontUtil;
import sk.gryfonnlair.priznaj.model.specific.AdmissionType;
import sk.gryfonnlair.priznaj.view.core.tools.CoreElement;
import sk.gryfonnlair.priznaj.view.core.tools.CoreElementAdapter;
import sk.gryfonnlair.priznaj.view.ui.PProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class AdmissionFragment extends Fragment implements OnScrollListener {

	//kluce
	public static final String FRAGMENT_TAG = "AdmissionFragment";
	public static final String ARGUMETNS_KEY_ADMISSION_TYPE = "AdmissionFragment_admissionType";
	public static final String ARGUMETNS_KEY_DRAWER_CHILD_ID = "AdmissionFragment_categoryId";
	//kecy
	static final String MSG_NO_INTERNET_CONNECTION = "Zapnite si internet pre ďalšie priznania.";
	static final String MSG_INTERNET_CONNECTION_ERROR = "Došlo k chybe spojenia.";
	static final String MSG_EMPTY_FROM_SERVER = "Na servery sa viac priznaní nenachádza.";
	static final String MSG_NO_ADMISSIONS_DOWNLOADED_YET = "Nestiahli ste ešte žiadne priznania zo servera.";
	//inicializacne
	private AdmissionType admissionsType;
	private Integer drawerChildId;
	//navigacne a zaroven ukladam
	private boolean noAnotherCAsFromDB;
	private boolean noAnotherCAsFromServer;
	private int savedScrollPosition;
	//objekty fragmnetu
	private LayoutInflater inflater;
	private View listViewFooter;
	private String footerDownload;
	private String footerNoAnother;
	private Button listViewFooterButton;
	PProgressDialog progressDialog;
	/**
	 * Adapter je global a zachova sa koli fragmentu, takze je vytvoreny v
	 * onCreate iba raz ked sa tvori fragment
	 */
	private CoreElementAdapter coreElementAdapter;
	private CoreAsyncTask coreAsyncTask;
	private ListView listView;
	/**
	 * pager v rohu listviewu
	 */
	TextView pager;

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// ! VELMI DOLEZITE Retain this fragment across configuration changes.
		setRetainInstance(true);

		AdmissionDroid.limitX = 0;

		admissionsType = AdmissionType.fromIdentifier(getArguments().getInt(ARGUMETNS_KEY_ADMISSION_TYPE, 0));
		final int tempDrawerChildId = getArguments().getInt(ARGUMETNS_KEY_DRAWER_CHILD_ID, -1);
		drawerChildId = tempDrawerChildId == -1 ? null : tempDrawerChildId;
		if (PriznajApplication.D) {
			Log.d(PriznajApplication.DEBUG_TAG, "AdmissionFragment> onCreate: setol som type=" + admissionsType + " a category=" + drawerChildId);
		}

		inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		listViewFooter = inflater.inflate(R.layout.activity_core_footer_layout, null, false);
		footerDownload = getActivity().getResources().getString(R.string.activity_core_footer_download);
		footerNoAnother = getActivity().getResources().getString(R.string.activity_core_footer_no_another);
		listViewFooterButton = (Button) listViewFooter.findViewById(R.id.activity_core_footer_button);
		listViewFooterButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(final View v) {
				startRestGetOlder();
			}
		});
		FontUtil.setOswaldRegularFont(listViewFooterButton);

		progressDialog = new PProgressDialog(getActivity());
		coreElementAdapter = new CoreElementAdapter(admissionsType, drawerChildId, getActivity(), 0, new ArrayList<CoreElement>());

		noAnotherCAsFromDB = false;
		noAnotherCAsFromServer = false;
		listViewFooter.setVisibility(View.GONE);
		savedScrollPosition = 0;
		listViewFooterButton.setEnabled(!noAnotherCAsFromServer);
		listViewFooterButton.setText(footerDownload);

		if (PriznajApplication.D) {
			Log.d(PriznajApplication.DEBUG_TAG, "AdmissionFragment> onCreate: nastavujem noAnotherCAsFromDB=" + noAnotherCAsFromDB +
					", noAnotherCAsFromServer=" + noAnotherCAsFromServer + ", savedScrollPosition=" + savedScrollPosition);
		}
	}

	@Override
	public void onStart() {
		if (PriznajApplication.D) {
			Log.d(PriznajApplication.DEBUG_TAG, "AdmissionFragment> onStart: znova vytvaram chache, clearujem adapter, zapinam initVlakno");
		}
		//preotze cistim cache v onStop() aby sa zbytocne nedrzala pamet ak som v inych aktivitach
		coreElementAdapter.adMemoryCache = new LruCache<String, Bitmap>(PriznajApplication.LIST_VIEW_AD_CACHE_MEMORY_IN_KILOBYTES);
		coreElementAdapter.clear();
		progressDialog.show();
		startInitCoreTask();
		super.onStart();
	}

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		if (PriznajApplication.D) {
			Log.d(PriznajApplication.DEBUG_TAG, "AdmissionFragment> onCreateView");
		}
		final View rootView = inflater.inflate(R.layout.activity_main_fragment_addmision_list, container, false);

		listView = (ListView) rootView.findViewById(R.id.activity_main_listview);
		listView.addFooterView(listViewFooter);
		listView.setAdapter(coreElementAdapter);
		listView.setOnScrollListener(this);
		pager = (TextView) rootView.findViewById(R.id.activity_main_pager);
		pager.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(final View v) {
				Toast.makeText(v.getContext(), pager.getText() + ". strana", Toast.LENGTH_SHORT).show();
			}
		});
		return rootView;
	}

	@Override
	public void onStop() {
		coreElementAdapter.adMemoryCache = null;
		if (coreAsyncTask != null) {
			coreAsyncTask.cancel(true);
		}
		if (progressDialog != null) {
			progressDialog.cancel();
		}
		pager.setVisibility(View.GONE);
		super.onStop();
	}

	@Override
	public void onDestroy() {
		//Vycistim AdMob Viewy lebo maze sa adapter a musia sa destroyvat ak sa uz nepouzivaju
		coreElementAdapter.clearAdViews();
		super.onDestroy();
	}

	@Override
	public void onSaveInstanceState(final Bundle outState) {
		//toto je retain fragment uchovava si properties hodnoty na orientationChange
		savedScrollPosition = listView.getFirstVisiblePosition();
		if (PriznajApplication.D) {
			Log.d(PriznajApplication.DEBUG_TAG, "AdmissionFragment> onSaveInstanceState: ukladam noAnotherCAsFromDB=" + noAnotherCAsFromDB +
					", noAnotherCAsFromServer=" + noAnotherCAsFromServer + ",footerVisible=" + listViewFooter.getVisibility() +
					", savedScrollPosition=" + savedScrollPosition);
		}
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onScroll(final AbsListView view, final int firstVisibleItem, final int visibleItemCount, final int totalItemCount) {
		if (pager != null) {
			//vypocet cisla strany pagera, vsetky minus pocet reklam delene 10, lebo to je strana na servery, HINT, cca vypocet lebo reklamy nie vzdy su
			final int pageNumber = (firstVisibleItem - (totalItemCount / PriznajApplication.LIST_VIEW_AD_OCCURRENCE)) / 10 + 1;
			pager.setText(Integer.toString(pageNumber));
			//vypocet viditelnosti pagera
			if (firstVisibleItem + visibleItemCount + 1 > totalItemCount) {
				pager.setVisibility(View.GONE);
			} else {
				pager.setVisibility(View.VISIBLE);
			}
		}

		if (coreElementAdapter.isEmpty()) {
			return;
		}
		final int lastItem = firstVisibleItem + visibleItemCount + PriznajApplication.LIST_VIEW_LOADING_BOTTOM_BORDER;
		if (lastItem >= totalItemCount && coreAsyncTask != null && coreAsyncTask.ready && !noAnotherCAsFromDB) {
			startScrollCoreTask();
		}

	}

	@Override
	public void onScrollStateChanged(final AbsListView view, final int scrollState) {

	}

	/**
	 * Bud sa spusti v onCreate alebo dodatocne na init z fottera po stiahnuti
	 */
	public void startInitCoreTask() {
		if (coreAsyncTask != null) {
			coreAsyncTask.cancel(true);
		}
		coreAsyncTask = new CoreAsyncTask(
				true,
				new OnCoreTaskEndCallback() {

					@Override
					public void onCoreTaskEndCallback(final List<CoreElement> result) {
						if (result == null) {
							if (PriznajApplication.D) {
								Log.d(PriznajApplication.DEBUG_TAG, "AdmissionFragment> startInitCoreTask> vlakno bolo zrusene NULL prisiel" +
										", AdmissionDroid.limitX=" + AdmissionDroid.limitX);
							}
						} else if (result.isEmpty()) {
							if (PriznajApplication.D) {
								Log.d(PriznajApplication.DEBUG_TAG, "AdmissionFragment> startInitCoreTask> prisiel prazdny arraylist" +
										", AdmissionDroid.limitX=" + AdmissionDroid.limitX);
							}
							Toast.makeText(getActivity(), MSG_NO_ADMISSIONS_DOWNLOADED_YET, Toast.LENGTH_SHORT).show();
							listViewFooter.setVisibility(View.VISIBLE);
							listViewFooterButton.setEnabled(true);
							listViewFooterButton.setText(footerDownload);
						} else {
							if (PriznajApplication.D) {
								Log.d(PriznajApplication.DEBUG_TAG, "AdmissionFragment> startInitCoreTask> prisiel arraylist size=" + result.size() +
										", AdmissionDroid.limitX=" + AdmissionDroid.limitX);
							}
							for (final CoreElement elementToAdd : result) {
								coreElementAdapter.add(elementToAdd);
							}
							coreElementAdapter.notifyDataSetChanged();
							if (listView != null) {
								listView.setSelection(savedScrollPosition > 0 ? savedScrollPosition + 1 : savedScrollPosition);
							}

							pager.setVisibility(View.VISIBLE);
							pager.setText("1");
						}
						progressDialog.cancel();
					}
				},
				admissionsType, drawerChildId);
		if (ApiVersionUtil.hasHoneycomb()) {
			coreAsyncTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, new Void[] {});
		}
		else {
			coreAsyncTask.execute();
		}
	}

	public void startScrollCoreTask() {
		if (coreAsyncTask != null) {
			coreAsyncTask.cancel(true);
		}
		coreAsyncTask = new CoreAsyncTask(
				false,
				new OnCoreTaskEndCallback() {

					@Override
					public void onCoreTaskEndCallback(final List<CoreElement> result) {
						if (result == null) {
							if (PriznajApplication.D) {
								Log.d(PriznajApplication.DEBUG_TAG, "AdmissionFragment> onCoreTaskEndCallback> SCROLL vlakno bolo zrusene NULL prisiel" +
										", AdmissionDroid.limitX=" + AdmissionDroid.limitX);
							}
							return;
						} else if (result.isEmpty()) {
							if (PriznajApplication.D) {
								Log.d(PriznajApplication.DEBUG_TAG, "AdmissionFragment> onCoreTaskEndCallback> SCROLL prisiel prazdny arraylist" +
										", AdmissionDroid.limitX=" + AdmissionDroid.limitX);
							}
						} else {
							if (PriznajApplication.D) {
								Log.d(PriznajApplication.DEBUG_TAG,
										"AdmissionFragment> onCoreTaskEndCallback> SCROLL prisiel arraylist size=" + result.size() +
												", AdmissionDroid.limitX=" + AdmissionDroid.limitX);
							}
							for (final CoreElement elementToAdd : result) {
								coreElementAdapter.add(elementToAdd);
							}
							coreElementAdapter.notifyDataSetChanged();
						}
						//check ci treba REST call
						if (result.size() < (PriznajApplication.LIST_VIEW_LOADING_LIMIT_Y) + result.size() / PriznajApplication.LIST_VIEW_AD_OCCURRENCE) {
							noAnotherCAsFromDB = true;
							listViewFooter.setVisibility(View.VISIBLE);
							listViewFooterButton.setText(noAnotherCAsFromServer ? footerNoAnother : footerDownload);
							if (PriznajApplication.D) {
								Log.d(PriznajApplication.DEBUG_TAG,
										"AdmissionFragment> onCoreTaskEndCallback> SCROLL prislo menej priznani ako limitY, volam REST" +
												" a nastavujem noAnotherCAsFromDB=" + noAnotherCAsFromDB + ",footerText=" + listViewFooterButton.getText());
							}
						}
					}
				},
				admissionsType, drawerChildId);
		if (ApiVersionUtil.hasHoneycomb()) {
			coreAsyncTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, new Void[] {});
		}
		else {
			coreAsyncTask.execute();
		}
	}

	public void startRestGetOlder() {
		if (!RestDroid.networkAvailable) {
			Toast.makeText(getActivity(), MSG_NO_INTERNET_CONNECTION, Toast.LENGTH_SHORT).show();
			return;
		}
		if (noAnotherCAsFromServer) {
			return;
		}
		Log.d(PriznajApplication.DEBUG_TAG, "AdmissionFragment> startRestGetOlder: Taham z server 200 dalsich priznani pre tabulku " + admissionsType);

		progressDialog.show();
		final RestAsyncTask restTask = new RestAsyncTask(
				new OnRestTaskEndCallback() {

					@Override
					public void onRestTaskEndCallback(final RestResponse result) {
						if (result == RestResponse.HTTP_ERROR) {
							Toast.makeText(getActivity(), MSG_INTERNET_CONNECTION_ERROR, Toast.LENGTH_SHORT).show();
							listViewFooterButton.setEnabled(true);
						} else if (result == RestResponse.NO_ADMISSIONS) {
							Toast.makeText(getActivity(), MSG_EMPTY_FROM_SERVER, Toast.LENGTH_SHORT).show();
							noAnotherCAsFromServer = true;
							listViewFooterButton.setEnabled(false);
						} else if (result == RestResponse.LESS_ADMISSIONS) {
							//hint: preto len tu fromdb = false lebo ak stiahnem 92 moze ich byt 60 pre kategoriu a aby fungoval nadalej scrollfetch
							noAnotherCAsFromDB = false;
							noAnotherCAsFromServer = true;
							listViewFooterButton.setEnabled(false);
							startScrollCoreTask();
						} else if (result == RestResponse.INIT_ADMISSIONS) {
							if (PriznajApplication.D) {
								Log.d(PriznajApplication.DEBUG_TAG, "AdmissionFragment> startRestGetOlder: STAHOL SOM INIT");
							}
							startInitCoreTask();
						} else {
							noAnotherCAsFromDB = false;
							noAnotherCAsFromServer = false;
							listViewFooterButton.setEnabled(true);
							startScrollCoreTask();
						}
						listViewFooterButton.setText(noAnotherCAsFromServer ? footerNoAnother : footerDownload);
						progressDialog.cancel();
						if (PriznajApplication.D) {
							Log.d(PriznajApplication.DEBUG_TAG, "AdmissionFragment> startRestGetOlder stav po reste : " +
									"noAnotherCAsFromDB=" + noAnotherCAsFromDB + ", noAnotherCAsFromServer=" + noAnotherCAsFromServer +
									", footerButtonEnable=" + listViewFooterButton.isEnabled());
						}
					}
				},
				admissionsType);
		if (ApiVersionUtil.hasHoneycomb()) {
			restTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, new Void[] {});
		}
		else {
			restTask.execute();
		}
	}

	static class CoreAsyncTask extends AsyncTask<Void, Void, List<CoreElement>> {

		private OnCoreTaskEndCallback onCoreTaskEndCallback;
		private final boolean init;
		boolean ready = true;
		private final AdmissionType admissionsType;
		private final Integer drawerChildId;

		public CoreAsyncTask(final boolean init, final OnCoreTaskEndCallback onCoreTaskEndCallback,
				final AdmissionType admissionsType, final Integer drawerChildId) {
			this.init = init;
			this.onCoreTaskEndCallback = onCoreTaskEndCallback;
			this.admissionsType = admissionsType;
			this.drawerChildId = drawerChildId;
		}

		public void disconnectCallback() {
			onCoreTaskEndCallback = null;
		}

		@Override
		protected void onPreExecute() {
			ready = false;
			super.onPreExecute();
		}

		@Override
		protected List<CoreElement> doInBackground(final Void... params) {
			//len preto aby sa plynule zavrel drawer
			SystemClock.sleep(500);
			if (PriznajApplication.D) {
				Log.d(PriznajApplication.DEBUG_TAG, "AdmissionFragment> CoreAsyncTask> doInBackground: admissionsType=" + admissionsType +
						", admissionsCategory=" + drawerChildId + ", AdmissionDroid.limitX=" + AdmissionDroid.limitX);
			}
			return AdmissionDroid.getAdmissions(init, admissionsType, drawerChildId);
		}

		@Override
		protected void onPostExecute(final List<CoreElement> result) {
			if (result == null || isCancelled()) {
				onCoreTaskEndCallback.onCoreTaskEndCallback(null);
			}
			else {
				onCoreTaskEndCallback.onCoreTaskEndCallback(result);
			}
			onCoreTaskEndCallback = null;
			ready = true;
		}

		@Override
		protected void onCancelled() {
			ready = true;
			onCoreTaskEndCallback = null;
			super.onCancelled();
		}
	}

	static interface OnCoreTaskEndCallback {

		void onCoreTaskEndCallback(List<CoreElement> result);
	}

	static class RestAsyncTask extends AsyncTask<Void, Void, RestResponse> {

		private OnRestTaskEndCallback onRestTaskEndCallback;
		private final AdmissionType admissionsType;

		public RestAsyncTask(final OnRestTaskEndCallback onRestTaskEndCallback, final AdmissionType admissionsType) {
			this.onRestTaskEndCallback = onRestTaskEndCallback;
			this.admissionsType = admissionsType;
		}

		public void disconnectCallback() {
			onRestTaskEndCallback = null;
		}

		@Override
		protected RestResponse doInBackground(final Void... params) {
			return AdmissionDroid.getOlderAdmissionsFromServer(admissionsType);
		}

		@Override
		protected void onPostExecute(final RestResponse result) {
			if (result == null || isCancelled()) {
				//preruseny task
				onRestTaskEndCallback.onRestTaskEndCallback(RestResponse.HTTP_ERROR);
			} else {
				onRestTaskEndCallback.onRestTaskEndCallback(result);
			}
			onRestTaskEndCallback = null;
		}

		@Override
		protected void onCancelled() {
			onRestTaskEndCallback = null;
			super.onCancelled();
		}

	}

	static interface OnRestTaskEndCallback {

		void onRestTaskEndCallback(RestResponse result);
	}
}
