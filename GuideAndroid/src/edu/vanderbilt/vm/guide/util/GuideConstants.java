package edu.vanderbilt.vm.guide.util;

public interface GuideConstants {

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
}
