package edu.vanderbilt.vm.guide.util;

public interface GuideConstants {
	
	/*
	 * PlaceDetailInterface interface note:
	 * Call it with GuideConstant.PLACE_ID_EXTRA and put in
	 * 	the desired Place's UniqueId
	 */
	public static final String PLACE_ID_EXTRA = "placeId";
	public static final int BAD_PLACE_ID = -1;
	public static final int DEFAULT_ID = 10;
	
	/*
	 * ViewMapActivity interface note:
	 * 	To open the map centred on a single building
	 * 	>> intent.putExtra(GuideConstants.MAP_FOCUS, {PlaceId});
	 * 
	 * 	To open the map showing all the places in Agenda
	 * 	as well as user's current location
	 * 	>> intent.putExtra(GuideConstants.MAP_AGENDA, "");
	 */
	public static final String MAP_FOCUS = "map_focus";
	public static final String MAP_AGENDA = "map_agenda";
	
	/*
	 * ViewMapActivity interface note:
	 * 
	 * Call it with GuideConstant.SELECTION
	 * 	- if calling from Main, then add integer corresponding
	 * 		to the tab of origin. {1,2,3,4, ...}
	 * 	- if calling from PlaceDetailActivity, add integer
	 * 		100 + PlaceId
	 * 	- if calling from anywhere else, I don't know...
	 */
	public static final String SELECTION = "selection";
	public static final int LIMIT = 100;
	
	
}
