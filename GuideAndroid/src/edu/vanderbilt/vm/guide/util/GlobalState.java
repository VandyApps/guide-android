package edu.vanderbilt.vm.guide.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import edu.vanderbilt.vm.guide.annotations.NeedsTesting;
import edu.vanderbilt.vm.guide.container.Agenda;
import edu.vanderbilt.vm.guide.container.Place;
import edu.vanderbilt.vm.guide.db.GuideDBConstants;

/**
 * This class holds singletons of certain objects we need to share throughout
 * the application, such as the user's agenda. This is simpler and easier than
 * using a SQLite database to hold the agenda and allows us to use several
 * methods to make data transactions with the agenda easier.
 * 
 * @author nicholasking
 * 
 */
@NeedsTesting(lastModifiedDate = "12/22/12")
public class GlobalState {

	private static Agenda userAgendaSingleton = new Agenda();
	
	/**
	 * @deprecated The place list singleton should no longer be used.
	 * A SQLite database serves this purpose.
	 */
	@Deprecated
	private static List<Place> sPlaceList;
	
	private static Logger logger = LoggerFactory.getLogger("util.GlobalState");

	private GlobalState() {
		throw new AssertionError("Do not instantiate this class.");
	}

	public static Agenda getUserAgenda() {
		return userAgendaSingleton;
	}

	/**
	 * Returns a list of places stored in the places.json file.
	 * 
	 * @deprecated Query the places table of the SQLite database instead
	 * @param context the Context to use to open an input stream
	 * @return a list of all places in the places.json file
	 */
	@Deprecated
	public static List<Place> getPlaceList(Context context) {
		if (sPlaceList == null) {
			try {
				sPlaceList = JsonUtils.readPlacesFromInputStream(context.getAssets()
						.open(GuideDBConstants.PLACES_JSON_NAME));
			} catch (IOException e) {
				logger.error("JSON import failed", e);
			}
		}
		return sPlaceList;
	}

	/**
	 * Returns a place given the unique ID of that place
	 * @deprecated Query the places table of the SQLite database instead
	 * @param id The id of the place to find
	 * @return The place with the given id
	 */
	@Deprecated
	public static Place getPlaceById(int id) {
		if (sPlaceList == null) {
			return null;
		}

		for (int n = 0; n < sPlaceList.size(); n++) {
			if (sPlaceList.get(n).getUniqueId() == id) {
				return sPlaceList.get(n);
			}
		}

		return null; // If search failed
	}
	
	// History Singleton
	private static Agenda userHistory = new Agenda();
	
	static {
		resetHistory();
	}
	
	/**
	 * Returns the user's history.
	 * 
	 * @return user History
	 */
	public static Agenda getUserHistory() {
		return userHistory;
	}
	
	/**
	 * Add a place to history. The list is arranged most recent first
	 * 
	 * @param plc
	 */
	public static void addHistory(Place plc){
		if (userHistory.size() == 0){
			userHistory.add(plc);
		} else if (userHistory.get(0).getUniqueId() == 1000) {
			userHistory.overwrite(new Agenda());
			userHistory.add(plc);
		} else {
			userHistory.addToTop(plc);
		}
		
	}
	
	/**
	 * Empties the history.
	 */
	public static void resetHistory(){
		if (userHistory == null){
			userHistory = new Agenda();
		} else {
			userHistory.overwrite(new Agenda());
		}
		
		Place temp = (new Place.Builder())
				.setName("History is Empty")
				.setUniqueId(1000).build(); // TODO
		userHistory.add(temp);
	}
	// END History Singleton
	
	// Bitmap store
	public static ArrayList<BitmapRecord> mBitmapStore = 
			new ArrayList<BitmapRecord>();
	
	private static final int STORE_LIMIT = 10;
	
	private static class BitmapRecord {
		Bitmap mImage;
		int mPlaceId;
	}
	
	/**
	 * Downloads the image associated with a Place for you.
	 * 
	 * @param plc
	 * @return a bitmap which is associated with the Place
	 */
	public static Bitmap getBitmapForPlace(final Place plc) {
		// make sure the cache is not too big
		// discard older entry if it exceeds a limit
		if (mBitmapStore.size() > STORE_LIMIT) {
			mBitmapStore.subList(5, mBitmapStore.size());
		}
		
		// check for existing cache
		for (BitmapRecord record : mBitmapStore) {
			if (plc.getUniqueId() == record.mPlaceId) {
				logger.debug("image found in store.");
				return record.mImage;
			}
		}
		
		// cache not found. Download image and add to cache
		logger.debug("No image found in store. Downloading a new Image.");
		
		mBitmapStore.add(new BitmapRecord());
		final int i = mBitmapStore.size() - 1; // the final element's index
		
		Thread downloadImage = new Thread() {
			@Override
			public void run() {
				try {
					InputStream is = (InputStream) new URL(
							plc.getPictureLoc()).getContent();
					logger.trace("Download succeeded");
					mBitmapStore.get(i).mImage = BitmapFactory.decodeStream(is);
					mBitmapStore.get(i).mPlaceId = plc.getUniqueId();
				} catch (Exception e) {
					logger.error("Download failed", e);
					mBitmapStore.get(i).mImage = null;
				}
			}
		};
		
		downloadImage.start();
		try {
			downloadImage.join();
			return mBitmapStore.get(i).mImage;
		} catch (InterruptedException e) {
			logger.error("Download failed", e);
			return null;
		}
		
	}
	// END Bitmap store
	
	// Coordinate lookup table
	
	
	
	
}
