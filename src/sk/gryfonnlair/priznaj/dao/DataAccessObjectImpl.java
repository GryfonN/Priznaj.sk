package sk.gryfonnlair.priznaj.dao;

import static sk.gryfonnlair.priznaj.view.core.TransformUtils.getGroupNameById;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sk.gryfonnlair.priznaj.PriznajApplication;
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
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;


public enum DataAccessObjectImpl implements DataAccessObject {

	INSTACE;

	private final Context context = PriznajApplication.getContext();
	private DBAdapter dbAdapter;

	public synchronized void createDatabase() {
		dbAdapter = DBAdapter.getDBAdapterInstance(context);
		try {
			dbAdapter.createDataBase();
		} catch (final IOException e) {
			e.printStackTrace();
		}
		if (dbAdapter != null) {
			dbAdapter.close();
		}
	}

	/**
	 * Checkne na tabulke admission_universities ci je prazdna, boolean pre
	 * splashscreen
	 * 
	 * @return null ak error
	 */
	public Boolean isEmtpy() {
		Cursor cursor = null;
		try {
			dbAdapter = DBAdapter.getDBAdapterInstance(context);
			dbAdapter.openDataBase();
			cursor = dbAdapter.getWritableDatabase().rawQuery(SQLiteQuery.CHECK_IF_EMPTY_TABLES, null);

			if (cursor != null) {
				cursor.moveToFirst();
				if (cursor.getInt(0) > 0) {
					return false;
				}
				else {
					return true;
				}
			}
		} catch (final SQLException sqlException) {
			if (PriznajApplication.D) {
				Log.e(DataAccessObjectImpl.class.getName(), "Exception: " + sqlException.toString());
			}
			return null;
		} finally {
			if (dbAdapter != null) {
				dbAdapter.close();
			}
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}
		return null;
	}

	@Override
	public synchronized Map<String, String> getNewsIds() {
		final Map<String, String> result = new HashMap<String, String>();
		try {
			dbAdapter = DBAdapter.getDBAdapterInstance(context);
			dbAdapter.openDataBase();
			dbAdapter.getWritableDatabase().beginTransaction();

			final Cursor c1 = dbAdapter.getWritableDatabase().rawQuery(SQLiteQuery.GET_TOP_ID_FROM + "admission_universities;", new String[] {});
			c1.moveToFirst();
			result.put("priznania", c1.getString(c1.getColumnIndex("pocet")));
			c1.close();

			final Cursor c2 = dbAdapter.getWritableDatabase().rawQuery(SQLiteQuery.GET_TOP_ID_FROM + "admission_girls_boys;", new String[] {});
			c2.moveToFirst();
			result.put("priznania2", c2.getString(c2.getColumnIndex("pocet")));
			c2.close();

			final Cursor c3 = dbAdapter.getWritableDatabase().rawQuery(SQLiteQuery.GET_TOP_ID_FROM + "admission_high_schools;", new String[] {});
			c3.moveToFirst();
			result.put("priznania_stredne", c3.getString(c3.getColumnIndex("pocet")));
			c3.close();

			dbAdapter.getWritableDatabase().setTransactionSuccessful();
		} catch (final Exception e) {
			if (PriznajApplication.D) {
				Log.e(DataAccessObjectImpl.class.getName(), "DataAccessObjectImpl>getNewsIds> Problem: " + e.toString());
			}
			return null;
		} finally {
			if (dbAdapter.getWritableDatabase() != null) {
				dbAdapter.getWritableDatabase().endTransaction();
			}
			if (dbAdapter != null) {
				dbAdapter.close();
			}
		}
		return result;
	}

	@Override
	public synchronized void insertUniAdmissions(final Collection<UniversityAdmission> admissions) {
		dbAdapter = DBAdapter.getDBAdapterInstance(context);
		SQLiteStatement statement = null;
		try {
			dbAdapter.openDataBase();
			dbAdapter.getWritableDatabase().beginTransaction();
			statement = dbAdapter.getWritableDatabase().compileStatement(SQLiteQuery.INSERT_UNIVERSITY_ADMISSION);
			for (final UniversityAdmission ua : admissions) {
				//ak kategoira -1 = covertDroid nevedel prekonvertovat = neznama univerzita
				if (ua.kategoria == -1) {
					continue;
				}
				statement.bindDouble(1, ua.id);
				statement.bindDouble(2, ua.kategoria);
				statement.bindString(3, ua.text);
				statement.bindLong(4, ua.cas);
				statement.executeInsert();
				statement.clearBindings();
			}
			dbAdapter.getWritableDatabase().setTransactionSuccessful();
			dbAdapter.getWritableDatabase().endTransaction();
		} catch (final Exception e) {
			if (PriznajApplication.D) {
				Log.e(PriznajApplication.DEBUG_TAG, "DataAccessObjectImpl>insertUniAdmission = exception:" + e.getMessage());
			}
		} finally {
			if (statement != null) {
				statement.close();
			}
			if (dbAdapter != null) {
				dbAdapter.close();
			}
		}
		if (PriznajApplication.D) {
			Log.d(PriznajApplication.DEBUG_TAG, "DataAccessObjectImpl>insertUniAdms count=" + Integer.toString(admissions != null ? admissions.size() : 0));
		}
	}

	@Override
	public synchronized void insertGBAdmissions(final Collection<GenderAdmission> admissions) {
		dbAdapter = DBAdapter.getDBAdapterInstance(context);
		SQLiteStatement statement = null;
		try {
			dbAdapter.openDataBase();
			dbAdapter.getWritableDatabase().beginTransaction();
			statement = dbAdapter.getWritableDatabase().compileStatement(SQLiteQuery.INSERT_GENDER_ADMISSION);
			for (final GenderAdmission ga : admissions) {
				statement.bindDouble(1, ga.id);
				statement.bindDouble(2, ga.kategoria);
				statement.bindString(3, ga.text);
				statement.bindLong(4, ga.cas);
				statement.executeInsert();
				statement.clearBindings();
			}
			dbAdapter.getWritableDatabase().setTransactionSuccessful();
			dbAdapter.getWritableDatabase().endTransaction();
		} catch (final Exception e) {
			if (PriznajApplication.D) {
				Log.e(PriznajApplication.DEBUG_TAG, "DataAccessObjectImpl>insertUniAdmission = exception:" + e.getMessage());
			}
		} finally {
			if (statement != null) {
				statement.close();
			}
			if (dbAdapter != null) {
				dbAdapter.close();
			}
		}
		if (PriznajApplication.D) {
			Log.d(PriznajApplication.DEBUG_TAG, "DataAccessObjectImpl>insertGBAdms count=" + Integer.toString(admissions != null ? admissions.size() : 0));
		}
	}

	@Override
	public synchronized void insertHSAdmissions(final Collection<HighSchoolAdmission> admissions) {
		dbAdapter = DBAdapter.getDBAdapterInstance(context);
		SQLiteStatement statement = null;
		try {
			dbAdapter.openDataBase();
			dbAdapter.getWritableDatabase().beginTransaction();
			statement = dbAdapter.getWritableDatabase().compileStatement(SQLiteQuery.INSERT_HIGHSCHOOL_ADMISSION);
			for (final HighSchoolAdmission ha : admissions) {
				statement.bindDouble(1, ha.id);
				statement.bindDouble(2, ha.skola);
				statement.bindString(3, ha.text);
				statement.bindLong(4, ha.cas);
				statement.executeInsert();
				statement.clearBindings();
			}
			dbAdapter.getWritableDatabase().setTransactionSuccessful();
			dbAdapter.getWritableDatabase().endTransaction();
		} catch (final Exception e) {
			if (PriznajApplication.D) {
				Log.e(PriznajApplication.DEBUG_TAG, "DataAccessObjectImpl>insertUniAdmission = exception:" + e.getMessage());
			}
		} finally {
			if (statement != null) {
				statement.close();
			}
			if (dbAdapter != null) {
				dbAdapter.close();
			}
		}
		if (PriznajApplication.D) {
			Log.d(PriznajApplication.DEBUG_TAG, "DataAccessObjectImpl>insertHSAdms count=" + Integer.toString(admissions != null ? admissions.size() : 0));
		}
	}

	@Override
	public synchronized HashMap<String, List<NavigationDrawerChildItem>> getNavigationDrawerChildren() {
		Cursor cursor = null;
		final HashMap<String, List<NavigationDrawerChildItem>> chilrenMultimap = new HashMap<String, List<NavigationDrawerChildItem>>(1);
		try {
			dbAdapter = DBAdapter.getDBAdapterInstance(context);
			dbAdapter.openDataBase();
			cursor = dbAdapter.getWritableDatabase().rawQuery(SQLiteQuery.SELECT_NAVIGATION_DRAWER_ITEMS, null);

			while (cursor.moveToNext()) {

				final int groupId = cursor.getInt(cursor.getColumnIndex("group_id"));
				final int childId = cursor.getInt(cursor.getColumnIndex("child_id"));
				final String childName = cursor.getString(cursor.getColumnIndex("child_name"));
				final String groupName = getGroupNameById(groupId);

				List<NavigationDrawerChildItem> tempList = chilrenMultimap.get(groupName);
				if (tempList == null) {
					chilrenMultimap.put(groupName, new ArrayList<NavigationDrawerChildItem>());
					tempList = chilrenMultimap.get(groupName);
				}
				tempList.add(new NavigationDrawerChildItem(childId, childName));
			}

		} catch (final SQLException sqlException) {
			if (PriznajApplication.D) {
				Log.e(DataAccessObjectImpl.class.getName(), "Exception: " + sqlException.toString());
			}
		} finally {
			if (dbAdapter != null) {
				dbAdapter.close();
			}
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}
		return chilrenMultimap;
	}

	@Override
	public synchronized List<Region> getAllRegions() {
		Cursor cursor = null;
		final List<Region> regionList = new ArrayList<Region>(1);
		try {
			dbAdapter = DBAdapter.getDBAdapterInstance(context);
			dbAdapter.openDataBase();
			cursor = dbAdapter.getWritableDatabase().rawQuery(SQLiteQuery.SELECT_ALL_REGIONS, null);

			while (cursor.moveToNext()) {
				final int regionId = cursor.getInt(cursor.getColumnIndex("_id"));
				final String regionName = cursor.getString(cursor.getColumnIndex("name"));
				regionList.add(new Region(regionId, regionName));
			}

		} catch (final SQLException sqlException) {
			if (PriznajApplication.D) {
				Log.e(DataAccessObjectImpl.class.getName(), "Exception: " + sqlException.toString());
			}
		} finally {
			if (dbAdapter != null) {
				dbAdapter.close();
			}
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}

		return regionList;
	}

	@Override
	public synchronized List<County> getCountiesByRegion(final int region_id) {
		Cursor cursor = null;
		final List<County> countyList = new ArrayList<County>(1);
		try {
			dbAdapter = DBAdapter.getDBAdapterInstance(context);
			dbAdapter.openDataBase();
			cursor = dbAdapter.getWritableDatabase().rawQuery(
					SQLiteQuery.SELECT_ALL_COUNTIES_BY_REGION,
					new String[] { Integer.toString(region_id) });

			while (cursor.moveToNext()) {
				final int countyId = cursor.getInt(cursor.getColumnIndex("_id"));
				final String countyName = cursor.getString(cursor.getColumnIndex("name"));
				final int regionId = cursor.getInt(cursor.getColumnIndex("region_id"));
				countyList.add(new County(countyId, countyName, regionId));
			}

		} catch (final SQLException sqlException) {
			if (PriznajApplication.D) {
				Log.e(DataAccessObjectImpl.class.getName(), "Exception: " + sqlException.toString());
			}
		} finally {
			if (dbAdapter != null) {
				dbAdapter.close();
			}
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}

		return countyList;
	}

	@Override
	public synchronized List<HighSchool> getHighSchoolsByCounty(final int county_id) {
		Cursor cursor = null;
		final List<HighSchool> highSchoolList = new ArrayList<HighSchool>(1);
		try {
			dbAdapter = DBAdapter.getDBAdapterInstance(context);
			dbAdapter.openDataBase();
			cursor = dbAdapter.getWritableDatabase().rawQuery(
					SQLiteQuery.SELECT_ALL_HIGHSCHOOLS_BY_COUNTY,
					new String[] { Integer.toString(county_id) });

			while (cursor.moveToNext()) {
				final int highSchoolId = cursor.getInt(cursor.getColumnIndex("_id"));
				final String highSchoolName = cursor.getString(cursor.getColumnIndex("name"));
				final int countyId = cursor.getInt(cursor.getColumnIndex("county_id"));
				highSchoolList.add(new HighSchool(highSchoolId, highSchoolName, countyId));
			}

		} catch (final SQLException sqlException) {
			if (PriznajApplication.D) {
				Log.e(DataAccessObjectImpl.class.getName(), "Exception: " + sqlException.toString());
			}
		} finally {
			if (dbAdapter != null) {
				dbAdapter.close();
			}
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}

		return highSchoolList;
	}

	@Override
	public synchronized List<Gender> getAllGenders() {
		Cursor cursor = null;
		final List<Gender> genderList = new ArrayList<Gender>(1);
		try {
			dbAdapter = DBAdapter.getDBAdapterInstance(context);
			dbAdapter.openDataBase();
			cursor = dbAdapter.getWritableDatabase().rawQuery(SQLiteQuery.SELECT_ALL_GENDERS, null);

			while (cursor.moveToNext()) {
				final int genderId = cursor.getInt(cursor.getColumnIndex("_id"));
				final String genderName = cursor.getString(cursor.getColumnIndex("name"));
				genderList.add(new Gender(genderId, genderName));
			}

		} catch (final SQLException sqlException) {
			if (PriznajApplication.D) {
				Log.e(DataAccessObjectImpl.class.getName(), "Exception: " + sqlException.toString());
			}
		} finally {
			if (dbAdapter != null) {
				dbAdapter.close();
			}
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}

		return genderList;
	}

	@Override
	public synchronized List<University> getAllUniversities() {
		Cursor cursor = null;
		final List<University> universityList = new ArrayList<University>(1);
		try {
			dbAdapter = DBAdapter.getDBAdapterInstance(context);
			dbAdapter.openDataBase();
			cursor = dbAdapter.getWritableDatabase().rawQuery(
					SQLiteQuery.SELECT_ALL_UNIVERSITIES, null);

			while (cursor.moveToNext()) {
				final int universityId = cursor.getInt(cursor.getColumnIndex("_id"));
				final String universityName = cursor.getString(cursor.getColumnIndex("name"));
				final String universityImageResource = cursor.getString(cursor.getColumnIndex("img_resource"));
				universityList.add(new University(universityId, universityName,
						universityImageResource == null ? "" : universityImageResource));
			}

		} catch (final SQLException sqlException) {
			if (PriznajApplication.D) {
				Log.e(DataAccessObjectImpl.class.getName(), "Exception: " + sqlException.toString());
			}
		} finally {
			if (dbAdapter != null) {
				dbAdapter.close();
			}
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}
		return universityList;
	}

	@Override
	public synchronized List<FavoriteAdmission> getLimitFavoriteAdmissions(final int limitX, final int limitY) {
		Cursor cursor = null;
		final List<FavoriteAdmission> favoriteAdmissionsList = new ArrayList<FavoriteAdmission>(limitY);
		try {
			dbAdapter = DBAdapter.getDBAdapterInstance(context);
			dbAdapter.openDataBase();
			cursor = dbAdapter.getWritableDatabase().rawQuery(
					SQLiteQuery.SELECT_LIMIT_FAVORITE_ADMISSIONS,
					new String[] { Integer.toString(limitX), Integer.toString(limitY) });

			while (cursor.moveToNext()) {
				final int admissionId = cursor.getInt(cursor.getColumnIndex("_id"));
				final int originId = cursor.getInt(cursor.getColumnIndex("origin_id"));
				final int type = cursor.getInt(cursor.getColumnIndex("type"));
				final String admissionText = cursor.getString(cursor.getColumnIndex("text"));
				final String admissionCategoryText = cursor.getString(cursor.getColumnIndex("category"));

				favoriteAdmissionsList.add(
						new FavoriteAdmission(admissionId, originId, AdmissionType.fromIdentifier(type),
								admissionText, admissionCategoryText));
			}

		} catch (final SQLException sqlException) {
			if (PriznajApplication.D) {
				Log.e(DataAccessObjectImpl.class.getName(), "Exception: " + sqlException.toString());
			}
		} finally {
			if (dbAdapter != null) {
				dbAdapter.close();
			}
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}
		return favoriteAdmissionsList;
	}

	@Override
	public synchronized boolean removeFavorites(final Integer[] genderIds, final Integer[] highschoolIds,
			final Integer[] universityIds, final Integer[] favoriteIds) {
		StringBuilder updateGender = null;
		StringBuilder updateHighSchool = null;
		StringBuilder updateUniversity = null;
		StringBuilder deleteFavorite = null;

		// statement pre g/b tabulku
		if (genderIds != null) {
			updateGender = new StringBuilder(SQLiteQuery.UPDATE_TABLE_GB_COLUMN_FAVORITE_TO_ZERO);
			updateGender.append(" (");
			updateGender.append(genderIds[0].toString());
			for (int i = 1; i < genderIds.length; i++) {
				updateGender.append(",");
				updateGender.append(genderIds[i].toString());
			}
			updateGender.append(");");
		}

		// statement pre hs tabulku
		if (highschoolIds != null) {
			updateHighSchool = new StringBuilder(SQLiteQuery.UPDATE_TABLE_HS_COLUMN_FAVORITE_TO_ZERO);
			updateHighSchool.append(" (");
			updateHighSchool.append(highschoolIds[0].toString());
			for (int i = 1; i < highschoolIds.length; i++) {
				updateHighSchool.append(",");
				updateHighSchool.append(highschoolIds[i].toString());
			}
			updateHighSchool.append(");");
		}

		// statement pre uni tabulku
		if (universityIds != null) {
			updateUniversity = new StringBuilder(SQLiteQuery.UPDATE_TABLE_UNI_COLUMN_FAVORITE_TO_ZERO);
			updateUniversity.append(" (");
			updateUniversity.append(universityIds[0].toString());
			for (int i = 1; i < universityIds.length; i++) {
				updateUniversity.append(",");
				updateUniversity.append(universityIds[i].toString());
			}
			updateUniversity.append(");");
		}

		// statment pre favorite tabulku, tieto by maly vzdy prist aspon jedno
		deleteFavorite = new StringBuilder(SQLiteQuery.DELETE_FAVORITE_ADMISSIONS);
		deleteFavorite.append(" (");
		deleteFavorite.append(favoriteIds[0].toString());
		for (int i = 1; i < favoriteIds.length; i++) {
			deleteFavorite.append(",");
			deleteFavorite.append(favoriteIds[i].toString());
		}
		deleteFavorite.append(");");

		SQLiteDatabase database = null;
		try {
			dbAdapter = DBAdapter.getDBAdapterInstance(context);
			dbAdapter.openDataBase();
			database = dbAdapter.getWritableDatabase();
			database.beginTransaction();

			if (updateGender != null) {
				final Cursor c1 = database.rawQuery(updateGender.toString(),
						null);
				c1.moveToFirst();
				c1.close();
				if (PriznajApplication.D) {
					Log.d(PriznajApplication.DEBUG_TAG, "DataAccessObjectImpl>removeFavorites> " + updateGender.toString());
				}
			}
			if (updateHighSchool != null) {
				final Cursor c2 = database.rawQuery(updateHighSchool.toString(), null);
				c2.moveToFirst();
				c2.close();
				if (PriznajApplication.D) {
					Log.d(PriznajApplication.DEBUG_TAG, "DataAccessObjectImpl>removeFavorites> " + updateHighSchool.toString());
				}
			}
			if (updateUniversity != null) {
				final Cursor c3 = database.rawQuery(updateUniversity.toString(), null);
				c3.moveToFirst();
				c3.close();
				if (PriznajApplication.D) {
					Log.d(PriznajApplication.DEBUG_TAG, "DataAccessObjectImpl>removeFavorites> " + updateUniversity.toString());
				}
			}
			final Cursor c4 = database.rawQuery(deleteFavorite.toString(), null);
			c4.moveToFirst();
			c4.close();
			if (PriznajApplication.D) {
				Log.d(PriznajApplication.DEBUG_TAG, "DataAccessObjectImpl>removeFavorites> " + deleteFavorite.toString());
			}

			database.setTransactionSuccessful();
		} catch (final SQLException sqlException) {
			if (PriznajApplication.D) {
				Log.e(DataAccessObjectImpl.class.getName(), "Exception: " + sqlException.toString());
			}
			return false;
		} finally {
			if (database != null) {
				database.endTransaction();
			}
			if (dbAdapter != null) {
				dbAdapter.close();
			}
		}
		return true;
	}

	@Override
	public synchronized List<CoreAdmission> searchGBAdmissionsWithLimit(final String searchWord, final int limitX, final int limitY) {
		Cursor cursor = null;
		final List<CoreAdmission> coreAdmissionsList = new ArrayList<CoreAdmission>(limitY);
		try {
			dbAdapter = DBAdapter.getDBAdapterInstance(context);
			dbAdapter.openDataBase();
			cursor = dbAdapter.getWritableDatabase().rawQuery(
					SQLiteQuery.SEARCH_GB_ADMISSIONS_WITH_LIMIT.replace("%?%", "%" + searchWord + "%"),
					new String[] { Integer.toString(limitX), Integer.toString(limitY) });

			while (cursor.moveToNext()) {
				final int admissionId = cursor.getInt(cursor.getColumnIndex("_id"));
				final int genderId = cursor.getInt(cursor.getColumnIndex("gender_id"));
				final String admissionText = cursor.getString(cursor.getColumnIndex("text"));
				final int favorite = cursor.getInt(cursor.getColumnIndex("favorite"));

				coreAdmissionsList.add(
						new CoreAdmission(admissionId, AdmissionType.GIRLS_BOYS,
								admissionText, genderId == 1 ? "GIRLS" : "BOYS",
								favorite == 0 ? false : true));
			}

		} catch (final SQLException sqlException) {
			if (PriznajApplication.D) {
				Log.e(DataAccessObjectImpl.class.getName(), "Exception: " + sqlException.toString());
			}
		} finally {
			if (dbAdapter != null) {
				dbAdapter.close();
			}
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}
		return coreAdmissionsList;
	}

	@Override
	public synchronized List<CoreAdmission> searchHSAdmissionsWithLimit(final String searchWord, final int limitX, final int limitY) {
		Cursor cursor = null;
		final List<CoreAdmission> coreAdmissionsList = new ArrayList<CoreAdmission>(limitY);
		try {
			dbAdapter = DBAdapter.getDBAdapterInstance(context);
			dbAdapter.openDataBase();

			cursor = dbAdapter.getWritableDatabase().rawQuery(
					SQLiteQuery.SEARCH_HS_ADMISSIONS_WITH_LIMIT.replace("%?%", "%" + searchWord + "%"),
					new String[] { Integer.toString(limitX), Integer.toString(limitY) });

			while (cursor.moveToNext()) {
				final int admissionId = cursor.getInt(cursor.getColumnIndex("_id"));
				final String highSchoolName = cursor.getString(cursor.getColumnIndex("school_name"));
				final String countyName = cursor.getString(cursor.getColumnIndex("county_name"));
				final String regionName = cursor.getString(cursor.getColumnIndex("region_name"));
				final String admissionText = cursor.getString(cursor.getColumnIndex("text"));
				final int favorite = cursor.getInt(cursor.getColumnIndex("favorite"));

				coreAdmissionsList.add(
						new CoreAdmission(admissionId, AdmissionType.HIGH_SCHOOL,
								admissionText, highSchoolName + "\n okres "
										+ countyName + ", " + regionName,
								favorite == 0 ? false : true));
			}
		} catch (final SQLException sqlException) {
			if (PriznajApplication.D) {
				Log.e(DataAccessObjectImpl.class.getName(), "Exception: " + sqlException.toString());
			}
		} finally {
			if (dbAdapter != null) {
				dbAdapter.close();
			}
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}
		return coreAdmissionsList;
	}

	@Override
	public synchronized List<CoreAdmission> searchUNIAdmissionsWithLimit(final String searchWord, final int limitX, final int limitY) {
		Cursor cursor = null;
		final List<CoreAdmission> coreAdmissionsList = new ArrayList<CoreAdmission>(limitY);
		try {
			dbAdapter = DBAdapter.getDBAdapterInstance(context);
			dbAdapter.openDataBase();
			cursor = dbAdapter.getWritableDatabase().rawQuery(
					SQLiteQuery.SEARCH_UNI_ADMISSIONS_WITH_LIMIT.replace("%?%", "%" + searchWord + "%"),
					new String[] { Integer.toString(limitX), Integer.toString(limitY) });

			while (cursor.moveToNext()) {
				final int admissionId = cursor.getInt(cursor.getColumnIndex("_id"));
				final String universityName = cursor.getString(cursor.getColumnIndex("university_name"));
				final String admissionText = cursor.getString(cursor.getColumnIndex("text"));
				final int favorite = cursor.getInt(cursor.getColumnIndex("favorite"));

				coreAdmissionsList.add(
						new CoreAdmission(admissionId, AdmissionType.UNIVERSITY, admissionText, universityName, favorite == 0 ? false : true));
			}

		} catch (final SQLException sqlException) {
			if (PriznajApplication.D) {
				Log.e(DataAccessObjectImpl.class.getName(), "Exception: " + sqlException.toString());
			}
		} finally {
			if (dbAdapter != null) {
				dbAdapter.close();
			}
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}
		return coreAdmissionsList;
	}

	@Override
	public synchronized boolean addToFavorites(final AdmissionType admissionType, final int admissionId) {
		if (PriznajApplication.D) {
			Log.d(PriznajApplication.DEBUG_TAG,
					"DataAccessObjectImpl>addToFavorite> admissionType;" + admissionType + "admissionId" + admissionId);
		}
		SQLiteDatabase database = null;
		try {
			dbAdapter = DBAdapter.getDBAdapterInstance(context);
			dbAdapter.openDataBase();
			database = dbAdapter.getWritableDatabase();
			database.beginTransaction();
			// update priznania v konkretnej tabulke, favorite 1
			final Cursor c1 = database.rawQuery(
					SQLiteQuery.UPDATE_FAVORITE_COLUMN.replace("#", admissionType == AdmissionType.GIRLS_BOYS ? "admission_girls_boys"
							: admissionType == AdmissionType.HIGH_SCHOOL ? "admission_high_schools" : "admission_universities"),
					new String[] { Integer.toString(1), Integer.toString(admissionId) });
			c1.moveToFirst();
			c1.close();
			if (PriznajApplication.D) {
				Log.d(PriznajApplication.DEBUG_TAG, "DataAccessObjectImpl>addToFavorite> nastavil som admission favorite 1");
			}

			// partial get , vytiahnutie textu a catogorii podla typu a
			// zostavenie categoryTextu
			String text = "Nevydalo ....";
			String categoryText = "Chyba v DAO ;)";
			if (admissionType == AdmissionType.GIRLS_BOYS) {
				final Cursor c2 = database.rawQuery(
						SQLiteQuery.FAVORITE_PARTIAL_GET_GB,
						new String[] { Integer.toString(admissionId) });
				c2.moveToFirst();
				categoryText = c2.getInt(c2.getColumnIndex("gender_id")) == 1 ? "GIRLS" : "BOYS";
				text = c2.getString(c2.getColumnIndex("text"));
				c2.close();
			} else if (admissionType == AdmissionType.HIGH_SCHOOL) {
				final Cursor c2 = database.rawQuery(
						SQLiteQuery.FAVORITE_PARTIAL_GET_HS,
						new String[] { Integer.toString(admissionId) });
				c2.moveToFirst();
				categoryText = c2.getString(c2.getColumnIndex("school_name"));
				text = c2.getString(c2.getColumnIndex("text"));
				c2.close();
			} else if (admissionType == AdmissionType.UNIVERSITY) {
				final Cursor c2 = database.rawQuery(
						SQLiteQuery.FAVORITE_PARTIAL_GET_UNI,
						new String[] { Integer.toString(admissionId) });
				c2.moveToFirst();
				categoryText = c2.getString(c2.getColumnIndex("university_name"));
				text = c2.getString(c2.getColumnIndex("text"));
				c2.close();
			}

			// vlozenie vytvorene favoriteAdmission
			database.execSQL(
					SQLiteQuery.ADD_FAVORITE_ADMISSION,
					new String[] { Integer.toString(admissionId),
							Integer.toString(admissionType.getIdentifier()),
							text, categoryText });
			if (PriznajApplication.D) {
				Log.d(PriznajApplication.DEBUG_TAG, "DataAccessObjectImpl>addToFavorite> pridal som FavoriteAdmission.");
			}

			database.setTransactionSuccessful();
		} catch (final SQLException sqlException) {
			if (PriznajApplication.D) {
				Log.e(DataAccessObjectImpl.class.getName(), "SQLException: " + sqlException.toString());
			}
			return false;
		} catch (final Exception exception) {
			if (PriznajApplication.D) {
				Log.e(DataAccessObjectImpl.class.getName(), "Exception: " + exception.toString());
			}
			return false;
		} finally {
			if (database != null) {
				database.endTransaction();
			}
			if (dbAdapter != null) {
				dbAdapter.close();
			}
		}
		return true;
	}

	@Override
	public synchronized boolean removeFromFavorites(final AdmissionType admissionType, final int admissionId) {
		if (PriznajApplication.D) {
			Log.d(PriznajApplication.DEBUG_TAG, "DataAccessObjectImpl>removeFromFavorites> admissionType;" + admissionType + "admissionId" + admissionId);
		}
		SQLiteDatabase database = null;
		try {
			dbAdapter = DBAdapter.getDBAdapterInstance(context);
			dbAdapter.openDataBase();
			database = dbAdapter.getWritableDatabase();
			database.beginTransaction();
			// update priznania v konkretnej tabulke, favorite 0
			final Cursor c1 = database.rawQuery(
					SQLiteQuery.UPDATE_FAVORITE_COLUMN.replace("#",
							admissionType == AdmissionType.GIRLS_BOYS ? "admission_girls_boys"
									: admissionType == AdmissionType.HIGH_SCHOOL ? "admission_high_schools" : "admission_universities"),
					new String[] { Integer.toString(0), Integer.toString(admissionId) });
			c1.moveToFirst();
			c1.close();
			if (PriznajApplication.D) {
				Log.d(PriznajApplication.DEBUG_TAG, "DataAccessObjectImpl>removeFromFavorites> nastavil som admission favorite 0.");
			}

			final Cursor c2 = database.rawQuery(
					SQLiteQuery.DELETE_FAVORITE_ADMISSION,
					new String[] { Integer.toString(admissionId), Integer.toString(admissionType.getIdentifier()) });
			c2.moveToFirst();
			c2.close();
			if (PriznajApplication.D) {
				Log.d(PriznajApplication.DEBUG_TAG, "DataAccessObjectImpl>removeFromFavorites> zmazal som FavoriteAdmission.");
			}

			database.setTransactionSuccessful();
		} catch (final SQLException sqlException) {
			if (PriznajApplication.D) {
				Log.e(DataAccessObjectImpl.class.getName(), "Exception: " + sqlException.toString());
			}
			return false;
		} finally {
			if (database != null) {
				database.endTransaction();
			}
			if (dbAdapter != null) {
				dbAdapter.close();
			}
		}
		return true;
	}

	@Override
	public synchronized List<CoreAdmission> getLimitCoreGBAdmissions(final int limitX, final int limitY, final Integer drawerChildId) {
		Cursor cursor = null;
		final List<CoreAdmission> coreAdmissionsList = new ArrayList<CoreAdmission>(limitY);
		try {
			dbAdapter = DBAdapter.getDBAdapterInstance(context);
			dbAdapter.openDataBase();
			if (drawerChildId == null) {
				cursor = dbAdapter.getWritableDatabase().rawQuery(
						SQLiteQuery.SELECT_LIMIT_CORE_GB_ADMISSIONS,
						new String[] { Integer.toString(limitX), Integer.toString(limitY) });
			}
			else {
				cursor = dbAdapter.getWritableDatabase().rawQuery(
						SQLiteQuery.SELECT_LIMIT_SPECIFIC_CORE_GB_ADMISSIONS,
						new String[] { Integer.toString(drawerChildId), Integer.toString(limitX), Integer.toString(limitY) });
			}

			while (cursor.moveToNext()) {
				final int admissionId = cursor.getInt(cursor.getColumnIndex("_id"));
				final int genderId = cursor.getInt(cursor.getColumnIndex("gender_id"));
				final String admissionText = cursor.getString(cursor.getColumnIndex("text"));
				final int favorite = cursor.getInt(cursor.getColumnIndex("favorite"));

				coreAdmissionsList.add(
						new CoreAdmission(admissionId, AdmissionType.GIRLS_BOYS,
								admissionText, genderId == 1 ? "GIRLS" : "BOYS",
								favorite == 0 ? false : true));
			}

		} catch (final SQLException sqlException) {
			if (PriznajApplication.D) {
				Log.e(DataAccessObjectImpl.class.getName(), "getLimitCoreGBAdmissions SQLException: " + sqlException.getMessage());
			}
		} catch (final Exception e) {
			if (PriznajApplication.D) {
				Log.e(DataAccessObjectImpl.class.getName(), "getLimitCoreGBAdmissions Exception: " + e.getMessage());
			}
		} finally {
			if (dbAdapter != null) {
				dbAdapter.close();
			}
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}
		return coreAdmissionsList;
	}

	@Override
	public synchronized List<CoreAdmission> getLimitCoreHSAdmissions(final int limitX, final int limitY, final Integer drawerChildId) {
		Cursor cursor = null;
		final List<CoreAdmission> coreAdmissionsList = new ArrayList<CoreAdmission>(limitY);
		try {
			dbAdapter = DBAdapter.getDBAdapterInstance(context);
			dbAdapter.openDataBase();
			if (drawerChildId == null) {
				cursor = dbAdapter.getWritableDatabase().rawQuery(
						SQLiteQuery.SELECT_LIMIT_CORE_HS_ADMISSIONS,
						new String[] { Integer.toString(limitX), Integer.toString(limitY) });
			}
			else {
				cursor = dbAdapter.getWritableDatabase().rawQuery(
						SQLiteQuery.SELECT_LIMIT_SPECIFIC_CORE_HS_ADMISSIONS,
						new String[] { Integer.toString(drawerChildId), Integer.toString(limitX), Integer.toString(limitY) });
			}
			while (cursor.moveToNext()) {
				final int admissionId = cursor.getInt(cursor.getColumnIndex("id"));
				final String highSchoolName = cursor.getString(cursor.getColumnIndex("school_name"));
				final String countyName = cursor.getString(cursor.getColumnIndex("county_name"));
				final String regionName = cursor.getString(cursor.getColumnIndex("region_name"));
				final String admissionText = cursor.getString(cursor.getColumnIndex("text"));
				final int favorite = cursor.getInt(cursor.getColumnIndex("favorite"));

				coreAdmissionsList.add(
						new CoreAdmission(admissionId, AdmissionType.HIGH_SCHOOL, admissionText,
								new StringBuilder(highSchoolName).append("\n okres ").append(countyName).append(", ").append(regionName).toString(),
								favorite == 0 ? false : true));
			}

		} catch (final SQLException sqlException) {
			if (PriznajApplication.D) {
				Log.e(DataAccessObjectImpl.class.getName(), "getLimitCoreHSAdmissions SQLException: " + sqlException.getMessage());
			}
		} catch (final Exception e) {
			if (PriznajApplication.D) {
				Log.e(DataAccessObjectImpl.class.getName(), "getLimitCoreHSAdmissions Exception: " + e.getMessage());
			}
		} finally {
			if (dbAdapter != null) {
				dbAdapter.close();
			}
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}
		return coreAdmissionsList;
	}

	@Override
	public synchronized List<CoreAdmission> getLimitCoreUNIAdmissions(final int limitX, final int limitY, final Integer drawerChildId) {
		Cursor cursor = null;
		final List<CoreAdmission> coreAdmissionsList = new ArrayList<CoreAdmission>(limitY);
		try {
			dbAdapter = DBAdapter.getDBAdapterInstance(context);
			dbAdapter.openDataBase();
			if (drawerChildId == null) {
				cursor = dbAdapter.getWritableDatabase().rawQuery(
						SQLiteQuery.SELECT_LIMIT_CORE_UNI_ADMISSIONS,
						new String[] { Integer.toString(limitX), Integer.toString(limitY) });
			}
			else {
				final StringBuilder query = new StringBuilder(SQLiteQuery.SELECT_LIMIT_SPECIFIC_CORE_UNI_ADMISSIONS).append('(');
				switch (drawerChildId) {
					case 7:
						query.append("7,8");
						break;
					case 8:
						query.append("9,10");
						break;
					case 9:
						query.append("11,12");
						break;
					case 10:
						query.append("13,14,15,16,17,18,19,20");
						break;
					default:
						query.append(drawerChildId.intValue());
						break;
				}
				query.append(')').append(SQLiteQuery.SELECT_LIMIT_SPECIFIC_CORE_UNI_ADMISSIONS_POSTFIX);
				cursor = dbAdapter.getWritableDatabase().rawQuery(
						query.toString(), new String[] { Integer.toString(limitX), Integer.toString(limitY) });
			}
			while (cursor.moveToNext()) {
				final int admissionId = cursor.getInt(cursor.getColumnIndex("id"));
				final String universityName = cursor.getString(cursor.getColumnIndex("university_name"));
				final String admissionText = cursor.getString(cursor.getColumnIndex("text"));
				final int favorite = cursor.getInt(cursor.getColumnIndex("favorite"));

				coreAdmissionsList.add(
						new CoreAdmission(admissionId, AdmissionType.UNIVERSITY, admissionText, universityName,
								favorite == 0 ? false : true));
			}

		} catch (final SQLException sqlException) {
			if (PriznajApplication.D) {
				Log.e(DataAccessObjectImpl.class.getName(), "getLimitCoreUNIAdmissions SQLException: " + sqlException.getMessage());
			}
		} catch (final Exception e) {
			if (PriznajApplication.D) {
				Log.e(DataAccessObjectImpl.class.getName(), "getLimitCoreUNIAdmissions Exception: " + e.getMessage());
			}
		} finally {
			if (dbAdapter != null) {
				dbAdapter.close();
			}
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}
		return coreAdmissionsList;
	}

	@Override
	public int getOldestIdDromTable(final AdmissionType tableType) {
		Cursor cursor = null;
		final String tableName = tableType == AdmissionType.UNIVERSITY ? "admission_universities" :
				tableType == AdmissionType.HIGH_SCHOOL ? "admission_high_schools" : "admission_girls_boys";
		try {
			dbAdapter = DBAdapter.getDBAdapterInstance(context);
			dbAdapter.openDataBase();
			cursor = dbAdapter.getWritableDatabase().rawQuery(
					new StringBuilder(SQLiteQuery.SELECT_MIN_ID_FROM_TABLE).append(tableName).append(';').toString(), new String[] {});
			cursor.moveToFirst();
			final int oldestId = cursor.getInt(cursor.getColumnIndex("oldest"));
			return oldestId;
		} catch (final Exception e) {
			if (PriznajApplication.D) {
				Log.e(DataAccessObjectImpl.class.getName(), "getOldestIdDromTable Exception: " + e.getMessage());
			}
			return -1;
		} finally {
			if (dbAdapter != null) {
				dbAdapter.close();
			}
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}
	}
}
