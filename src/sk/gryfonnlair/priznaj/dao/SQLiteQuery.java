package sk.gryfonnlair.priznaj.dao;

/**
 * Trieda pre vsetky query na databazu, pisat kazdu premennu ako static final
 * setri to pamet je to android convenction<br>
 * <b>!!BACHA NA FORMATOVANIE</b>
 * <p>
 * <b>Vo vsetkych query pisat vsetky potrebne nazvy columnov aby nebol neskor
 * problem pri update</b>
 * <p>
 * <b>javadoc pre prehlad v DataAccessObjectImpl ked haldam</b>
 * 
 * @author Martin Kiss - DIFFUSION
 * 
 */
public class SQLiteQuery {
	/**
	 * SELECT COUNT(*) FROM admission_universities;
	 */
	public static final String CHECK_IF_EMPTY_TABLES = "SELECT COUNT(*) FROM admission_universities;";

	// NavigationDrawer

	/**
	 * Select NavigationDrawer items
	 */
	public static final String SELECT_NAVIGATION_DRAWER_ITEMS = " SELECT navigation_drawer_children.group_id, navigation_drawer_children.child_id, "
			+ " COALESCE(universities_grouped.name, region.name) AS child_name "
			+ " FROM navigation_drawer_children "
			+ " LEFT JOIN universities_grouped ON navigation_drawer_children.child_type = 1 AND navigation_drawer_children.child_id = universities_grouped._id "
			+ " LEFT JOIN region ON navigation_drawer_children.child_type = 2 AND navigation_drawer_children.child_id = region._id; ";

	//REST
	/**
	 * treba appendnut tabulku
	 * SELECT MAX(_id) FROM
	 */
	public static String GET_TOP_ID_FROM = "SELECT MAX(_id) AS pocet FROM ";
	/**
	 * INSERT INTO admission_girls_boys (_id,gender_id,text,datetime,favorite) VALUES (?,?,?,?,0);
	 */
	public static final String INSERT_GENDER_ADMISSION =
			"INSERT OR REPLACE INTO admission_girls_boys (_id,gender_id,text,datetime,favorite) VALUES (?,?,?,?,0);";
	/**
	 * INSERT INTO admission_high_schools (_id,high_school_id,text,datetime,favorite) VALUES (?,?,?,?,0);
	 */
	public static final String INSERT_HIGHSCHOOL_ADMISSION =
			"INSERT OR REPLACE INTO admission_high_schools (_id,high_school_id,text,datetime,favorite) VALUES (?,?,?,?,0);";
	/**
	 * INSERT INTO admission_universities (_id,university_id,text,datetime,favorite) VALUES (?,?,?,?,0);
	 */
	public static final String INSERT_UNIVERSITY_ADMISSION =
			"INSERT OR REPLACE INTO admission_universities (_id,university_id,text,datetime,favorite) VALUES (?,?,?,?,0);";

	// SEND
	/**
	 * SELECT _id,name FROM gender ORDER BY name COLLATE NOCASE ASC;
	 */
	public static final String SELECT_ALL_GENDERS = "SELECT _id,name FROM gender ORDER BY name COLLATE NOCASE ASC;";
	/**
	 * SELECT _id,name,img_resource FROM universities ORDER BY name COLLATE
	 * NOCASE ASC;
	 */
	public static final String SELECT_ALL_UNIVERSITIES = "SELECT _id,name,img_resource FROM universities ORDER BY name COLLATE NOCASE ASC;";
	/**
	 * SELECT _id,name FROM region ORDER BY name COLLATE NOCASE ASC;
	 */
	public static final String SELECT_ALL_REGIONS = "SELECT _id,name FROM region ORDER BY name COLLATE NOCASE ASC;";
	/**
	 * SELECT _id,region_id,name FROM county WHERE region_id = ? ORDER BY name
	 * COLLATE NOCASE ASC;
	 */
	public static final String SELECT_ALL_COUNTIES_BY_REGION = "SELECT _id,region_id,name FROM county WHERE region_id = ? ORDER BY name COLLATE NOCASE ASC;";
	/**
	 * SELECT _id,county_id,name FROM high_schools WHERE county_id = ? ORDER BY
	 * name COLLATE NOCASE ASC;
	 */
	public static final String SELECT_ALL_HIGHSCHOOLS_BY_COUNTY = "SELECT _id,county_id,name FROM high_schools WHERE county_id = ? ORDER BY name COLLATE NOCASE ASC;";

	// FAVORITE
	/**
	 * SELECT _id,origin_id,type,text,category FROM admission_favorites ORDER BY
	 * datetime DESC LIMIT ?,?;
	 */
	public static final String SELECT_LIMIT_FAVORITE_ADMISSIONS = "SELECT _id,origin_id,type,text,category FROM admission_favorites ORDER BY datetime DESC LIMIT ?,?;";
	/**
	 * UPDATE admission_girls_boys SET favorite=0 WHERE _id IN <br>
	 * <b>treba doplnit appednut idcka a setnut tabulku pri pouzivani</b>
	 */
	public static final String UPDATE_TABLE_GB_COLUMN_FAVORITE_TO_ZERO = "UPDATE admission_girls_boys SET favorite=0 WHERE _id IN";
	/**
	 * UPDATE admission_high_schools SET favorite=0 WHERE _id IN<br>
	 * <b>treba doplnit appednut idcka a setnut tabulku pri pouzivani</b>
	 */
	public static final String UPDATE_TABLE_HS_COLUMN_FAVORITE_TO_ZERO = "UPDATE admission_high_schools SET favorite=0 WHERE _id IN";
	/**
	 * UPDATE admission_universities SET favorite=0 WHERE _id IN <br>
	 * <b>treba doplnit appednut idcka a setnut tabulku pri pouzivani</b>
	 */
	public static final String UPDATE_TABLE_UNI_COLUMN_FAVORITE_TO_ZERO = "UPDATE admission_universities SET favorite=0 WHERE _id IN";
	/**
	 * DELETE FROM admission_favorites WHERE _id IN <br>
	 * <b>treba doplnit appednut idcka pri pouzivani</b>
	 */
	public static final String DELETE_FAVORITE_ADMISSIONS = "DELETE FROM admission_favorites WHERE _id IN";

	// SEARCH
	/**
	 * SELECT _id,gender_id,text,favorite FROM admission_girls_boys WHERE text
	 * LIKE \"%?%\" ORDER BY datetime DESC LIMIT ?,?;
	 */
	public static final String SEARCH_GB_ADMISSIONS_WITH_LIMIT = "SELECT _id,gender_id,text,favorite FROM admission_girls_boys WHERE text LIKE \"%?%\" ORDER BY datetime DESC LIMIT ?,?;";
	/**
	 * SELECT a._id,h.name AS school_name,c.name AS county_name,r.name AS region_name,a.text,a.favorite FROM admission_high_schools AS a LEFT JOIN high_schools AS h ON a.high_school_id=h._id LEFT JOIN county AS c ON h.county_id=c._id LEFT JOIN region AS r ON c.region_id=r._id WHERE a.text LIKE \"%?%\" ORDER BY a.datetime DESC LIMIT ?,?;
	 */
	public static final String SEARCH_HS_ADMISSIONS_WITH_LIMIT =
			"SELECT a._id,h.name AS school_name,c.name AS county_name,r.name AS region_name,a.text,a.favorite FROM admission_high_schools AS a LEFT JOIN high_schools AS h ON a.high_school_id=h._id LEFT JOIN county AS c ON h.county_id=c._id LEFT JOIN region AS r ON c.region_id=r._id WHERE a.text LIKE \"%?%\" ORDER BY a.datetime DESC LIMIT ?,?;";
	/**
	 * SELECT a._id,u.name AS university_name,a.text,a.favorite FROM
	 * admission_universities AS a LEFT JOIN universities AS u ON
	 * a.university_id=u._id WHERE a.text LIKE \"%?%\" ORDER BY a.datetime DESC
	 * LIMIT ?,?;
	 */
	public static final String SEARCH_UNI_ADMISSIONS_WITH_LIMIT = "SELECT a._id,u.name AS university_name,a.text,a.favorite FROM admission_universities AS a LEFT JOIN universities AS u ON a.university_id=u._id WHERE a.text LIKE \"%?%\" ORDER BY a.datetime DESC LIMIT ?,?;";
	/**
	 * UPDATE # SET favorite=? WHERE _id=?;
	 * <p>
	 * <b>treba # repace ako string za nazov tabulky</b>
	 */
	public static final String UPDATE_FAVORITE_COLUMN = "UPDATE # SET favorite=? WHERE _id=?;";
	/**
	 * DELETE FROM admission_favorites WHERE origin_id=? AND type=?
	 */
	public static final String DELETE_FAVORITE_ADMISSION = "DELETE FROM admission_favorites WHERE origin_id=? AND type=?;";
	/**
	 * INSERT INTO admission_favorites (origin_id,type,text,datetime,category)
	 * VALUES (?,?,?,datetime('NOW'),?);
	 */
	public static final String ADD_FAVORITE_ADMISSION = "INSERT INTO admission_favorites (origin_id,type,text,datetime,category) VALUES (?,?,?,datetime('NOW'),?);";
	// partial seleckty pre nasetovanie stringu categoryText do novovzniknuteho
	// favoriteAdmission
	/**
	 * SELECT gender_id,text FROM admission_girls_boys WHERE _id=?;
	 */
	public static final String FAVORITE_PARTIAL_GET_GB = "SELECT gender_id,text FROM admission_girls_boys WHERE _id=?;";
	/**
	 * SELECT h.name AS school_name,a.text AS text FROM admission_high_schools
	 * AS a LEFT JOIN high_schools AS h ON a.high_school_id=h._id WHERE a._id=?;
	 */
	public static final String FAVORITE_PARTIAL_GET_HS = "SELECT h.name AS school_name,a.text AS text FROM admission_high_schools AS a LEFT JOIN high_schools AS h ON a.high_school_id=h._id WHERE a._id=?;";
	/**
	 * SELECT u.name AS university_name,a.text AS text FROM
	 * admission_universities AS a LEFT JOIN universities AS u ON
	 * a.university_id=u._id WHERE a._id=?;
	 */
	public static final String FAVORITE_PARTIAL_GET_UNI = "SELECT u.name AS university_name,a.text AS text FROM admission_universities AS a LEFT JOIN universities AS u ON a.university_id=u._id WHERE a._id=?;";


	//CORE
	public static final String SELECT_LIMIT_CORE_GB_ADMISSIONS =
			"SELECT _id,gender_id,text,favorite FROM admission_girls_boys " +
					"ORDER BY datetime DESC LIMIT ?,?;";
	public static final String SELECT_LIMIT_SPECIFIC_CORE_GB_ADMISSIONS =
			"SELECT _id,gender_id,text,favorite FROM admission_girls_boys " +
					"WHERE gender_id=? ORDER BY datetime DESC LIMIT ?,?;";
	public static final String SELECT_LIMIT_CORE_HS_ADMISSIONS =
			"SELECT a._id AS id,h.name AS school_name,c.name AS county_name,r.name AS region_name,a.text,a.favorite " +
			"FROM admission_high_schools AS a LEFT JOIN high_schools AS h ON a.high_school_id=h._id LEFT JOIN county AS c ON h.county_id=c._id LEFT JOIN region AS r ON c.region_id=r._id " +
			"ORDER BY a.datetime DESC LIMIT ?,?;";
	public static final String SELECT_LIMIT_SPECIFIC_CORE_HS_ADMISSIONS =
			"SELECT a._id AS id,h.name AS school_name,c.name AS county_name,r.name AS region_name,a.text,a.favorite " +
			"FROM admission_high_schools AS a LEFT JOIN high_schools AS h ON a.high_school_id=h._id LEFT JOIN county AS c ON h.county_id=c._id LEFT JOIN region AS r ON c.region_id=r._id " +
					"WHERE r._id=? ORDER BY a.datetime DESC LIMIT ?,?;";
	public static final String SELECT_LIMIT_CORE_UNI_ADMISSIONS =
			"SELECT a._id AS id,u.name AS university_name,a.text,a.favorite FROM admission_universities AS a LEFT JOIN universities AS u ON a.university_id=u._id " +
					"ORDER BY a.datetime DESC LIMIT ?,?;";
	public static final String SELECT_LIMIT_SPECIFIC_CORE_UNI_ADMISSIONS =
			"SELECT a._id AS id,u.name AS university_name,a.text,a.favorite FROM admission_universities AS a LEFT JOIN universities AS u ON a.university_id=u._id " +
					"WHERE a.university_id IN ";
	public static final String SELECT_LIMIT_SPECIFIC_CORE_UNI_ADMISSIONS_POSTFIX = " ORDER BY a.datetime DESC LIMIT ?,?;";
	public static final String SELECT_MIN_ID_FROM_TABLE = "SELECT MIN(_id) AS oldest FROM ";
}
