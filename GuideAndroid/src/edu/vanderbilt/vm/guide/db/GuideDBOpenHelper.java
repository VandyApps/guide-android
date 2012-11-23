package edu.vanderbilt.vm.guide.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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
					TourTable.DISTANCE_COL + " TEXT, " +
					TourTable.PLACES_ON_TOUR_COL + " TEXT, " +
					TourTable.TIME_REQUIRED_COL + " TEXT);";
	
	private static final int DB_VERSION = 1;
	private static final Logger logger = LoggerFactory.getLogger("db.GuideDBOpenHelper");
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		// Create the Place and Tour databases
		logger.trace("Executing SQL: \n" + PLACE_DB_CREATE);
		db.execSQL(PLACE_DB_CREATE);
		logger.trace("Executing SQL: \n" + TOUR_DB_CREATE);
		db.execSQL(TOUR_DB_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Do nothing for now
	}
	
	public GuideDBOpenHelper(Context context) {
		super(context, DATABASE_NAME, null, DB_VERSION);
	}

}
