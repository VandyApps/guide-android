package edu.vanderbilt.vm.guide.db;

import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import edu.vanderbilt.vm.guide.util.JsonUtils;

/**
 * Manages the creation of the application's SQLite database.  Refer to the
 * Android developer documentation at 
 * http://developer.android.com/guide/topics/data/data-storage.html#db
 * for more information on how this works.
 * 
 * Please do not use your IDE's auto format feature on this class.  It'll
 * probably screw up the nice indenting for the create table statements.
 * @author nicholasking
 *
 */
public class GuideDBOpenHelper extends SQLiteOpenHelper implements GuideDBConstants {
	
	// These Strings are SQL commands to create the Places and Tours tables
	private static final String PLACE_DB_CREATE = 
			"CREATE TABLE " + PlaceTable.PLACE_TABLE_NAME + " (" +
					PlaceTable.ID_COL + " INTEGER PRIMARY KEY, " +
					PlaceTable.NAME_COL + " TEXT, " +
					PlaceTable.CATEGORY_COL + " TEXT, " +
					PlaceTable.DESCRIPTION_COL + " TEXT, " +
					PlaceTable.HOURS_COL + " TEXT, " +
					PlaceTable.LATITUDE_COL + " FLOAT, " +
					PlaceTable.LONGITUDE_COL + " FLOAT, " +
					PlaceTable.AUDIO_LOC_COL + " TEXT, " +
					PlaceTable.IMAGE_LOC_COL + " TEXT, " +
					PlaceTable.VIDEO_LOC_COL + " TEXT);";
	
	private static final String TOUR_DB_CREATE =
			"CREATE TABLE " + TourTable.TOUR_TABLE_NAME + " (" +
					TourTable.ID_COL + " INTEGER PRIMARY KEY, " +
					TourTable.NAME_COL + " TEXT, " +
					TourTable.DESCRIPTION_COL + " TEXT, " +
					TourTable.DISTANCE_COL + " TEXT, " +
					TourTable.PLACES_ON_TOUR_COL + " TEXT, " +
					TourTable.ICON_LOC_COL + " TEXT, " +
					TourTable.TIME_REQUIRED_COL + " TEXT);";
	
	private static final int DB_VERSION = 4;
	private static final Logger logger = LoggerFactory.getLogger("db.GuideDBOpenHelper");
	
	private final Context mContext;
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		// Create the Place and Tour databases
		logger.trace("Executing SQL: \n" + PLACE_DB_CREATE);
		db.execSQL(PLACE_DB_CREATE);
		logger.trace("Executing SQL: \n" + TOUR_DB_CREATE);
		db.execSQL(TOUR_DB_CREATE);
		
		logger.trace("Populating " + 
				GuideDBConstants.PlaceTable.PLACE_TABLE_NAME + 
				" table from JSON file " + GuideDBConstants.PLACES_JSON_NAME);
		InputStream in = null;
		try {
			in = mContext.getAssets().open(
					GuideDBConstants.PLACES_JSON_NAME);
			JsonUtils.populateDatabaseFromInputStream(
					GuideDBConstants.PlaceTable.PLACE_TABLE_NAME, in, db);
		} catch (IOException e) {
			logger.error("Error processing file " + 
					GuideDBConstants.PLACES_JSON_NAME, e);
		} finally {
			if(in != null) {
				try {
					in.close();
				} catch (IOException e) {
					logger.error("Error closing input stream for file " 
							+ GuideDBConstants.PLACES_JSON_NAME, e);
				}
			}
		}
		
		logger.trace("Populating " + 
				GuideDBConstants.TourTable.TOUR_TABLE_NAME + 
				" table from JSON file " + GuideDBConstants.TOURS_JSON_NAME);
		in = null;
		try {
			in = mContext.getAssets().open(
					GuideDBConstants.TOURS_JSON_NAME);
			JsonUtils.populateDatabaseFromInputStream(
					GuideDBConstants.TourTable.TOUR_TABLE_NAME, in, db);
		} catch (IOException e) {
			logger.error("Error processing file " + 
					GuideDBConstants.TOURS_JSON_NAME, e);
		} finally {
			if(in != null) {
				try {
					in.close();
				} catch (IOException e) {
					logger.error("Error closing input stream for file " 
							+ GuideDBConstants.TOURS_JSON_NAME, e);
				}
			}
		}
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE " + PlaceTable.PLACE_TABLE_NAME);
		db.execSQL("DROP TABLE " + TourTable.TOUR_TABLE_NAME);
		onCreate(db);
	}
	
	public GuideDBOpenHelper(Context context) {
		super(context, DATABASE_NAME, null, DB_VERSION);
		mContext = context;
	}

}
