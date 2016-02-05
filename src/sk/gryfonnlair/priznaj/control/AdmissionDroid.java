package sk.gryfonnlair.priznaj.control;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import sk.gryfonnlair.priznaj.PriznajApplication;
import sk.gryfonnlair.priznaj.control.activities.AdDroid;
import sk.gryfonnlair.priznaj.control.rest.ConvertDroid;
import sk.gryfonnlair.priznaj.control.rest.RestDroid;
import sk.gryfonnlair.priznaj.dao.DataAccessObjectImpl;
import sk.gryfonnlair.priznaj.model.rest.InitResult;
import sk.gryfonnlair.priznaj.model.rest.NewGBResult;
import sk.gryfonnlair.priznaj.model.rest.NewHSResult;
import sk.gryfonnlair.priznaj.model.rest.NewUniResult;
import sk.gryfonnlair.priznaj.model.specific.AdmissionType;
import sk.gryfonnlair.priznaj.model.specific.CoreAdmission;
import sk.gryfonnlair.priznaj.view.core.tools.CoreElement;
import android.util.Log;

import com.google.gson.Gson;


/**
 * Zastresuje vyber priznani a rozhoduje ci volat resty alebo lokal, pouzitie je
 * v splash na init ak ja db prazdna po prvy krat, na gtNews cize v splashi
 * dotaha vsetky nove admission a taktiez setne shared pref podla noviniek, ale
 * hlavne je pouzity ako droid pre core obrazovku
 * 
 * @author gryfonn
 * 
 */
public final class AdmissionDroid {

	public static volatile int limitX = 0;

	//SPLASH VYUZITIE
	/**
	 * Z kniznice objekt na serializaciu a deserializaciu Stringov
	 */
	private static final Gson gsonMachine = new Gson();

	/**
	 * Vytiahne priznania z kazdej tabulky mnozstvo na zklade servera
	 */
	public static final boolean initializeFirstAdmissions() {
		final String json = RestDroid.getInitAdmissions();
		if (json == null) {
			return false;
		}
		final InitResult initResult = gsonMachine.fromJson(json, InitResult.class);
		Log.d(PriznajApplication.DEBUG_TAG, "AdmissionDroid>initializeFirstAdmissions> stiahol a skonvertoval som JSON.");
		//insertovanie priznani po jednom v forku
		DataAccessObjectImpl.INSTACE.insertHSAdmissions(initResult.priznania_stredne);
		DataAccessObjectImpl.INSTACE.insertGBAdmissions(initResult.priznania2);
		ConvertDroid.convertCollectionUniversityAdmission(initResult.priznania);
		DataAccessObjectImpl.INSTACE.insertUniAdmissions(initResult.priznania);
		return true;
	};

	/**
	 * Rest call, nasetuje pocet novych prispevkou a este aj stiahne nove
	 * prispevky
	 */
	public static final String getNews() {
		final Map<String, String> maxIdsFromLocalDB = DataAccessObjectImpl.INSTACE.getNewsIds();
		if (maxIdsFromLocalDB == null) {
			return new String();
		}
		if (PriznajApplication.D) {
			Log.d(PriznajApplication.DEBUG_TAG, "AdmissionDroid> getNews: Map top IDciek: " + maxIdsFromLocalDB.toString());
		}
		final String jsonCounts = RestDroid.getNews(maxIdsFromLocalDB);

		final String jsonNewUnis = RestDroid.getNewUniAdmissions(maxIdsFromLocalDB.get("priznania"));
		final String jsonNewHSs = RestDroid.getNewHSAdmissions(maxIdsFromLocalDB.get("priznania_stredne"));
		final String jsonNewGBs = RestDroid.getNewGBAdmissions(maxIdsFromLocalDB.get("priznania2"));

		final NewUniResult uniResult = jsonNewUnis == null ? null : gsonMachine.fromJson(jsonNewUnis, NewUniResult.class);
		final NewHSResult hsResult = jsonNewHSs == null ? null : gsonMachine.fromJson(jsonNewHSs, NewHSResult.class);
		final NewGBResult gbResult = jsonNewGBs == null ? null : gsonMachine.fromJson(jsonNewGBs, NewGBResult.class);
		if (uniResult != null) {
			ConvertDroid.convertCollectionUniversityAdmission(uniResult.priznania);
			DataAccessObjectImpl.INSTACE.insertUniAdmissions(uniResult.priznania);
		}
		if (hsResult != null) {
			DataAccessObjectImpl.INSTACE.insertHSAdmissions(hsResult.priznania_stredne);
		}
		if (gbResult != null) {
			DataAccessObjectImpl.INSTACE.insertGBAdmissions(gbResult.priznania2);
		}
		return jsonCounts;
	}

	//CORE ACTIVITY VYUZITIE
	/**
	 * UPDATE priznania v admission_favorites
	 * 
	 * @param newFavoriteValue rozhoduje o vkladani alebo remove/update
	 * @param admissionType typ priznania originalneho
	 * @param admissionId id originalneho priznania
	 * @return true ak prebehlo ok vsetko
	 */
	public static final synchronized boolean changeFavoriteOnAdmission(final boolean newFavoriteValue, final AdmissionType admissionType, final int admissionId) {
		return newFavoriteValue ? addToFavorite(admissionType, admissionId) : removeFromFavorite(admissionType, admissionId);
	}

	/**
	 * pomocna pre
	 * {@link #changeFavoriteOnAdmission(boolean, AdmissionType, int)}, pridava
	 * oblubene
	 * 
	 * @param admissionType
	 * @param admissionId
	 * @return
	 */
	private static final boolean addToFavorite(final AdmissionType admissionType, final int admissionId) {
		return DataAccessObjectImpl.INSTACE.addToFavorites(admissionType, admissionId);
	}

	/**
	 * pomocna pre
	 * {@link #changeFavoriteOnAdmission(boolean, AdmissionType, int)},
	 * remove/update z
	 * favorite
	 * 
	 * @param admissionType
	 * @param admissionId
	 * @return
	 */
	private static final boolean removeFromFavorite(final AdmissionType admissionType, final int admissionId) {
		return DataAccessObjectImpl.INSTACE.removeFromFavorites(admissionType, admissionId);
	}

	public static final synchronized List<CoreElement> getAdmissions(final boolean init, final AdmissionType admissionType, final Integer drawerChildId) {
		return init ? getInitAdmissions(admissionType, drawerChildId) : getAnotherAdmissions(admissionType, drawerChildId);
	}

	private static final List<CoreElement> getInitAdmissions(final AdmissionType admissionType, final Integer drawerChildId) {
		List<CoreAdmission> admissions = new ArrayList<CoreAdmission>();
		switch (admissionType.getIdentifier()) {
			case 0:
				admissions = DataAccessObjectImpl.INSTACE.getLimitCoreGBAdmissions(0,
						limitX == 0 ? PriznajApplication.LIST_VIEW_LOADING_LIMIT_Y : limitX, drawerChildId);
				break;
			case 1:
				admissions = DataAccessObjectImpl.INSTACE.getLimitCoreHSAdmissions(0,
						limitX == 0 ? PriznajApplication.LIST_VIEW_LOADING_LIMIT_Y : limitX, drawerChildId);
				break;
			case 2:
				admissions = DataAccessObjectImpl.INSTACE.getLimitCoreUNIAdmissions(0,
						limitX == 0 ? PriznajApplication.LIST_VIEW_LOADING_LIMIT_Y : limitX, drawerChildId);
				break;
			default:
				break;
		}
		if (admissions.isEmpty()) {
			//posielam si prazdny lebo na tom stavam podmienky
			return new ArrayList<CoreElement>(1);
		} else {
			limitX = admissions.size();
			final List<CoreElement> elements = new ArrayList<CoreElement>();
			for (final CoreAdmission ca : admissions) {
				elements.add(new CoreElement(ca));
			}
			insertAdsToSearchElementList(elements);
			return elements;
		}
	}

	private static final List<CoreElement> getAnotherAdmissions(final AdmissionType admissionType, final Integer drawerChildId) {
		List<CoreAdmission> admissions = new ArrayList<CoreAdmission>();
		switch (admissionType.getIdentifier()) {
			case 0:
				admissions = DataAccessObjectImpl.INSTACE.getLimitCoreGBAdmissions(limitX, PriznajApplication.LIST_VIEW_LOADING_LIMIT_Y, drawerChildId);
				break;
			case 1:
				admissions = DataAccessObjectImpl.INSTACE.getLimitCoreHSAdmissions(limitX, PriznajApplication.LIST_VIEW_LOADING_LIMIT_Y, drawerChildId);
				break;
			case 2:
				admissions = DataAccessObjectImpl.INSTACE.getLimitCoreUNIAdmissions(limitX, PriznajApplication.LIST_VIEW_LOADING_LIMIT_Y, drawerChildId);
				break;
			default:
				break;
		}
		if (admissions.isEmpty()) {
			//posielam si prazdny lebo na tom stavam podmienky
			return new ArrayList<CoreElement>(1);
		} else {
			limitX += admissions.size();
			final List<CoreElement> elements = new ArrayList<CoreElement>();
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

	/**
	 * Vytiahne z prislusnej tabulky najstarsie id a posle ho do requestu na
	 * server response prekonvertuje a vlozi do db, SEBESTACNA metoda returnuje
	 * len kod vysledku
	 * 
	 * @param admissionType
	 * @return RestResponse
	 */
	public static final synchronized RestResponse getOlderAdmissionsFromServer(final AdmissionType admissionType) {
		final int oldestId = DataAccessObjectImpl.INSTACE.getOldestIdDromTable(admissionType);
		if (PriznajApplication.D) {
		Log.d(PriznajApplication.DEBUG_TAG, new StringBuilder("AdmissionDroid> getOlderAdmissionsFromServer: pre ")
					.append(admissionType).append(", najstarsie Id je id=").append(oldestId).toString());
		}
		if (oldestId == -1) {
			return RestResponse.HTTP_ERROR;
		}
		if (oldestId == 0) {
			if (initializeFirstAdmissions()) {
				return RestResponse.INIT_ADMISSIONS;
			} else {
				return RestResponse.HTTP_ERROR;
			}
		}
		final String jsonResponse =
				admissionType == AdmissionType.UNIVERSITY ? RestDroid.getOlderUniAdmissions(Integer.toString(oldestId)) :
						admissionType == AdmissionType.HIGH_SCHOOL ? RestDroid.getOlderHSAdmissions(Integer.toString(oldestId)) :
								RestDroid.getOlderGBAdmissions(Integer.toString(oldestId));
		if (PriznajApplication.D) {
			Log.d(PriznajApplication.DEBUG_TAG, new StringBuilder("AdmissionDroid> getOlderAdmissionsFromServer: RepsonseJSON: ")
					.append(jsonResponse == null ? "NULL" : Integer.toString(jsonResponse.length())).toString());
		}
		if (jsonResponse == null) {
			return RestResponse.HTTP_ERROR;
		}
		if (admissionType == AdmissionType.UNIVERSITY) {
			final NewUniResult uniResult = gsonMachine.fromJson(jsonResponse, NewUniResult.class);
			if (uniResult == null || uniResult.priznania == null || uniResult.priznania.isEmpty()) {
				return RestResponse.NO_ADMISSIONS;
			}
			ConvertDroid.convertCollectionUniversityAdmission(uniResult.priznania);
			DataAccessObjectImpl.INSTACE.insertUniAdmissions(uniResult.priznania);
			if (uniResult.priznania.size() < RestDroid.GET_OLDER_COUNT) {
				return RestResponse.LESS_ADMISSIONS;
			} else {
				return RestResponse.FULL_ADMISSIONS;
			}

		} else if (admissionType == AdmissionType.HIGH_SCHOOL) {
			final NewHSResult hsResult = gsonMachine.fromJson(jsonResponse, NewHSResult.class);
			if (hsResult == null || hsResult.priznania_stredne == null || hsResult.priznania_stredne.isEmpty()) {
				return RestResponse.NO_ADMISSIONS;
			}
			DataAccessObjectImpl.INSTACE.insertHSAdmissions(hsResult.priznania_stredne);
			if (hsResult.priznania_stredne.size() < RestDroid.GET_OLDER_COUNT) {
				return RestResponse.LESS_ADMISSIONS;
			} else {
				return RestResponse.FULL_ADMISSIONS;
			}
		} else {
			final NewGBResult gbResult = gsonMachine.fromJson(jsonResponse, NewGBResult.class);
			if (gbResult == null || gbResult.priznania2 == null || gbResult.priznania2.isEmpty()) {
				return RestResponse.NO_ADMISSIONS;
			}
			DataAccessObjectImpl.INSTACE.insertGBAdmissions(gbResult.priznania2);
			if (gbResult.priznania2.size() < RestDroid.GET_OLDER_COUNT) {
				return RestResponse.LESS_ADMISSIONS;
			} else {
				return RestResponse.FULL_ADMISSIONS;
			}
		}
	}

	public static enum RestResponse {
		/**
		 * Chyba v spojeni
		 */
		HTTP_ERROR,
		/**
		 * nic neprislo JSON null
		 */
		NO_ADMISSIONS,
		/**
		 * prislo plnych 200 kusov
		 */
		FULL_ADMISSIONS,
		/**
		 * prislo menej <200 a uz asi nic neni
		 */
		LESS_ADMISSIONS,
		/**
		 * ked sa nezapol net a stahovalo sa cez footer dodatocne
		 */
		INIT_ADMISSIONS
	}
}
