package edu.vanderbilt.vm.guide.db;

/**
 * Specifies some constants that are useful for building SQL statements
 * to interact with the backing databases
 * @author nicholasking
 *
 */
public interface GuideDBConstants {

	// Remember that all of these fields are implicitly public, static, final
	String DATABASE_NAME = "guide.db";
	
	public interface PlaceTable {
		String PLACE_TABLE_NAME = "places";
		String ID_COL = "id";
		String LATITUDE_COL = "latitude";
		String LONGITUDE_COL = "longitude";
		String IMAGE_LOC_COL = "picture_loc";
		String AUDIO_LOC_COL = "audio_loc";
		String VIDEO_LOC_COL = "video_loc";
		String NAME_COL = "name";
		String DESCRIPTION_COL = "description";
		String HOURS_COL = "hours";
		String CATEGORY_COL = "category";
	}
	
	public interface TourTable {
		String TOUR_TABLE_NAME = "tours";
		String ID_COL = "id";
		String PLACES_ON_TOUR_COL = "places_on_tour";
		String TIME_REQUIRED_COL = "time_required";
		String DISTANCE_COL = "distance";
		String NAME_COL = "name";
	}
	
}
