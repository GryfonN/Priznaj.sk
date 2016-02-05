package sk.gryfonnlair.priznaj.control.util;

import sk.gryfonnlair.priznaj.PriznajApplication;
import android.graphics.Typeface;
import android.widget.TextView;


/**
 * Util trieda s metodami na setovanie pisma pre View komponenty
 * 
 * @author gryfonn
 * 
 */
public class FontUtil {

	private static final Typeface OSWALD_REGULAR_FONT = Typeface.createFromAsset(PriznajApplication.getContext().getAssets(), "fonts/Oswald-Regular.otf");

	private FontUtil() {
	}

	/**
	 * Setovanie pisma 'fonts/Oswald-Regular.otf' na pole TextView komponentov,
	 * ktore maju metodu setTypeface(), <b>kludne mozem poslat null mam to
	 * osetrene</b>
	 * 
	 * @param views
	 */
	public static void setOswaldRegularFont(final TextView... views) {
		for (final TextView view : views) {
			if (view != null) {
				view.setTypeface(OSWALD_REGULAR_FONT);
			}
		}
	}

}
