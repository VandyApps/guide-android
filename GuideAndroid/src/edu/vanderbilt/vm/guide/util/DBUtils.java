package edu.vanderbilt.vm.guide.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import edu.vanderbilt.vm.guide.annotations.NeedsTesting;
import edu.vanderbilt.vm.guide.container.Agenda;
import edu.vanderbilt.vm.guide.container.Place;
import edu.vanderbilt.vm.guide.container.Tour;
import edu.vanderbilt.vm.guide.db.GuideDBConstants;
import edu.vanderbilt.vm.guide.db.GuideDBConstants.TourTable;

/**
 * Static helper methods to make querying the database easier
 * 
 * @author nick
 * 
 */
@NeedsTesting(lastModifiedDate = "12/22/12")
public class DBUtils {

	private static final Logger logger = LoggerFactory
			.getLogger("util.DBUtils");

	/**
	 * Queries db for the place with the given uniqueId and creates a Place
	 * object to hold the result of the query.
	 * 
	 * @param uniqueId
	 *            The unique id of the place
	 * @param db
	 *            The database to query
	 * @return The Place with the given uniqueId
	 */
	public static Place getPlaceById(int uniqueId, SQLiteDatabase db) {
		Cursor cursor = db.query(GuideDBConstants.PlaceTable.PLACE_TABLE_NAME,
				null, GuideDBConstants.PlaceTable.ID_COL + "=" + uniqueId,
				null, null, null, null);

		if (!cursor.moveToFirst()) {
			logger.warn("Got an empty cursor");
			return null;
		}

		Place place = getPlaceFromCursor(cursor);

		if (place == null) {
			logger.warn("Could not find place with id {}", uniqueId);
		}
		return place;
	}

	/**
	 * Queries db for all of the given place ids and returns an array of the
	 * places retrieved. <b>Use this method instead of getPlaceById if you need
	 * to get more than one place at a time.</b> Simply calling getPlaceById
	 * over and over will hammer the database with a bunch of unnecessary
	 * queries and result in poor performance.
	 * 
	 * @param uniqueIds
	 *            The ids to query the database for
	 * @param db
	 *            The database to query
	 * @return An array of the places retrieved. Some places may be null if the
	 *         they were not found.
	 */
	public static Place[] getPlaceArrayById(int[] uniqueIds, SQLiteDatabase db) {
		if (uniqueIds == null || uniqueIds.length == 0) {
			logger.warn("Got a bad unique ID array");
			throw new IllegalArgumentException(
					"You must give a non-null, non-empty array");
		}

		StringBuilder query = new StringBuilder("SELECT * FROM "
				+ GuideDBConstants.PlaceTable.PLACE_TABLE_NAME + " WHERE "
				+ GuideDBConstants.PlaceTable.ID_COL + " IN (");
		for (int i : uniqueIds) {
			query.append(i);
			query.append(',');
		}
		query.deleteCharAt(query.length() - 1); // delete the trailing comma
		query.append(')');

		Cursor cursor = db.rawQuery(query.toString(), null);
		if (!cursor.moveToFirst()) {
			logger.warn("Got an empty cursor");
			return null;
		}

		Place[] places = new Place[uniqueIds.length];
		int i = 0;

		while (!cursor.isAfterLast()) {
			places[i] = getPlaceFromCursor(cursor);
			cursor.moveToNext();
			i++;
		}

		return places;
	}

	/**
	 * Creates a Place object from a cursor. The cursor should have come from a
	 * query to the places table in the sqlite database. This method will use
	 * the cursor at its current position.
	 * 
	 * @param cursor
	 *            The cursor to use to create the place
	 * @return The place created with the data in the cursor
	 */
	public static Place getPlaceFromCursor(Cursor cursor) {
		Place.Builder bldr = new Place.Builder();
		try {
			int index = cursor
					.getColumnIndex(GuideDBConstants.PlaceTable.ID_COL);
			if (index != -1) {
				bldr.setUniqueId(cursor.getInt(index));
			} else {
				// This should never happen. We should always get an ID column
				// back. If it does happen, something went wrong, so we want
				// to abort and log an error message
				logger.error("Got a cursor with no ID column!");
				return null;
			}

			index = cursor
					.getColumnIndex(GuideDBConstants.PlaceTable.AUDIO_LOC_COL);
			if (index != -1) {
				bldr.setAudioLoc(cursor.getString(index));
			}

			index = cursor
					.getColumnIndex(GuideDBConstants.PlaceTable.CATEGORY_COL);
			if (index != -1) {
				bldr.addCategory(cursor.getString(index));
			}

			index = cursor
					.getColumnIndex(GuideDBConstants.PlaceTable.DESCRIPTION_COL);
			if (index != -1) {
				bldr.setDescription(cursor.getString(index));
			}

			index = cursor
					.getColumnIndex(GuideDBConstants.PlaceTable.HOURS_COL);
			if (index != -1) {
				bldr.setHours(cursor.getString(index));
			}

			index = cursor
					.getColumnIndex(GuideDBConstants.PlaceTable.IMAGE_LOC_COL);
			if (index != -1) {
				bldr.setImageLoc(cursor.getString(index));
			}

			index = cursor
					.getColumnIndex(GuideDBConstants.PlaceTable.LATITUDE_COL);
			if (index != -1) {
				bldr.setLatitude(cursor.getDouble(index));
			}

			index = cursor
					.getColumnIndex(GuideDBConstants.PlaceTable.LONGITUDE_COL);
			if (index != -1) {
				bldr.setLongitude(cursor.getDouble(index));
			}

			index = cursor.getColumnIndex(GuideDBConstants.PlaceTable.NAME_COL);
			if (index != -1) {
				bldr.setName(cursor.getString(index));
			}

			index = cursor
					.getColumnIndex(GuideDBConstants.PlaceTable.VIDEO_LOC_COL);
			if (index != -1) {
				bldr.setVideoLoc(cursor.getString(index));
			}

		} catch (Exception e) {
			logger.error("Caught exception while trying to "
					+ "create place from a cursor: ", e);
			return null;
		}

		return bldr.build();
	}

	/**
	 * Creates a Tour object from a cursor containing a query to the Tour table.
	 * Uses the Cursor at its current position.
	 * <p/>
	 * Precondition: cursor must contain an id column
	 * 
	 * @param cursor
	 *            The cursor to use
	 * @param db
	 *            The database to inflate the Places on the tour from
	 * @return a Tour containing the attributes in the cursor
	 */
	@NeedsTesting(lastModifiedDate = "12/23/12")
	public static Tour getTourFromCursor(Cursor cursor, SQLiteDatabase db) {
		Tour.Builder bldr = new Tour.Builder();
		int index = cursor.getColumnIndex(TourTable.ID_COL);
		if (index == -1) {
			throw new SQLiteException("Your cursor must contain an id column");
		}
		int tourId = cursor.getInt(index);
		bldr.setUniqueId(tourId);

		index = cursor.getColumnIndex(TourTable.PLACES_ON_TOUR_COL);
		if (index != -1) {
			String placesOnTour = cursor.getString(index);
			String[] placeIdStrings = placesOnTour.split(",");
			int[] placeIdInts = new int[placeIdStrings.length];
			{
				int i = 0;
				try {
					for (; i < placeIdStrings.length; i++) {
						placeIdInts[i] = Integer.parseInt(placeIdStrings[i]);
					}
				} catch (NumberFormatException e) {
					logger.error(
							"A place ID stored in the cursor of tour with id: "
									+ tourId
									+ " is not formatted as an integer", e);
					logger.error("String that caused error: {}",
							placeIdStrings[i]);
					return null;
				}
			}
			Place[] placeArr = getPlaceArrayById(placeIdInts, db);
			Agenda tourAgenda = new Agenda();
			for (Place place : placeArr) {
				tourAgenda.add(place);
			}
			bldr.setAgenda(tourAgenda);
		}

		index = cursor.getColumnIndex(TourTable.DESCRIPTION_COL);
		if (index != -1) {
			String description = cursor.getString(index);
			bldr.setDescription(description);
		}

		index = cursor.getColumnIndex(TourTable.DISTANCE_COL);
		if (index != -1) {
			String distance = cursor.getString(index);
			bldr.setDistance(distance);
		}

		index = cursor.getColumnIndex(TourTable.ICON_LOC_COL);
		if (index != -1) {
			String iconLoc = cursor.getString(index);
			bldr.setIconLoc(iconLoc);
		}

		index = cursor.getColumnIndex(TourTable.NAME_COL);
		if (index != -1) {
			String name = cursor.getString(index);
			bldr.setName(name);
		}

		index = cursor.getColumnIndex(TourTable.TIME_REQUIRED_COL);
		if (index != -1) {
			String timeReq = cursor.getString(index);
			bldr.setTimeReq(timeReq);
		}

		return bldr.build();
	}

	/**
	 * Convenience method to query the places table of the database for all
	 * places. Ask for only the columns needed; passing null will return all
	 * columns, which will most likely just waste memory.
	 * 
	 * @param columns
	 *            The columns to query for
	 * @param db
	 *            The database to query
	 * @return A Cursor of call places in the database with the given columns
	 */
	public static Cursor getAllPlaces(String[] columns, SQLiteDatabase db) {
		return db.query(GuideDBConstants.PlaceTable.PLACE_TABLE_NAME, columns,
				null, null, null, null, GuideDBConstants.PlaceTable.NAME_COL);
	}

}
