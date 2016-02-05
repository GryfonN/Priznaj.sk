package sk.gryfonnlair.priznaj.control.activities;

import java.util.ArrayList;
import java.util.List;

import sk.gryfonnlair.priznaj.PriznajApplication;
import sk.gryfonnlair.priznaj.dao.DataAccessObjectImpl;
import sk.gryfonnlair.priznaj.model.specific.AdmissionType;
import sk.gryfonnlair.priznaj.model.specific.CoreAdmission;
import sk.gryfonnlair.priznaj.view.core.tools.CoreElement;


/**
 * Controller, ktory pracuje s db pre Search activity
 * 
 * @author gryfonn
 * 
 */
public class SearchDroid {

	public static volatile int limitX = 0;

	/**
	 * Hlada v db priznania kt obsahju v texte searchWord
	 * 
	 * @param admissionType typ priznania, tabulka v db
	 * @param searchWord hladany vyraz
	 */
	public static synchronized final List<CoreElement> searchAdmissions(final boolean initSelect, final AdmissionType admissionType, final String searchWord) {
		return initSelect ? getInitAdmissions(admissionType, searchWord) : getAnotherAdmissions(admissionType, searchWord);
	}

	/**
	 * LimitX pouzijem ako LimitY, a berem od 0
	 * 
	 * @param admissionType
	 * @param searchWord
	 * @return
	 */
	private static final List<CoreElement> getInitAdmissions(final AdmissionType admissionType, final String searchWord) {
		final List<CoreAdmission> admissions;
		switch (admissionType.getIdentifier()) {
			case 0:
				admissions = DataAccessObjectImpl.INSTACE.searchGBAdmissionsWithLimit(searchWord,
						0, limitX == 0 ? PriznajApplication.LIST_VIEW_LOADING_LIMIT_Y : limitX);
				break;
			case 1:
				admissions = DataAccessObjectImpl.INSTACE.searchHSAdmissionsWithLimit(searchWord,
						0, limitX == 0 ? PriznajApplication.LIST_VIEW_LOADING_LIMIT_Y : limitX);
				break;
			case 2:
				admissions = DataAccessObjectImpl.INSTACE.searchUNIAdmissionsWithLimit(searchWord,
						0, limitX == 0 ? PriznajApplication.LIST_VIEW_LOADING_LIMIT_Y : limitX);
				break;
			default:
				admissions = new ArrayList<CoreAdmission>();
				break;
		}
		if (admissions.isEmpty()) {
			return new ArrayList<CoreElement>(1);
		} else {
			limitX = admissions.size();
			final List<CoreElement> elements =
					new ArrayList<CoreElement>(admissions.size() + (admissions.size() / PriznajApplication.LIST_VIEW_AD_OCCURRENCE));
			for (final CoreAdmission ca : admissions) {
				elements.add(new CoreElement(ca));
			}
			insertAdsToSearchElementList(elements);
			return elements;
		}
	}

	/**
	 * taham dalsich limitY podla limitX hodnoty
	 * 
	 * @param admissionType
	 * @param searchWord
	 * @return
	 */
	private static final List<CoreElement> getAnotherAdmissions(final AdmissionType admissionType, final String searchWord) {
		final List<CoreAdmission> admissions;
		switch (admissionType.getIdentifier()) {
			case 0:
				admissions = DataAccessObjectImpl.INSTACE.searchGBAdmissionsWithLimit(searchWord,
						limitX, PriznajApplication.LIST_VIEW_LOADING_LIMIT_Y);
				break;
			case 1:
				admissions = DataAccessObjectImpl.INSTACE.searchHSAdmissionsWithLimit(searchWord,
						limitX, PriznajApplication.LIST_VIEW_LOADING_LIMIT_Y);
				break;
			case 2:
				admissions = DataAccessObjectImpl.INSTACE.searchUNIAdmissionsWithLimit(searchWord,
						limitX, PriznajApplication.LIST_VIEW_LOADING_LIMIT_Y);
				break;
			default:
				admissions = new ArrayList<CoreAdmission>();
				break;
		}
		if (admissions.isEmpty()) {
			return new ArrayList<CoreElement>(1);
		} else {
			limitX += admissions.size();
			final List<CoreElement> elements =
					new ArrayList<CoreElement>(admissions.size() + (admissions.size() / PriznajApplication.LIST_VIEW_AD_OCCURRENCE));
			for (final CoreAdmission ca : admissions) {
				elements.add(new CoreElement(ca));
			}
			insertAdsToSearchElementList(elements);
			return elements;
		}
	}

	/**
	 * Povklada do listu elementov reklamu podla AdDroid.adOccurrence
	 * 
	 * @param elements
	 */
	private static final void insertAdsToSearchElementList(final List<CoreElement> elements) {
		final int addCount = elements.size() / PriznajApplication.LIST_VIEW_AD_OCCURRENCE;
		for (int pos = 1; pos <= addCount; pos++) {
			elements.add(pos * PriznajApplication.LIST_VIEW_AD_OCCURRENCE, new CoreElement(AdDroid.generateRandomAdDrawableResource()));
		}
	}
}
