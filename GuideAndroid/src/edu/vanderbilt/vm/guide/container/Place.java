package edu.vanderbilt.vm.guide.container;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import edu.vanderbilt.vm.guide.db.GuideDBConstants;

/**
 * In-memory representation of a place. Use this class when you want to deal
 * with a nicer interface than SQLite cursors. This class is also useful for
 * data transactions between other Guide container classes.
 * 
 * @author nicholasking
 * 
 */
public class Place {

	private static final Logger logger = LoggerFactory
			.getLogger("container.Place");
	private static final int DEFAULT_ID = -1;

	private double mLatitude;
	private double mLongitude;
	private String mImageLoc;
	private String mAudioLoc;
	private String mVideoLoc;
	private String mName;
	private String mDescription;
	private String mHours;
	private String mCategory;
	private int mUniqueId;

	/* package */Place() {
	}

	private Place(Place.Builder builder) {
		if (builder.mUniqueId == DEFAULT_ID) {
			throw new IllegalArgumentException(
					"Unique ID must not be default value (" + DEFAULT_ID + ")");
		}
		mLatitude = builder.mLatitude;
		mLongitude = builder.mLongitude;
		mImageLoc = builder.mImageLoc;
		mAudioLoc = builder.mAudioLoc;
		mVideoLoc = builder.mVideoLoc;
		mName = builder.mName;
		mDescription = builder.mDescription;
		mHours = builder.mHours;
		mUniqueId = builder.mUniqueId;
		mCategory = builder.mCategory;
	}

	/**
	 * A class for creating Place objects. You must use this class in order to
	 * create a place. Chain setter method calls where appropriate. Call build()
	 * when you have finished setting all of the fields. Any unset fields will
	 * be given a default value.
	 * <p/>
	 * <b>Note:</b> You must set a uniqueId for every place. Failing to do so
	 * will result in an exception.
	 * 
	 * @author nicholasking
	 * 
	 */
	public static class Builder {
		private double mLatitude = 0;
		private double mLongitude = 0;
		private String mImageLoc;
		private String mAudioLoc;
		private String mVideoLoc;
		private String mName;
		private String mDescription;
		private String mHours;
		private String mCategory;
		private int mUniqueId = DEFAULT_ID;

		public Builder() {
		}

		public Builder setLatitude(double lat) {
			mLatitude = lat;
			return this;
		}

		public Builder setLongitude(double lon) {
			mLongitude = lon;
			return this;
		}

		public Builder setImageLoc(String imageLoc) {
			mImageLoc = imageLoc;
			return this;
		}

		public Builder setAudioLoc(String audioLoc) {
			mAudioLoc = audioLoc;
			return this;
		}

		public Builder setVideoLoc(String videoLoc) {
			mVideoLoc = videoLoc;
			return this;
		}

		public Builder setName(String name) {
			mName = name;
			return this;
		}

		public Builder setDescription(String desc) {
			mDescription = desc;
			return this;
		}

		public Builder setCategory(String category) {
			mCategory = category;
			return this;
		}

		public Builder setHours(String hours) {
			mHours = hours;
			return this;
		}

		public Builder setUniqueId(int uniqueId) {
			mUniqueId = uniqueId;
			return this;
		}

		public Place build() {
			return new Place(this);
		}

	}

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
	 * places retrieved.  <b>Use this method instead of getPlaceById if you need
	 * to get more than one place at a time.</b>  Simply calling getPlaceById over
	 * and over will hammer the database with a bunch of unnecessary queries and
	 * result in poor performance.
	 * 
	 * @param uniqueIds The ids to query the database for
	 * @param db The database to query
	 * @return An array of the places retrieved.  Some places may be null if the
	 * they were not found.
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
		for(int i : uniqueIds) {
			query.append(i);
			query.append(',');
		}
		query.deleteCharAt(query.length()-1); // delete the trailing comma
		query.append(')');
		
		Cursor cursor = db.rawQuery(query.toString(), null);
		if (!cursor.moveToFirst()) {
			logger.warn("Got an empty cursor");
			return null;
		}
		
		Place[] places = new Place[uniqueIds.length];
		int i = 0;
		
		while(!cursor.isAfterLast()) {
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
				bldr.setCategory(cursor.getString(index));
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

	public double getLatitude() {
		return mLatitude;
	}

	public double getLongitude() {
		return mLongitude;
	}

	public String getPictureLoc() {
		return mImageLoc;
	}

	public String getAudioLoc() {
		return mAudioLoc;
	}

	public String getVideoLoc() {
		return mVideoLoc;
	}

	public String getName() {
		return mName;
	}

	public String getDescription() {
		return mDescription;
	}

	public String getHours() {
		return mHours;
	}

	public String getCategory() {
		return mCategory;
	}

	public int getUniqueId() {
		return mUniqueId;
	}

	public int hashCode() {
		// We just return the unique ID for efficiency,
		// hoping that the client has actually made the ID unique
		return mUniqueId;
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof Place)) {
			return false;
		}
		Place otherPlace = (Place) other;
		// We shouldn't have to compare anything other than the
		// unique ID.
		return this.mUniqueId == otherPlace.mUniqueId;
	}

}
