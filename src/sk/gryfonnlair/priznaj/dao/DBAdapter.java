package sk.gryfonnlair.priznaj.dao;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;


public class DBAdapter extends SQLiteOpenHelper {

	private static String DB_PATH = "/data/data/sk.gryfonnlair.priznaj/databases/";
	private static String DB_NAME = "priznaj-sk.db";
	//TODO UPDATES ak zvysim db (zmenim) tak treba zvysit cislo +1
	private static final int DB_VERSION = 1;
	private SQLiteDatabase myDataBase;
	private final Context myContext;

	private static DBAdapter myDBAdapter;

	/**
	 * Constructor Takes and keeps a reference of the passed context in order to
	 * access to the application assets and resources.
	 * 
	 * @param context
	 */
	private DBAdapter(final Context context) {
		super(context, DB_NAME, null, DB_VERSION);
		myContext = context;
	}

	/**
	 * getting Instance
	 * 
	 * @param context
	 * @return DBAdapter
	 */
	public static synchronized DBAdapter getDBAdapterInstance(final Context context) {
		if (myDBAdapter == null) {
			myDBAdapter = new DBAdapter(context);
		}
		return myDBAdapter;
	}

	/**
	 * Creates an empty database on the system and rewrites it with your own
	 * database.
	 **/
	public void createDataBase() throws IOException {
		final boolean dbExist = checkDataBase();
		if (dbExist) {
			// do nothing - database already exist
		} else {
			// By calling following method
			// 1) an empty database will be created into
			// the default system path of your application
			// 2) than we overwrite that database with our database.
			getReadableDatabase();
			try {
				copyDataBase();
			} catch (final IOException e) {
				throw new Error("Error copying database");
			}
		}
	}

	/**
	 * Check if the database already exist to avoid re-copying the file each
	 * time you open the application.
	 * 
	 * @return true if it exists, false if it doesn't
	 */
	private boolean checkDataBase() {
		// SQLiteDatabase checkdb = null;
		boolean checkdb = false;
		try {
			final String myPath = DB_PATH + DB_NAME;
			final File dbfile = new File(myPath);
			// checkdb =
			// SQLiteDatabase.openDatabase(myPath,null,SQLiteDatabase.OPEN_READWRITE);
			checkdb = dbfile.exists();
		} catch (final SQLiteException e) {
			System.out.println("Database doesn't exist");
		}

		return checkdb;
	}

	/**
	 * Copies your database from your local assets-folder to the just created
	 * empty database in the system folder, from where it can be accessed and
	 * handled. This is done by transfering bytestream.
	 * */
	private void copyDataBase() throws IOException {
		// Open your local db as the input stream
		final InputStream myInput = myContext.getAssets().open(DB_NAME);
		// Path to the just created empty db
		final String outFileName = DB_PATH + DB_NAME;
		// Open the empty db as the output stream
		// OutputStream myOutput = new FileOutputStream(
		// "/data/data/sk.diffusion.android.augusto/databases/rosto.db");
		final OutputStream myOutput = new FileOutputStream(outFileName);
		// transfer bytes from the inputfile to the outputfile
		final byte[] buffer = new byte[1024];
		int length;
		while ((length = myInput.read(buffer)) > 0) {
			myOutput.write(buffer, 0, length);
		}
		// Close the streams
		myOutput.flush();
		myOutput.close();
		myInput.close();
	}

	/**
	 * Open the database
	 * 
	 * @throws SQLException
	 */
	public void openDataBase() throws SQLException {
		final String myPath = DB_PATH + DB_NAME;
		myDataBase = SQLiteDatabase.openDatabase(myPath, null,
				SQLiteDatabase.OPEN_READWRITE);
		if (!myDataBase.isReadOnly()) {
			myDataBase.execSQL("PRAGMA foreign_keys=ON;");
		}
	}

	/**
	 * Close the database if exist
	 */
	@Override
	public synchronized void close() {
		if (myDataBase != null) {
			myDataBase.close();
		}
		super.close();
	}

	/**
	 * Call on creating data base for example for creating tables at run time
	 */
	@Override
	public void onCreate(final SQLiteDatabase db) {
	}

	/**
	 * can used for drop tables then call onCreate(db) function to create tables
	 * again - upgrade
	 */
	@Override
	public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
	}

	// ----------------------- CRUD Functions ------------------------------

	/**
	 * This function used to select the records from DB.
	 * 
	 * @param tableName
	 * @param tableColumns
	 * @param whereClase
	 * @param whereArgs
	 * @param groupBy
	 * @param having
	 * @param orderBy
	 * @return A Cursor object, which is positioned before the first entry.
	 */
	public Cursor selectRecordsFromDB(final String tableName, final String[] tableColumns,
			final String whereClase, final String whereArgs[], final String groupBy,
			final String having, final String orderBy) {
		return myDataBase.query(tableName, tableColumns, whereClase, whereArgs,
				groupBy, having, orderBy);
	}

	/**
	 * select records from db and return in list
	 * 
	 * @param tableName
	 * @param tableColumns
	 * @param whereClase
	 * @param whereArgs
	 * @param groupBy
	 * @param having
	 * @param orderBy
	 * @return ArrayList<ArrayList<String>>
	 */
	public ArrayList<ArrayList<String>> selectRecordsFromDBList(
			final String tableName, final String[] tableColumns, final String whereClase,
			final String whereArgs[], final String groupBy, final String having, final String orderBy) {
		final ArrayList<ArrayList<String>> retList = new ArrayList<ArrayList<String>>();
		ArrayList<String> list = new ArrayList<String>();
		final Cursor cursor = myDataBase.query(tableName, tableColumns, whereClase,
				whereArgs, groupBy, having, orderBy);
		if (cursor.moveToFirst()) {
			do {
				list = new ArrayList<String>();
				for (int i = 0; i < cursor.getColumnCount(); i++) {
					list.add(cursor.getString(i));
				}
				retList.add(list);
			} while (cursor.moveToNext());
		}
//		if (cursor != null && !cursor.isClosed()) {
		if (!cursor.isClosed()) {
			cursor.close();
		}
		return retList;

	}

	/**
	 * This function used to insert the Record in DB.
	 * 
	 * @param tableName
	 * @param nullColumnHack
	 * @param initialValues
	 * @return the row ID of the newly inserted row, or -1 if an error occurred
	 */
	public long insertRecordsInDB(final String tableName, final String nullColumnHack,
			final ContentValues initialValues) {
		return myDataBase.insert(tableName, nullColumnHack, initialValues);
	}

	/**
	 * This function used to update the Record in DB.
	 * 
	 * @param tableName
	 * @param initialValues
	 * @param whereClause
	 * @param whereArgs
	 * @return true / false on updating one or more records
	 */
	public boolean updateRecordInDB(final String tableName,
			final ContentValues initialValues, final String whereClause, final String whereArgs[]) {
		return myDataBase.update(tableName, initialValues, whereClause,
				whereArgs) > 0;
	}

	/**
	 * This function used to update the Record in DB.
	 * 
	 * @param tableName
	 * @param initialValues
	 * @param whereClause
	 * @param whereArgs
	 * @return 0 in case of failure otherwise return no of row(s) are updated
	 */
	public int updateRecordsInDB(final String tableName, final ContentValues initialValues,
			final String whereClause, final String whereArgs[]) {
		return myDataBase.update(tableName, initialValues, whereClause,
				whereArgs);
	}

	/**
	 * This function used to delete the Record in DB.
	 * 
	 * @param tableName
	 * @param whereClause
	 * @param whereArgs
	 * @return 0 in case of failure otherwise return no of row(s) are deleted.
	 */
	public int deleteRecordInDB(final String tableName, final String whereClause,
			final String[] whereArgs) {
		return myDataBase.delete(tableName, whereClause, whereArgs);
	}

	// --------------------- Select Raw Query Functions ---------------------

	/**
	 * apply raw Query
	 * 
	 * @param query
	 * @param selectionArgs
	 * @return Cursor
	 */
	public Cursor selectRecordsFromDB(final String query, final String[] selectionArgs) {
		return myDataBase.rawQuery(query, selectionArgs);
	}

	/**
	 * apply raw query and return result in list
	 * 
	 * @param query
	 * @param selectionArgs
	 * @return ArrayList<ArrayList<String>>
	 */
	public ArrayList<ArrayList<String>> selectRecordsFromDBList(final String query,
			final String[] selectionArgs) {
		final ArrayList<ArrayList<String>> retList = new ArrayList<ArrayList<String>>();
		ArrayList<String> list = new ArrayList<String>();
		final Cursor cursor = myDataBase.rawQuery(query, selectionArgs);
		if (cursor.moveToFirst()) {
			do {
				list = new ArrayList<String>();
				for (int i = 0; i < cursor.getColumnCount(); i++) {
					list.add(cursor.getString(i));
				}
				retList.add(list);
			} while (cursor.moveToNext());
		}
//		if (cursor != null && !cursor.isClosed()) {
		if (!cursor.isClosed()) {
			cursor.close();
		}
		return retList;
	}

}
