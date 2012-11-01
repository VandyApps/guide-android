package edu.vanderbilt.vm.guide.util;

import java.util.List;

import android.content.Context;
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
		/* Taken from /developer.android.com/ */
		// Acquire a reference to the system Location Manager
		LocationManager locationManager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);

		// Define a listener that responds to location updates
		LocationListener locationListener = new LocationListener() {
			public void onLocationChanged(Location location) {
				// Called when a new location is found by the network location
				// provider.
				// makeUseOfNewLocation(location);
				CurrLocation = location;
			}

			public void onStatusChanged(String provider, int status,
					Bundle extras) {
			}

			public void onProviderEnabled(String provider) {
			}

			public void onProviderDisabled(String provider) {
			}
		};

		// Register the listener with the Location Manager to receive location
		// updates
		locationManager.requestLocationUpdates(
				LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
		return CurrLocation;
	}

	private static double findDistance(double x1, double y1, double x2,
			double y2) {
		return Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
	}
}
