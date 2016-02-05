package sk.gryfonnlair.priznaj.view.splash;

import java.util.HashMap;
import java.util.Map;

import sk.gryfonnlair.priznaj.PriznajApplication;
import sk.gryfonnlair.priznaj.R;
import sk.gryfonnlair.priznaj.control.AdmissionDroid;
import sk.gryfonnlair.priznaj.control.rest.RestDroid;
import sk.gryfonnlair.priznaj.control.util.ApiVersionUtil;
import sk.gryfonnlair.priznaj.control.util.FontUtil;
import sk.gryfonnlair.priznaj.control.util.ScreenUtil;
import sk.gryfonnlair.priznaj.dao.DataAccessObjectImpl;
import sk.gryfonnlair.priznaj.view.core.MainActivity;
import sk.gryfonnlair.priznaj.view.core.tools.AdMobHackActivity;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


/**
 * SplashScreen ako loading screen, logo blika prostrednictvom animacie, na
 * pozadi AsyncTask na volanie rest sluzby pre zakladne info pre apku. <br>
 * <p>
 * Vlakno sa spusti len raz, checkuem to booleanom taskStarted, ktore sa setne v
 * metode onCreate() na true po spusteni a prenasa sa v bundle pri recreate
 * activity. Vlakno je roboene v osobitnej triede kedze bude rozsialhe a chcem
 * ako tak oddelit vyzor od funkcionality
 * 
 * @author gryfonn
 * 
 */
public class SplashActivity extends Activity {

	private ImageView imageView;
	private AnimationDrawable splashAnimation;
	private SplashLoadingAsyncTask task;

	LinearLayout loadingLayout;
	private Animation rotate;
	private View loadingImage;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		ScreenUtil.setScreenOrientation(this);
		setContentView(R.layout.activity_splash);

		//TODO nahodit efekt nech to nevidno tak chujovo
		if (AdMobHackActivity.AdMobMemoryLeakWorkAroundActivity == null) {
			if (PriznajApplication.D) {
				Log.d(PriznajApplication.DEBUG_TAG, "SplashActivity> starting the AdMobActivity");
			}
			AdMobHackActivity.startAdMobActivity(this);
		}

		imageView = (ImageView) findViewById(R.id.activity_splash_logo_iv);
		imageView.setBackgroundResource(R.drawable.activity_splash_logo_animation);
		splashAnimation = (AnimationDrawable) imageView.getBackground();
		loadingLayout = (LinearLayout) findViewById(R.id.activity_splash_loading);
		loadingImage = findViewById(R.id.activity_splash_loading_image);
		rotate = AnimationUtils.loadAnimation(this, R.anim.p_progress_dialog_rotate);
		final TextView loadingText = (TextView) findViewById(R.id.activity_splash_loading_text);
		FontUtil.setOswaldRegularFont(loadingText);

		//musi byt pred zapnutim vlakna inak bude staticka premenna na siet FLASE
		RestDroid.isNetworkConnected(this);

		task = (SplashLoadingAsyncTask) getLastNonConfigurationInstance();
		if (task == null) {
			task = new SplashLoadingAsyncTask();
			try {
				if (ApiVersionUtil.hasHoneycomb()) {
					task.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, new Void[] {});
				}
				else {
					task.execute();
				}
			} catch (final Exception e) {
				if (PriznajApplication.D) {
					Log.e(PriznajApplication.DEBUG_TAG, "SpashActivity: SplashLoadingAsyncTask crashol." + e.getMessage());
				}
				finish();
			}
		}
		task.connectActivity(this);
	}

	@Override
	@Deprecated
	public Object onRetainNonConfigurationInstance() {
		return task;
	}

	@Override
	protected void onStart() {
		super.onStart();
		if (RestDroid.networkAvailable) {
			loadingLayout.setVisibility(View.VISIBLE);
			loadingImage.startAnimation(rotate);
			splashAnimation.start();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		splashAnimation.stop();
	}

	@Override
	public void onBackPressed() {
		//disable back buttonu aby neprerusil loading
	}

	@Override
	public void finish() {
		imageView.clearAnimation();
		loadingLayout.setVisibility(View.GONE);
		loadingImage.clearAnimation();
		task.disconnectActivity();
		task = null;
		super.finish();
	}

	public void taskSuccess(final String newsInJSON) {
		if (PriznajApplication.D) {
			Log.d(PriznajApplication.DEBUG_TAG, "SplashActivity> taskSuccess: news json=" + newsInJSON);
		}
		saveNewsCounts(newsInJSON);
		SplashActivity.this.startActivity(new Intent(SplashActivity.this, MainActivity.class));
		finish();
	}

	/**
	 * Rozparsuje reponse text, vycleanuje sharedPref a ulozi aktualne novinky,
	 * hnusna dlha nepozerat ani :D
	 * 
	 * @param newsInJSON
	 */
	@SuppressLint("CommitPrefEdits")
	private void saveNewsCounts(final String newsInJSON) {
		final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		final boolean restoreTutorialPref = prefs.getBoolean(PriznajApplication.PREF_TUTORIAL_COMPLETE, false);
		//precistenie
		prefs.edit().clear().putBoolean(PriznajApplication.PREF_TUTORIAL_COMPLETE, restoreTutorialPref).commit();
		if (PriznajApplication.D) {
			Log.d(PriznajApplication.DEBUG_TAG, "SplashActivity> saveNewsCounts: Clearujem vsetky preferences a setujem spet TUTORIAL=" + restoreTutorialPref);
		}
		if (newsInJSON.length() < 1) {
			return;
		}
		final Map<String, String> propertiesToSave = new HashMap<String, String>(1);
		//separacia
		final int endOfUniNews = newsInJSON.indexOf("priznania2:");
		final int endOfGBNews = newsInJSON.indexOf("priznania_stredne:");
		String UniNews = newsInJSON.substring(0, endOfUniNews);
		String GBNews = newsInJSON.substring(UniNews.length(), endOfGBNews);
		String HSNews = newsInJSON.substring(endOfGBNews);

		if (UniNews.length() > 10) {
			//odjeben predpodnu a carku na konci
			UniNews = UniNews.substring(10);
			UniNews = UniNews.substring(0, UniNews.length() - 1);
			//rozplitujem a ulozim do mapy
			final String tempArray[] = UniNews.split(",");
			for (int i = 0; i < tempArray.length; i++) {
				final String toMap[] = tempArray[i].split("=");
				propertiesToSave.put(toMap[0], toMap[1]);
			}
		}
		if (GBNews.length() > 11) {
			GBNews = GBNews.substring(11);
			GBNews = GBNews.substring(0, GBNews.length() - 1);
			final String tempArray[] = GBNews.split(",");
			for (int i = 0; i < tempArray.length; i++) {
				final String toMap[] = tempArray[i].split("=");
				propertiesToSave.put(toMap[0].equals("1") ? "G" : "B", toMap[1]);
			}
		}
		if (HSNews.length() > 18) {
			HSNews = HSNews.substring(18);
			HSNews = HSNews.substring(0, HSNews.length() - 1);
			final String tempArray[] = HSNews.split(",");
			for (int i = 0; i < tempArray.length; i++) {
				final String toMap[] = tempArray[i].split("=");
				propertiesToSave.put(toMap[0], toMap[1]);
			}
		}

		if (propertiesToSave.isEmpty()) {
			return;
		}
		//samotne ulozenie
		//S TYMTO MA PROBLEM LINT
		SharedPreferences.Editor editor = prefs.edit();
		for (final Map.Entry<String, String> entry : propertiesToSave.entrySet())
		{
			try {
				editor = editor.putInt(entry.getKey(), Integer.parseInt(entry.getValue()));
			} catch (final NumberFormatException e) {
				if (PriznajApplication.D) {
					Log.e(PriznajApplication.DEBUG_TAG, "SplashActivity> saveNewsCounts: NumberFormatException " + entry.getKey() + "=" + entry.getValue());
				}
			}
			if (PriznajApplication.D) {
				Log.d(PriznajApplication.DEBUG_TAG, "SplashActivity> saveNewsCounts: PREF pripravujem: " + entry.getKey() + "=" + entry.getValue());
			}
		}
		editor.commit();
		if (PriznajApplication.D) {
			Log.d(PriznajApplication.DEBUG_TAG, "SplashActivity> saveNewsCounts: ULOZENE");
		}
	}

	/**
	 * AsyncTask pre SplashScreen, spusta sa v {@link SplashActivity} a ma
	 * sluzit na
	 * zakladnu inicializaciu apky (Rest requesty a podobne)
	 * <p>
	 * Obsahuje Activity ako property, aby vedelo po skonceni ktoru aktivitu
	 * zavriet, je nutne ju setovat rucne pri vytvoreni tohto AsyncTasku
	 * 
	 * @author gryfonn
	 * 
	 */
	static class SplashLoadingAsyncTask extends AsyncTask<Void, Void, String> {

		private SplashActivity callingAcitivty;

		public void connectActivity(final SplashActivity activity) {
			callingAcitivty = activity;
		}

		public void disconnectActivity() {
			callingAcitivty = null;
		}

		@Override
		protected String doInBackground(final Void... params) {
			//Vytvorim db
			DataAccessObjectImpl.INSTACE.createDatabase();
			String newsInJSON = "";
			//ak mam aktivny net idem sa hrat s REST sluzbami
			if (RestDroid.networkAvailable) {
				final Boolean dbEmtpy = DataAccessObjectImpl.INSTACE.isEmtpy();
				//ak error pri checkovani
				if (dbEmtpy == null) {
					return null;
				}
				if (PriznajApplication.D) {
					Log.d(PriznajApplication.DEBUG_TAG, "SplashLoadingAsyncTask>doInBackground: isEmpty=" + dbEmtpy.toString());
				}
				if (dbEmtpy) {
					AdmissionDroid.initializeFirstAdmissions();
				} else {
					newsInJSON = AdmissionDroid.getNews();
				}
			}
			//ak neni net tak len 2 sec aby som videl logo
			else {
				SystemClock.sleep(2000);
			}
			return newsInJSON;
		}

		@Override
		protected void onPostExecute(final String result) {
			if (result == null) {
				callingAcitivty.finish();
			} else {
				callingAcitivty.taskSuccess(result);
			}
		}

		@Override
		protected void onCancelled() {
			callingAcitivty.finish();
			callingAcitivty = null;
			super.onCancelled();
		}
	}
}
