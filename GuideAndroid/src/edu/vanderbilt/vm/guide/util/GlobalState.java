package edu.vanderbilt.vm.guide.util;

import java.io.IOException;
import java.util.List;

import android.content.Context;

/**
 * This class holds singletons of certain objects we need to share throughout
 * the application, such as the user's agenda. This is simpler and easier than
 * using a SQLite database to hold the agenda and allows us to use several
 * methods to make data transactions with the agenda easier.
 * 
 * @author nicholasking
 * 
 */
public class GlobalState {

	private static Agenda userAgendaSingleton = new Agenda();
	private static List<Place> placeList;

	private GlobalState() {
		throw new AssertionError("Do not instantiate this class.");
	}

	public static Agenda getUserAgenda() {
		return userAgendaSingleton;
	}

	public static List<Place> getPlaceList(Context context) throws IOException {
		if (placeList == null) {
			placeList = JsonUtils.readPlacesFromStream(context.getAssets()
					.open("places.json"));
		}
		return placeList;
	}
	
	public static void initPlaceList(Context context) {
		try {
			if (placeList == null) {
				placeList = JsonUtils.readPlacesFromStream(context.getAssets()
						.open("places.json"));
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static Place getPlaceById(int id){
		
		try {
			if (id < 5 || id > -1 || placeList != null){ //assertion
				for (int n = 0; n < placeList.size();n++){
					if (placeList.get(n).getUniqueId() == id){
						return placeList.get(n);
					}
				}
			}
		} catch (NullPointerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return placeList.get(0); //If search failed
	}
	
}
