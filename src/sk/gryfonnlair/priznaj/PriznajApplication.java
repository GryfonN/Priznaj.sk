package sk.gryfonnlair.priznaj;

import java.text.DecimalFormat;

import sk.gryfonnlair.priznaj.view.splash.SplashActivity;
import android.app.Application;
import android.content.Context;
import android.os.Debug;
import android.util.Log;


/**
 * Trieda na ziskanie contextu aplikacie v lubovolnej triede aplikacie <br>
 * <p>
 * Pouzitie napriklad v ..control.util.FontUtil.java pre uschovu fontu do
 * satickej premennej raz aby som nemusel v kazdej activite to robit na
 * onCreate()
 * zdroj:http://stackoverflow.com/questions/4391720/how-can-i-get-a-resource
 * -content-from-a-static-context
 * 
 * @author gryfonn
 * 
 */
public class PriznajApplication extends Application {

	/**
	 * Tag pre Log.d pre celu appku, prehlad, v release sa moze vyhodit
	 */
	public static String DEBUG_TAG = "PRIZNAJ.SK";
	//TODO RELEASE nastavit false a vypnut logy
	//TODO RELEASE codeVersion++ a pripadne ak mam debugged true tak dat prec aj to

	/**
	 * Debug spravy povolene ?
	 */
	public static final boolean D = false;
	/**
	 * pocet elementov zdola v listview ab sa aktivoval znova laoding
	 * dalsich(9gag)
	 */
	public final static int LIST_VIEW_LOADING_BOTTOM_BORDER = 2;
	/**
	 * LimitY pre kazdy droid ze tolko vyberam na scroll z db
	 */
	public final static int LIST_VIEW_LOADING_LIMIT_Y = 30;
	/**
	 * Vyskyt pre reklamu v liste,bacha na limitY ak je mensi tak reklama
	 * nebude medzi elementy vypoctom vlozena
	 */
	public final static int LIST_VIEW_AD_OCCURRENCE = 15;
	/**
	 * <b>TREBA NASTAVIT PODLA POCTU OBRAZKOV PRE REKLAMU</b> momentalne mame
	 * tri obrazky kt maju dokopy pri max rozliseny 220kb takze setujem 300
	 */
	public final static int LIST_VIEW_AD_CACHE_MEMORY_IN_KILOBYTES = 170;
	/**
	 * <b>Velkost sucet velkosti max rozliseni tutorial screenov !</b>
	 */
	public final static int TUTORIAL_CAHCE_MEMORY_IN_KILOBYTES = 700;
	/**
	 * <b>POZOR</b> V {@link SplashActivity#saveNewsCounts} cleanujem vsetko a
	 * znova nasetujem tutorial takze v pripade dalsich pref treba dopisat do
	 * tej metody aby sa zachovali
	 */
	public final static String PREF_TUTORIAL_COMPLETE = "PREF_TUTORIAL_COMPLETE";

	private static Context mContext;


	@Override
	public void onCreate() {
		super.onCreate();
		mContext = this;
	}

	/**
	 * Vratim context aplikacie
	 * 
	 * @return android.content.Context
	 */
	public static Context getContext() {
		return mContext;
	}

	public static void logHeap(@SuppressWarnings("rawtypes") final Class clazz) {
		final Double allocated = Double.valueOf(Debug.getNativeHeapAllocatedSize()) / Double.valueOf((1048576));
		final Double available = Double.valueOf(Debug.getNativeHeapSize()) / 1048576.0;
		final Double free = Double.valueOf(Debug.getNativeHeapFreeSize()) / 1048576.0;
		final DecimalFormat df = new DecimalFormat();
		df.setMaximumFractionDigits(2);
		df.setMinimumFractionDigits(2);

		Log.d(DEBUG_TAG, "debug. =================================");
		Log.d(DEBUG_TAG, "debug.heap native: allocated " + df.format(allocated) + "MB of " + df.format(available) + "MB (" + df.format(free) + "MB free) in ["
				+ clazz.getName().replaceAll("com.myapp.android.", "") + "]");
		Log.d(DEBUG_TAG,
				"debug.memory: allocated: " + df.format(Double.valueOf(Runtime.getRuntime().totalMemory() / 1048576)) + "MB of "
						+ df.format(Double.valueOf(Runtime.getRuntime().maxMemory() / 1048576)) + "MB ("
						+ df.format(Double.valueOf(Runtime.getRuntime().freeMemory() / 1048576)) + "MB free)");
	}
}
