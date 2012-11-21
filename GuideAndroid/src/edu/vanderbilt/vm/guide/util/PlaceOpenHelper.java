package edu.vanderbilt.vm.guide.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class PlaceOpenHelper extends SQLiteOpenHelper {

	private static final String PLACE_DB_NAME = "places";
	private static final int DB_VERSION = 1;
	
	
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}
	
	public PlaceOpenHelper(Context context) {
		super(context, PLACE_DB_NAME, null, DB_VERSION);
	}

}
