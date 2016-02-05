package sk.gryfonnlair.priznaj.control.activities;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import sk.gryfonnlair.priznaj.PriznajApplication;
import sk.gryfonnlair.priznaj.dao.DataAccessObjectImpl;
import sk.gryfonnlair.priznaj.model.specific.AdmissionType;
import sk.gryfonnlair.priznaj.model.specific.FavoriteAdmission;
import sk.gryfonnlair.priznaj.view.favorite.tools.FavoriteElement;
import android.util.Log;


/**
 * Controller, ktory pracuje s db pre Favorite activity
 * 
 * @author gryfonn
 * 
 */
public class FavoriteDroid {

	public static volatile int limitX = 0;

	public static synchronized List<FavoriteElement> getAdmissions(final boolean initSelect) {
		return initSelect ? getInitAdmissions() : getAnotherAdmissions();
	}

	private static List<FavoriteElement> getInitAdmissions() {
		final List<FavoriteAdmission> admissions = DataAccessObjectImpl.INSTACE.getLimitFavoriteAdmissions(
				0,
				limitX == 0 ? PriznajApplication.LIST_VIEW_LOADING_LIMIT_Y : limitX);
		if (admissions.isEmpty()) {
			return null;
		} else {
			limitX = admissions.size();
			final List<FavoriteElement> elements =
					new ArrayList<FavoriteElement>(admissions.size() + (admissions.size() / PriznajApplication.LIST_VIEW_AD_OCCURRENCE));
			for (final FavoriteAdmission fa : admissions) {
				elements.add(new FavoriteElement(fa));
			}
			insertAdsToFavoriteElementList(elements);
			return elements;
		}
	}

	private static List<FavoriteElement> getAnotherAdmissions() {
		final List<FavoriteAdmission> admissions = DataAccessObjectImpl.INSTACE.getLimitFavoriteAdmissions(
				limitX,
				PriznajApplication.LIST_VIEW_LOADING_LIMIT_Y);
		if (admissions.isEmpty()) {
			return null;
		} else {
			limitX += admissions.size();
			final List<FavoriteElement> elements = new ArrayList<FavoriteElement>(admissions.size() + (admissions.size() / PriznajApplication.LIST_VIEW_AD_OCCURRENCE));
			for (final FavoriteAdmission fa : admissions) {
				elements.add(new FavoriteElement(fa));
			}
			insertAdsToFavoriteElementList(elements);
			return elements;
		}
	}

	/**
	 * Vymaze favorite a aj updatene core tabulky, volat vo vlakne, je to cez
	 * transakciu riesene
	 * 
	 * @param fas
	 */
	public static void deleteFavoriteAdmissions(final List<FavoriteAdmission> fas) {
		final Set<Integer> favoriteIDs = new HashSet<Integer>(fas.size());
		final Set<Integer> genderIDs = new HashSet<Integer>(fas.size());
		final Set<Integer> highschoolIDs = new HashSet<Integer>(fas.size());
		final Set<Integer> universityIDs = new HashSet<Integer>(fas.size());
		//pretriedenie do 4 zoznamov
		for (final FavoriteAdmission fa : fas) {
			favoriteIDs.add(fa.id);
			if (fa.admissionType == AdmissionType.GIRLS_BOYS) {
				genderIDs.add(fa.originId);
			} else if (fa.admissionType == AdmissionType.HIGH_SCHOOL) {
				highschoolIDs.add(fa.originId);
			} else if (fa.admissionType == AdmissionType.UNIVERSITY) {
				universityIDs.add(fa.originId);
			}
		}
		if (PriznajApplication.D) {
		Log.d(PriznajApplication.DEBUG_TAG, "FavoriteDroid>deleteFavoriteAdmissions: fas:" + fas.size() + "\n" +
				", favIDs:" + favoriteIDs.size() +
				", genderIDs:" + genderIDs.size() +
				", highIDs:" + highschoolIDs.size() +
				", uniIDs:" + universityIDs.size()
				);
		}
		final boolean success = DataAccessObjectImpl.INSTACE.removeFavorites(
				genderIDs.isEmpty() ? null : genderIDs.toArray(new Integer[] {}),
				highschoolIDs.isEmpty() ? null : highschoolIDs.toArray(new Integer[] {}),
				universityIDs.isEmpty() ? null : universityIDs.toArray(new Integer[] {}),
				favoriteIDs.toArray(new Integer[] {})
				);
		
		//ak deletnem tak aby som mal korektny limit=kolko priznani je v liste uz a kolko brat
		if (success) {
			limitX -= fas.size();
		}
		if (PriznajApplication.D) {
			Log.d(PriznajApplication.DEBUG_TAG, "FavoriteDroid>deleteFavoriteAdmissions: Mazanie uspesne ? =" + success + ", limitX=" + limitX);
		}
	}

	/**
	 * Povklada do listu elementov reklamu podla AdDroid.adOccurrence
	 * 
	 * @param elements list FE priznani
	 */
	private static void insertAdsToFavoriteElementList(final List<FavoriteElement> elements) {
		final int addCount = elements.size() / PriznajApplication.LIST_VIEW_AD_OCCURRENCE;
		for (int pos = 1; pos <= addCount; pos++) {
			elements.add(pos * PriznajApplication.LIST_VIEW_AD_OCCURRENCE, new FavoriteElement(AdDroid.generateRandomAdDrawableResource()));
		}
	}
}
