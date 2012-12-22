package edu.vanderbilt.vm.guide.db;

import edu.vanderbilt.vm.guide.annotations.NeedsTesting;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

@NeedsTesting(lastModifiedDate = "12/22/12")
public class GuideContentProvider extends ContentProvider implements
		GuideDBConstants {

	public static final String AUTHORITY = "edu.vanderbilt.vm.guide.provider";

	private static final UriMatcher MATCHER = new UriMatcher(
			UriMatcher.NO_MATCH);

	// Matcher return codes
	private static final int SINGLE_PLACE = 1;
	private static final int MULTIPLE_PLACE = 2;
	private static final int SINGLE_TOUR = 3;
	private static final int MULTIPLE_TOUR = 4;

	static {
		MATCHER.addURI(AUTHORITY, PlaceTable.PATH_SINGLE, SINGLE_PLACE);
		MATCHER.addURI(AUTHORITY, PlaceTable.PATH_MULTIPLE, MULTIPLE_PLACE);
		MATCHER.addURI(AUTHORITY, TourTable.PATH_SINGLE, SINGLE_TOUR);
		MATCHER.addURI(AUTHORITY, TourTable.PATH_MULTIPLE, MULTIPLE_TOUR);
	}

	private GuideDBOpenHelper mHelper;

	@Override
	public boolean onCreate() {
		mHelper = new GuideDBOpenHelper(getContext());
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {

		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		String orderBy = null;
		switch (MATCHER.match(uri)) {
		case SINGLE_PLACE:
			qb.appendWhere(PlaceTable.ID_COL + "="
					+ uri.getPathSegments().get(1));
		case MULTIPLE_PLACE:
			qb.setTables(PlaceTable.PLACE_TABLE_NAME);
			orderBy = PlaceTable.DEFAULT_ORDER;
			break;
		case SINGLE_TOUR:
			qb.appendWhere(TourTable.ID_COL + "="
					+ uri.getPathSegments().get(1));
		case MULTIPLE_TOUR:
			qb.setTables(TourTable.TOUR_TABLE_NAME);
			orderBy = TourTable.DEFAULT_ORDER;
			break;
		default:
			throw new IllegalArgumentException("Invalid Uri: " + uri);
		}

		SQLiteDatabase db = mHelper.getReadableDatabase();
		if (!TextUtils.isEmpty(sortOrder)) {
			orderBy = sortOrder;
		}

		Cursor cursor = qb.query(db, projection, selection, selectionArgs,
				null, null, orderBy);
		cursor.setNotificationUri(getContext().getContentResolver(), uri);
		return cursor;
	}

	@Override
	public String getType(Uri uri) {
		switch (MATCHER.match(uri)) {
		case SINGLE_PLACE:
			return PlaceTable.CONTENT_ITEM_TYPE;
		case MULTIPLE_PLACE:
			return PlaceTable.CONTENT_TYPE;
		case SINGLE_TOUR:
			return TourTable.CONTENT_ITEM_TYPE;
		case MULTIPLE_TOUR:
			return TourTable.CONTENT_TYPE;
		default:
			throw new IllegalArgumentException("Invalid Uri: " + uri);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		if (!values.containsKey(PlaceTable.ID_COL)
				&& !values.containsKey(TourTable.ID_COL)) {
			throw new SQLException("You must specify an ID");
		}
		String tableName;
		String idColName;
		Uri contentUri;
		
		switch (MATCHER.match(uri)) {
		case MULTIPLE_PLACE:
			tableName = PlaceTable.PLACE_TABLE_NAME;
			idColName = PlaceTable.ID_COL;
			contentUri = PlaceTable.CONTENT_URI;
			break;
		case MULTIPLE_TOUR:
			tableName = TourTable.TOUR_TABLE_NAME;
			idColName = TourTable.ID_COL;
			contentUri = TourTable.CONTENT_URI;
			break;
		default:
			throw new SQLException("Invalid Uri: " + uri);
		}

		int id = values.getAsInteger(idColName);
		SQLiteDatabase db = mHelper.getWritableDatabase();
		Cursor c = db.query(tableName, null, idColName + "=" + id, null, null,
				null, null);
		
		// This will be true if the cursor is not empty
		if (c.moveToFirst()) {
			throw new SQLException("There is already a tuple in the "
					+ tableName + " table with the id " + id);
		}
		
		long rowId = db.insert(tableName, null, values);
		if(rowId > 0) {
			Uri insertedUri = ContentUris.withAppendedId(contentUri, rowId);
			getContext().getContentResolver().notifyChange(insertedUri, null);
			return insertedUri;
		} else {
			throw new SQLException("Failed to insert row into " + tableName);
		}
	}

	@Override
	public int delete(Uri uri, String where, String[] whereArgs) {
		SQLiteDatabase db = mHelper.getWritableDatabase();
		int count;
		switch(MATCHER.match(uri)) {
		case SINGLE_PLACE: {
			String rowId = uri.getPathSegments().get(1);
			String whereClause = PlaceTable.ID_COL + "=" + rowId;
			if(!TextUtils.isEmpty(where)) {
				whereClause += " AND (" + where + ")";
			}
			count = db.delete(PlaceTable.PLACE_TABLE_NAME, whereClause, whereArgs);
			break;
		}
		case MULTIPLE_PLACE:
			count = db.delete(PlaceTable.PLACE_TABLE_NAME, where, whereArgs);
			break;
		case SINGLE_TOUR: {
			String rowId = uri.getPathSegments().get(1);
			String whereClause = TourTable.ID_COL + "=" + rowId;
			if(!TextUtils.isEmpty(where)) {
				whereClause += " AND (" + where + ")";
			}
			count = db.delete(TourTable.TOUR_TABLE_NAME, whereClause, whereArgs);
			break;
		}
		case MULTIPLE_TOUR:
			count = db.delete(TourTable.TOUR_TABLE_NAME, where, whereArgs);
			break;
		default:
			throw new IllegalArgumentException("Invalid Uri: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	@Override
	public int update(Uri uri, ContentValues values, String where,
			String[] whereArgs) {
		SQLiteDatabase db = mHelper.getWritableDatabase();
		int count;
		
		switch(MATCHER.match(uri)) {
		case SINGLE_PLACE: {
			String rowId = uri.getPathSegments().get(1);
			String whereClause = PlaceTable.ID_COL + "=" + rowId;
			if(!TextUtils.isEmpty(where)) {
				whereClause += " AND (" + where + ")";
			}
			count = db.update(PlaceTable.PLACE_TABLE_NAME, values, whereClause, whereArgs);
			break;
		}
		case MULTIPLE_PLACE:
			count = db.update(PlaceTable.PLACE_TABLE_NAME, values, where, whereArgs);
			break;
		case SINGLE_TOUR: {
			String rowId = uri.getPathSegments().get(1);
			String whereClause = TourTable.ID_COL + "=" + rowId;
			if(!TextUtils.isEmpty(where)) {
				whereClause += " AND (" + where + ")";
			}
			count = db.update(TourTable.TOUR_TABLE_NAME, values, whereClause, whereArgs);
			break;
		}
		case MULTIPLE_TOUR:
			count = db.update(TourTable.TOUR_TABLE_NAME, values, where, whereArgs);
			break;
		default:
			throw new IllegalArgumentException("Invalid Uri: " + uri);
		}
		
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

}
