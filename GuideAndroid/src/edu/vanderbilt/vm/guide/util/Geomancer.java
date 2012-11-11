package edu.vanderbilt.vm.guide.util;

import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

/**
 * Provide methods related to Geolocation and positioning These are
 * array-transversal procedures, so there may be conflicts with the SQL approach
 * Methods still untested as of 10/22/2012
 * 
 * @author abdulra1
 * 
 */
public class Geomancer {
	private static Location CurrLocation;
	private static LocationManager mLocationManager;
	private static LocationListener mLocationListener = new LocationListener() {
		public void onLocationChanged(Location location) {
			// Called when a new location is found by the network location
			// provider.
			// makeUseOfNewLocation(location);
			CurrLocation = location;
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
		}

		public void onProviderEnabled(String provider) {
		}

		public void onProviderDisabled(String provider) {
		}
	};;

	/* Returns a Place which has the closest coordinate to the given Location */
	public static Place findClosestPlace(Location location,
			List<Place> placeList) {
		double CurrDist = 0;
		int count = 0;

		for (int n = 0; n < placeList.size(); n++) {
			double dist = findDistance(placeList.get(n).getLongitude(),
					placeList.get(n).getLatitude(), location.getLatitude(),
					location.getLongitude());
			if (dist < CurrDist) {
				CurrDist = dist;
				count = n;
			}
		}
		return placeList.get(count);
	}

	public static Location locateDevice(Context context) {
		if (mLocationManager == null) {
			// Acquire a reference to the system Location Manager
			mLocationManager = (LocationManager) context
					.getSystemService(Context.LOCATION_SERVICE);
			Criteria criteria = new Criteria();
			criteria.setAccuracy(Criteria.ACCURACY_FINE);
			criteria.setPowerRequirement(Criteria.POWER_LOW);
			criteria.setAltitudeRequired(false);
			criteria.setBearingRequired(false);
			criteria.setSpeedRequired(false);
			criteria.setCostAllowed(true);

			List<String> matchingProviders = mLocationManager.getProviders(
					criteria, false);
			if (!matchingProviders.isEmpty()) {
				String provider = matchingProviders.get(0);

				mLocationManager.requestLocationUpdates(provider, 0, 0,
						mLocationListener);
			} else {
				mLocationManager.requestLocationUpdates(
						LocationManager.NETWORK_PROVIDER, 0, 0,
						mLocationListener);
			}
		}
		return CurrLocation;
	}

	private static double findDistance(double x1, double y1, double x2,
			double y2) {
		return Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
	}
}

// milliseconds
//int t = 5000;
//// meters
//int distance = 5;
//
//LocationListener myLocationListener = new LocationListener() {
//	public void onLocationChanged(Location location) {
//		// Update application based on new location.
//	}
//
//	public void onProviderDisabled(String provider) {
//		// Update application if provider disabled.
//	}
//
//	public void onProviderEnabled(String provider) {
//		// Update application if provider enabled.
//	}
//
//	public void onStatusChanged(String provider, int status,
//			Bundle extras) {
//		// Update application if provider hardware status changed.
//	}
//};
//lm.requestLocationUpdates(provider, t, distance,
//		myLocationListener);

