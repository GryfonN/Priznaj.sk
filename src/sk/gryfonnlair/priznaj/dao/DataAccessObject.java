package sk.gryfonnlair.priznaj.dao;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sk.gryfonnlair.priznaj.model.County;
import sk.gryfonnlair.priznaj.model.Gender;
import sk.gryfonnlair.priznaj.model.HighSchool;
import sk.gryfonnlair.priznaj.model.Region;
import sk.gryfonnlair.priznaj.model.University;
import sk.gryfonnlair.priznaj.model.rest.GenderAdmission;
import sk.gryfonnlair.priznaj.model.rest.HighSchoolAdmission;
import sk.gryfonnlair.priznaj.model.rest.UniversityAdmission;
import sk.gryfonnlair.priznaj.model.specific.AdmissionType;
import sk.gryfonnlair.priznaj.model.specific.CoreAdmission;
import sk.gryfonnlair.priznaj.model.specific.FavoriteAdmission;
import sk.gryfonnlair.priznaj.model.specific.NavigationDrawerChildItem;


/**
 * 
 * @author Martin Kiss - Diffusion
 * 
 */
public interface DataAccessObject {

	HashMap<String, List<NavigationDrawerChildItem>> getNavigationDrawerChildren();

	//REST
	/**
	 * Vrati dvojice pre get_news rest sluzbu
	 * 
	 * @return
	 */
	Map<String, String> getNewsIds();

	/**
	 * <b>POZOR PROBLEM</b> treba presetovat int kategoria v objekte na nase id
	 * skoly, kedze objekt na kategoria carovne id a univerzita ma text, takze
	 * podla univerzity nasetujem id
	 * 
	 * @param admission
	 */
	void insertUniAdmissions(Collection<UniversityAdmission> admissions);

	/**
	 * Vlozi SERVER objekt na nasu db
	 * 
	 * @param admission
	 */
	void insertGBAdmissions(Collection<GenderAdmission> admissions);

	/**
	 * Vlozi SERVER objekt na nasu db
	 * 
	 * @param admission
	 */
	void insertHSAdmissions(Collection<HighSchoolAdmission> admissions);

	//SEND
	/**
	 * Select uplne vsetkych krajov
	 * 
	 * @return
	 */
	List<Region> getAllRegions();

	/**
	 * Select okresov na zaklade id kraja
	 * 
	 * @param region_id id kraja
	 * @return
	 */
	List<County> getCountiesByRegion(int region_id);

	/**
	 * Select Strednych skol na zaklade id okresu
	 * 
	 * @return
	 */
	List<HighSchool> getHighSchoolsByCounty(int county_id);

	/**
	 * Select uplne vsetkych pohlavi (aj ked su len dva)
	 * 
	 * @return
	 */
	List<Gender> getAllGenders();

	/**
	 * Select uplne vsetkych univerzit
	 * 
	 * @return
	 */
	List<University> getAllUniversities();

	//FAVORITE
	/**
	 * Select dalsich priznani, zalezi podla limitX hodnoty, limitY bude
	 * nastavena cez FavoriteDroid <br>
	 * <p>
	 * <i> SELECT * FROM `your_table` LIMIT 0, 10<br>
	 * This will display the first 10 results from the database.<br>
	 * SELECT * FROM `your_table` LIMIT 5, 5<br>
	 * This will show records 6, 7, 8, 9, and 10</i>
	 * 
	 * @param limitX prva hodnota limit klauzuli
	 * @return List<FavoriteAdmission>
	 */
	List<FavoriteAdmission> getLimitFavoriteAdmissions(int limitX, final int limitY);

	/**
	 * <b>check validnych argumentov v controleri</b>
	 * Vymaze favorite, update nad tromi tabulkami + delete nad favorite,
	 * Transakcia
	 * 
	 * @param genderIds
	 * @param highschoolIds
	 * @param universityIds
	 * @param favoriteIds
	 */
	boolean removeFavorites(Integer[] genderIds, Integer[] highschoolIds, Integer[] universityIds, Integer[] favoriteIds);

	//SEARCH
	List<CoreAdmission> searchGBAdmissionsWithLimit(String searchWord, int limitX, final int limitY);

	List<CoreAdmission> searchHSAdmissionsWithLimit(String searchWord, int limitX, final int limitY);

	List<CoreAdmission> searchUNIAdmissionsWithLimit(String searchWord, int limitX, final int limitY);

	/**
	 * prida jeden do favorite
	 * 
	 * @param admissionType
	 * @param admissionId
	 * @return
	 */
	boolean addToFavorites(AdmissionType admissionType, int admissionId);

	/**
	 * remove jeden z favorite
	 * 
	 * @param admissionType
	 * @param admissionId
	 * @return
	 */
	boolean removeFromFavorites(AdmissionType admissionType, int admissionId);
	
	//CORE
	List<CoreAdmission> getLimitCoreGBAdmissions(int limitX, final int limitY, Integer category);

	List<CoreAdmission> getLimitCoreHSAdmissions(int limitX, final int limitY, Integer category);

	List<CoreAdmission> getLimitCoreUNIAdmissions(int limitX, final int limitY, Integer category);
	
	int getOldestIdDromTable(AdmissionType tableType);
}
