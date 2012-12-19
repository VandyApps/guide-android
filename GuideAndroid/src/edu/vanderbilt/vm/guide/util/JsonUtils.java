package edu.vanderbilt.vm.guide.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.google.gson.stream.JsonReader;

import edu.vanderbilt.vm.guide.container.Place;
import edu.vanderbilt.vm.guide.db.GuideDBConstants;
import edu.vanderbilt.vm.guide.db.GuideDBOpenHelper;
import edu.vanderbilt.vm.guide.db.GuideDBConstants.PlaceTable;

public class JsonUtils {

	private static final int BAD_ID = -1;
	private static final Logger logger = LoggerFactory
			.getLogger("util.JsonUtils");

	/**
	 * Because this is just a class for static utility methods, this class
	 * should not be instantiated.
	 */
	private JsonUtils() {
		throw new AssertionError("Do not instantiate this class.");
	}

	/**
	 * Makes a list of places from a JSON-formatted input stream.
	 * 
	 * @param in The InputStream with the JSON-formatted data
	 * @return a list of places created
	 * @throws IOException 
	 * @deprecated Use the SQLite database methods instead to read in
	 * new places from an input stream and store them in the database
	 */
	@Deprecated
	public static List<Place> readPlacesFromInputStream(InputStream in)
			throws IOException {
		JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
		List<Place> places = new ArrayList<Place>();

		reader.beginArray();
		while (reader.hasNext()) {
			places.add(readPlace(reader));
		}
		return places;
	}

	
	@Deprecated
	public static Place readPlace(JsonReader reader) throws IOException {
		Place.Builder bldr = new Place.Builder();
		reader.beginObject();
		while (reader.hasNext()) {
			String name = reader.nextName();
			if (name.equals("id")) {
				bldr.setUniqueId(reader.nextInt());
			} else if (name.equals("name")) {
				bldr.setName(reader.nextString());
			} else if (name.equals("category")) {
				bldr.setCategory(reader.nextString());
			} else if (name.equals("hours")) {
				bldr.setHours(reader.nextString());
			} else if (name.equals("placeDescription")) {
				bldr.setDescription(reader.nextString());
			} else if (name.equals("imagePath")) {
				bldr.setImageLoc(reader.nextString());
			} else if (name.equals("videoPath")) {
				bldr.setVideoLoc(reader.nextString());
			} else if (name.equals("audioPath")) {
				bldr.setAudioLoc(reader.nextString());
			} else if (name.equals("latitude")) {
				bldr.setLatitude(reader.nextDouble());
			} else if (name.equals("longitude")) {
				bldr.setLongitude(reader.nextDouble());
			} else {
				reader.skipValue();
			}
		}
		reader.endObject();
		return bldr.build();
	}

	/**
	 * Adds tuples to a SQLite database from a JSON-formatted input stream.
	 * The table name lets this method determine which type of JSON it is
	 * dealing with, so use
	 * GuideDbConstants.PlaceTable.PLACE_TABLE_NAME
	 * or
	 * GuideDBConstants.TourTable.TOUR_TABLE_NAME
	 * to inform this method of which type of JSON parsing to do.
	 * 
	 * @param tableName Name of table to populate; used to determine how the JSON
	 * is formatted
	 * @param in  The InputStream that contains the JSON to read
	 * @param db  The opened SQLiteDatabase to be populated
	 * @return The populated database (a reference to db)
	 * @throws IOException
	 */
	public static SQLiteDatabase populateDatabaseFromInputStream(
			String tableName, InputStream in, SQLiteDatabase db)
			throws IOException {
		JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));

		reader.beginArray();
		if (tableName.equals(GuideDBConstants.PlaceTable.PLACE_TABLE_NAME)) {
			while (reader.hasNext()) {
				insertPlaceTuple(db, reader);
			}
		} else if (tableName.equals(GuideDBConstants.TourTable.TOUR_TABLE_NAME)) {
			while (reader.hasNext()) {
				insertTourTuple(db, reader);
			}
		}

		return db;
	}

	private static void insertPlaceTuple(SQLiteDatabase db, JsonReader reader)
			throws IOException {
		reader.beginObject();

		int id = BAD_ID;
		ContentValues cv = new ContentValues();

		while (reader.hasNext()) {
			String propertyName = reader.nextName();
			if (propertyName.equals("id")) {
				id = reader.nextInt();
			} else if (propertyName.equals("name")) {
				cv.put(PlaceTable.NAME_COL, reader.nextString());
			} else if (propertyName.equals("category")) {
				cv.put(PlaceTable.CATEGORY_COL, reader.nextString());
			} else if (propertyName.equals("hours")) {
				cv.put(PlaceTable.HOURS_COL, reader.nextString());
			} else if (propertyName.equals("placeDescription")) {
				cv.put(PlaceTable.DESCRIPTION_COL, reader.nextString());
			} else if (propertyName.equals("imagePath")) {
				cv.put(PlaceTable.IMAGE_LOC_COL, reader.nextString());
			} else if (propertyName.equals("videoPath")) {
				cv.put(PlaceTable.VIDEO_LOC_COL, reader.nextString());
			} else if (propertyName.equals("audioPath")) {
				cv.put(PlaceTable.AUDIO_LOC_COL, reader.nextString());
			} else if (propertyName.equals("latitude")) {
				cv.put(PlaceTable.LATITUDE_COL, reader.nextDouble());
			} else if (propertyName.equals("longitude")) {
				cv.put(PlaceTable.LONGITUDE_COL, reader.nextDouble());
			} else {
				reader.skipValue();
			}
		}

		reader.endObject();

		if (id == BAD_ID) {
			logger.warn("Got a place with no ID.  Skipping.");
			return;
		}

		cv.put(PlaceTable.ID_COL, id);
		logger.trace("Inserting cv into places table: {}", cv);
		db.insert(PlaceTable.PLACE_TABLE_NAME, null, cv);
	}

	private static void insertTourTuple(SQLiteDatabase db, JsonReader reader)
			throws IOException {
		// TODO: Implement.
	}

}
