package sk.gryfonnlair.priznaj.view.core.tools;

import sk.gryfonnlair.priznaj.PriznajApplication;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;


/**
 * http://stackoverflow.com/questions/6148812/android-admob-causes-memory-leak
 * postup vysvetleny tu:
 * http://stackoverflow.com/questions/9558708/admob-memory-leak-avoiding-by-
 * using-empty-activity
 * 
 * Islo o to ze AdView si vzdy nechal aj po =null .destroy referenciu na nejaku
 * picovinu z aktivity nieco s inputom a toril memory leak a kedze som to
 * puzival v mainaktivite tak mrte velky memory leak
 * 
 * Toto je riesenie s singleton activitou prazdnou pre AdMob
 * 
 * Verzia AdMoB 6.4.1, mozno na novsej pojde ok
 * 
 * 
 * @author gryfonn
 * 
 */
public class AdMobHackActivity extends Activity {

	public static AdMobHackActivity AdMobMemoryLeakWorkAroundActivity;

	//TODO UPDATES sledovat AdMoB ci je novsia verzia ako 6.4.1 a ci uz ma porieseny memory leak, test na malej helow wolrd appke
	public AdMobHackActivity() {
		super();
		if (AdMobMemoryLeakWorkAroundActivity != null) {
			throw new IllegalStateException("This activity should be created only once during the entire application life");
		}
		AdMobMemoryLeakWorkAroundActivity = this;
	}

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (PriznajApplication.D) {
			Log.d(PriznajApplication.DEBUG_TAG, "AdMobHackActivity> onCreate - AdMobActivity");
		}
		finish();
	}

	@Override
	public void finish() {
		super.finish();
	}

	public static final void startAdMobActivity(final Activity activity) {
		if (PriznajApplication.D) {
			Log.i(PriznajApplication.DEBUG_TAG, "AdMobHackActivity> startAdMobActivity");
		}
		final Intent i = new Intent();
		i.setComponent(new ComponentName(activity.getApplicationContext(), AdMobHackActivity.class));
		activity.startActivity(i);
	}

}
