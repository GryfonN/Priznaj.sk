package sk.gryfonnlair.priznaj.control.util;

import android.app.Activity;
import android.content.res.Configuration;
import android.view.WindowManager;


/**
 * Util metody pre nastavenia screenu landscape pre normal a small obrazovku
 * hadzem fulscreen
 * 
 * @author gryfonn
 * 
 */
public final class ScreenUtil {

	private ScreenUtil() {
	}

	public static void setScreenOrientation(final Activity activity) {
		//ak je landscape orientacia
		if (activity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			//ak je maly alebo normal
			switch (activity.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) {
				case Configuration.SCREENLAYOUT_SIZE_NORMAL:
					activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
							WindowManager.LayoutParams.FLAG_FULLSCREEN);
					break;
				case Configuration.SCREENLAYOUT_SIZE_SMALL:
					activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
							WindowManager.LayoutParams.FLAG_FULLSCREEN);
					break;
				default:
					break;
			}

		}
	}
}
