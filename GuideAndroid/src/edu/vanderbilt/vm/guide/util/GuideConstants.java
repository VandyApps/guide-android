package edu.vanderbilt.vm.guide.util;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

public interface GuideConstants {

	/*
	 * PlaceDetailInterface interface note: Call it with
	 * GuideConstant.PLACE_ID_EXTRA and put in the desired Place's UniqueId
	 */
	public static final String PLACE_ID_EXTRA = "placeId";
	public static final int BAD_PLACE_ID = -1;
	public static final int DEFAULT_ID = 10;
	public static final ColorDrawable ACTION_BAR_BG = new ColorDrawable(
			Color.rgb(189, 187, 14));
}
