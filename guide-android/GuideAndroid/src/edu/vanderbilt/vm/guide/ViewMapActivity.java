package edu.vanderbilt.vm.guide;

import java.io.IOException;
import java.util.List;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.google.android.maps.*;

import edu.vanderbilt.vm.guide.util.ActivityTabListener;
import edu.vanderbilt.vm.guide.util.GlobalState;
import edu.vanderbilt.vm.guide.util.Place;

@TargetApi(11)
public class ViewMapActivity extends MapActivity {
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);
		setupActionBar();
		
		/* Begin customizing MapView [athran] */
		MapView MV = (MapView)findViewById(R.id.mapview);
		MV.setBuiltInZoomControls(true);
		
			// Controller set where and how the map points to
		MapController control = MV.getController();
		control.setZoom(17);	// set zoom level
		control.setCenter(convToGeoPoint()); // TODO set center to current.
		
			// Overlay creation
		List<Overlay> master_overlay = MV.getOverlays();
		master_overlay.add(new PlacesOverlay((Drawable)getResources().getDrawable(R.drawable.marker)));
		
		MyLocationOverlay self = new MyLocationOverlay(this, MV);
		master_overlay.add(self);
		
		/* End customizing MapView */
		
		LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

//		Criteria criteria = new Criteria();
//		criteria.setAccuracy(Criteria.ACCURACY_FINE);
//		criteria.setPowerRequirement(Criteria.POWER_LOW);
//		criteria.setAltitudeRequired(false);
//		criteria.setBearingRequired(false);
//		criteria.setSpeedRequired(false);
//		criteria.setCostAllowed(true);
//
//		List<String> matchingProviders = lm.getProviders(criteria, false);
//
//		String provider = matchingProviders.get(0);
//		// milliseconds
//		int t = 5000;
//		// meters
//		int distance = 5;
//		
//		LocationListener myLocationListener = new LocationListener() {
//			public void onLocationChanged(Location location) {
//				// Update application based on new location.
//			}
//
//			public void onProviderDisabled(String provider) {
//				// Update application if provider disabled.
//			}
//
//			public void onProviderEnabled(String provider) {
//				// Update application if provider enabled.
//			}
//
//			public void onStatusChanged(String provider, int status,
//					Bundle extras) {
//				// Update application if provider hardware status changed.
//			}
//		};
//		lm.requestLocationUpdates(provider, t, distance,
//				myLocationListener);

	}

	private void setupActionBar() {
		ActionBar ab = getActionBar();
		ab.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		ab.setDisplayShowTitleEnabled(false);

		Tab tab = ab.newTab().setText("Map")
				.setTabListener(new DummyTabListener());
		ab.addTab(tab);

		tab = ab.newTab().setText("Tours").setTabListener(new ActivityTabListener(this, GuideMain.class, 1));
		ab.addTab(tab);
		
		tab = ab.newTab().setText("Places")
				.setTabListener(new ActivityTabListener(this, GuideMain.class, 2));
		ab.addTab(tab);
		
		tab = ab.newTab().setText("Agenda")
				.setTabListener(new ActivityTabListener(this, GuideMain.class, 3));
		ab.addTab(tab);
	}
	
	/* @author athran
	 * this subclass defines the layer that contains
	 * the Places pin on the map
	 * code from page 454
	 */
	private class PlacesOverlay extends ItemizedOverlay<OverlayItem>{
		private List<OverlayItem> mItemList;
		private Drawable marker = null;
		
		public PlacesOverlay(Drawable marker){
			super(marker);
			this.marker = marker;
			boundCenterBottom(marker);
			
			// get PlaceList from GlobalState
			// TODO DB migration.
			List<Place> pl = null;
			try { pl = GlobalState.getPlaceList(null); }	
			catch (IOException e) { e.printStackTrace();	
				Log.e("ViewMapActivity.java", "Fail to get PlaceList");}
			
			// transcribing PlaceList into List of item
			// may not be the best way to do it (?)
			for (int j = 0;j<pl.size();j++){
				mItemList.add(new OverlayItem(
						convToGeoPoint(pl.get(j)),	// GeoPoint
						pl.get(j).getName(),		// Pin tag
						"A Place in Vanderbilt. This is a ShortDescription"));// Pin snippet
			}
			
			populate();
		}
		
		protected boolean onTap(int i){
			/**
			 * TODO clicking on the map pins should lead to the PlaceDetailActivity
			 */
			
			return true;
		}
		
		protected OverlayItem createItem(int i){
			return mItemList.get(i);
		}
		
		public int size(){
			return mItemList.size();
		}
	}
	/* End subclass */
	
	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

	private static class DummyTabListener implements ActionBar.TabListener {

		@Override
		public void onTabSelected(Tab tab, FragmentTransaction ft) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onTabUnselected(Tab tab, FragmentTransaction ft) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onTabReselected(Tab tab, FragmentTransaction ft) {
			// TODO Auto-generated method stub

		}

	}
	
	/* @author athran
	 * Extracts the coordinate information from Location or Place
	 * and create a GeoPoint from it
	 */
	private static GeoPoint convToGeoPoint(Location loc){
		return new GeoPoint((int)(loc.getLatitude()*1000000),(int)(loc.getLongitude()*1000000));
	}
	
	private static GeoPoint convToGeoPoint(Place place){
		return new GeoPoint((int)(place.getLatitude()*1000000),(int)(place.getLongitude()*1000000));
	}
	
	private static GeoPoint convToGeoPoint(){
		return null; //TODO Remove this as soon as the CurrentLocation is available
	}
	/* End utility methods */
}
