package sk.gryfonnlair.priznaj.control.activities;

import sk.gryfonnlair.priznaj.R;
import android.net.Uri;


/**
 * Droid pre generaciu reklamy, vsetko co s reklamou suvysi
 * 
 * @author gryfonn
 * 
 */
public class AdDroid {
	private static int adCursor = 0;

	/*
	 * !!!!!!!!!!!!!!!!!! POZOR !!!!!!!!!!!!!!!!!!!!!!!!!!!!
	 * Obrazky reklam (bitmapy) sa v listviewe drzia v LruCache pameti,
	 * cize ak sa pocet reklamy zvysi LruCache si nemoze cely cas drzat
	 * referenciu na 100+ bitmap, Testovane to bolo na 3 reklmanych obrazkoch
	 * !!!!
	 * 
	 * v pripade velkeho poctu reklamy by som asi odstranil LruCache a nech
	 * sa android sa stara o Native Heap ako to vie. Kedze sa ref na bitmapy
	 * neudrzia v LruCache
	 */

	/**
	 * priorita je ze prve ukazuje ads[1] a potom dalsie a posledny je ads[0]
	 */
	private static final int[] ads = {
			R.drawable.ad_bord,
			R.drawable.ad_priznaj
	};

	/**
	 * Random drawable resource reklamy na zaklade random cisla z pola reklam v
	 * {@link AdDroid}e
	 * 
	 * @return resourceId
	 */
	public static int generateRandomAdDrawableResource() {
		return getNextAdResource();
	}

	/**
	 * String pre label podla resource na obrazok reklamy
	 * 
	 * @param drawableResourceId
	 * @return String label inak www.priznaj.sk
	 */
	public static String getLabelForAd(final int drawableResourceId) {
		switch (drawableResourceId) {
			case R.drawable.ad_bord:
				return "www.bord.sk";
			default:
				return "www.priznaj.sk";
		}
	}

	/**
	 * Uri podla resource na obrazok reklamy
	 * 
	 * @param drawableResourceId
	 * @return Uri adresu inak http://www.priznaj.sk
	 */
	public static Uri getUriForAd(final int drawableResourceId) {
		switch (drawableResourceId) {
			case R.drawable.ad_bord:
				return Uri.parse("http://www.bord.sk");
			default:
				return Uri.parse("http://www.priznaj.sk");
		}
	}

	private static int getNextAdResource() {
		adCursor = adCursor >= ads.length - 1 ? 0 : adCursor + 1;
		return ads[adCursor];
	}
}
