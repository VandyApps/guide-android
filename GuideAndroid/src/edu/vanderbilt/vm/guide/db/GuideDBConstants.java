
package edu.vanderbilt.vm.guide.db;

import android.net.Uri;

/**
 * Specifies some constants that are useful for building SQL statements to
 * interact with the backing databases
 * 
 * @author nicholasking
 */
public interface GuideDBConstants {

    // Remember that all of these fields are implicitly public, static, final

    String DATABASE_NAME = "guide.db";

    String PLACES_JSON_NAME = "places.json";

    String TOURS_JSON_NAME = "tours.json";
    
    String NODES_JSON_NAME = "nodes.json";

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

        // For use with the ContentProvider
        String PATH_SINGLE = "places/#";

        String PATH_MULTIPLE = "places";

        Uri CONTENT_URI = Uri.parse("content://" + GuideContentProvider.AUTHORITY + "/"
                + PLACE_TABLE_NAME);

        // Meta data
        String DEFAULT_ORDER = NAME_COL + " ASC";

        String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.edu.vanderbilt.vm.guide.place";

        String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.edu.vanderbilt.vm.guide.place";
    }

    public interface TourTable {
        String TOUR_TABLE_NAME = "tours";

        String ID_COL = "id";

        String PLACES_ON_TOUR_COL = "places_on_tour";

        String TIME_REQUIRED_COL = "time_required";

        String DISTANCE_COL = "distance";

        String NAME_COL = "name";

        String ICON_LOC_COL = "icon_loc";

        String DESCRIPTION_COL = "description";

        // For use with the ContentProvider
        String PATH_SINGLE = "tours/#";

        String PATH_MULTIPLE = "tours";

        Uri CONTENT_URI = Uri.parse("content://" + GuideContentProvider.AUTHORITY + "/"
                + TOUR_TABLE_NAME);

        // Meta data
        String DEFAULT_ORDER = NAME_COL + " ASC";

        String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.edu.vanderbilt.vm.guide.tour";

        String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.edu.vanderbilt.vm.guide.tour";
    }
    
    public interface NodeTable {
        String NODE_TABLE_NAME = "nodes";
        
        String ID_COL = "id";
        
        String LAT_COL = "latitude";
        
        String LON_COL = "longitude";
        
        String NEIGHBOR_COL  = "neighbor_ids";
    }

}
