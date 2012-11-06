package edu.vanderbilt.vm.guide;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import com.google.android.maps.*;

import edu.vanderbilt.vm.guide.util.ActivityTabListener;

@TargetApi(11)
public class ViewMapActivity extends MapActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);
		setupActionBar();
		
		/* Begin customizing MapView [Athran] */
		MapView MV = (MapView)findViewById(R.id.mapview);
		MV.setBuiltInZoomControls(true);
		MapController control = MV.getController();
		
		control.setZoom(17);	// set zoom level
		
		
		setupDisplayMarkers();
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
	
	private void setupDisplayMarkers(){
		
	}

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
	
	private static GeoPoint getGeoPointFromLocation(Location loc){
		return new GeoPoint((int)(loc.getLatitude()*1000000),(int)(loc.getLongitude()*1000000));
	}
}
