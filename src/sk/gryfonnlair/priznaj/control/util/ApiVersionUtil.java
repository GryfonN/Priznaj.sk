package sk.gryfonnlair.priznaj.control.util;

import android.os.Build;


/**
 * Obsahuje staticke final metody kt vracaju bollean a pytaju sa na verziu
 * android API
 * 
 * @author gryfonn
 * 
 */
public final class ApiVersionUtil {

	/**
	 * API 8
	 * 
	 * @return
	 */
	public static final boolean hasFroyo() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO;
	}

	/**
	 * API 9
	 * 
	 * @return
	 */
	public static final boolean hasGingerbread() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD;
	}

	/**
	 * API 11
	 * 
	 * @return
	 */
	public static final boolean hasHoneycomb() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
	}

	/**
	 * API 13
	 * 
	 * @return
	 */
	public static final boolean hasHoneycomb2() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2;
	}
}
