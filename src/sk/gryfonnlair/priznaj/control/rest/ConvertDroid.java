package sk.gryfonnlair.priznaj.control.rest;

import java.util.Collection;

import sk.gryfonnlair.priznaj.model.rest.UniversityAdmission;


/**
 * Driod na prerabku a upravu Server objekto a nase androidacke, vid priklad
 * university carovne id naservery
 * 
 * @author gryfonn
 * 
 */
public class ConvertDroid {

	/**
	 * SERVER -> LOCAL <br>
	 * Vyberiem si string univerzitu a podla nej <b>presetujem</b> property
	 * kategoria v objekte na nase lokalne id aby som nasledne mohol dany objekt
	 * vlozit do db, v pripade ze najde skola ktora neni , tak sa nastavi -1
	 * idcko
	 * 
	 * @param collection UniversityAdmission zo server
	 */
	public static void convertCollectionUniversityAdmission(final Collection<UniversityAdmission> collection) {
		//TODO UPDATES do buducna select z db
		for (final UniversityAdmission ua : collection) {
			if ("UPJŠ".equalsIgnoreCase(ua.univerzita)) {
				ua.kategoria = 1;
			} else if ("TUKE".equalsIgnoreCase(ua.univerzita)) {
				ua.kategoria = 2;
			} else if ("EUBA".equalsIgnoreCase(ua.univerzita)) {
				ua.kategoria = 3;
			} else if ("STU".equalsIgnoreCase(ua.univerzita)) {
				ua.kategoria = 4;
			} else if ("UK".equalsIgnoreCase(ua.univerzita)) {
				ua.kategoria = 5;
			} else if ("UNIPO".equalsIgnoreCase(ua.univerzita)) {
				ua.kategoria = 6;
			} else if ("UKF".equalsIgnoreCase(ua.univerzita)) {
				ua.kategoria = 7;
			} else if ("SPU".equalsIgnoreCase(ua.univerzita)) {
				ua.kategoria = 8;
			} else if ("UNIZA".equalsIgnoreCase(ua.univerzita)) {
				ua.kategoria = 9;
			} else if ("UCM".equalsIgnoreCase(ua.univerzita)) {
				ua.kategoria = 10;
			} else if ("TRUNI".equalsIgnoreCase(ua.univerzita)) {
				ua.kategoria = 11;
			} else if ("TNUNI".equalsIgnoreCase(ua.univerzita)) {
				ua.kategoria = 12;
			} else if ("UVLF".equalsIgnoreCase(ua.univerzita)) {
				ua.kategoria = 13;
			} else if ("UMB".equalsIgnoreCase(ua.univerzita)) {
				ua.kategoria = 14;
			} else if ("TUZVO".equalsIgnoreCase(ua.univerzita)) {
				ua.kategoria = 15;
			} else if ("VŠMU".equalsIgnoreCase(ua.univerzita)) {
				ua.kategoria = 16;
			} else if ("VŠVU".equalsIgnoreCase(ua.univerzita)) {
				ua.kategoria = 17;
			} else if ("AU".equalsIgnoreCase(ua.univerzita)) {
				ua.kategoria = 18;
			} else if ("KU".equalsIgnoreCase(ua.univerzita)) {
				ua.kategoria = 19;
			} else if ("UJS".equalsIgnoreCase(ua.univerzita)) {
				ua.kategoria = 20;
			}
			//ak neznama skola
			else {
				ua.kategoria = -1;
			}
		}

	}

}
