package edu.vanderbilt.vm.guide.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.Fragment;
import android.content.Context;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import edu.vanderbilt.vm.guide.container.Place;
import edu.vanderbilt.vm.guide.util.GlobalState;
import edu.vanderbilt.vm.guide.util.GuideConstants;

public class PlaceMapFrag extends MapFragment {
	
	private Place mPlace;
	private static final Logger logger = LoggerFactory
			.getLogger("ui.PlaceTabFragment");
	
	public static PlaceMapFrag newInstance(Context ctx,Place plc) {
		
		PlaceMapFrag frag = (PlaceMapFrag) Fragment.instantiate(ctx, 
				"edu.vanderbilt.vm.guide.ui.PlaceMapFrag");
		frag.mPlace = plc;
		return frag;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		GoogleMap map = this.getMap();
		
		if (mPlace == null) {
			// Show default Place (Vanderbilt Uni: UniqueId 10)
			logger.error("No Place is specified. Showing default instead.");
			mPlace = GlobalState.getPlaceById(GuideConstants.DEFAULT_ID);
		}
		
		CameraUpdate update = CameraUpdateFactory.newLatLngZoom(
				new LatLng(mPlace.getLatitude(), mPlace.getLongitude()),
				MapViewer.BUILDING_ZOOM);
		map.moveCamera(update);
		
		map.addMarker(new MarkerOptions()
				.draggable(false)
				.title(mPlace.getName())
				.snippet(mPlace.getCategories().get(0)));
	}
	
}
