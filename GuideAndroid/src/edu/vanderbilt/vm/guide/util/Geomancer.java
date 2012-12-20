package edu.vanderbilt.vm.guide.util;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.vanderbilt.vm.guide.container.Place;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

/**
 * Provide methods related to Geolocation and positioning.
 * - at the start of the application, call activateGeolocation()
 * 		which returns void and accepts no argument.
 * - when the device's location is needed, call getDeviceLocation()
 * 		which accepts no argument. It returns a Location object which 
 * 		can then be fed into findClosestPlace() which returns a Place object.
 * 
 * These are array-transversal procedures, so there may be conflicts with the SQL approach
 * 
 * @author abdulra1
 */
public class Geomancer {
	
	private static final Logger logger = LoggerFactory.getLogger("util.Geomancer");
	
	private static Location CurrLocation;
	private static LocationManager mLocationManager;
	private static LocationListener mLocationListener = new LocationListener() {
		public void onLocationChanged(Location location) {
			// Called when a new location is found by the network location provider.
			CurrLocation = location;
			logger.info("Receiving location at lat/lon {},{}", location.getLatitude(), location.getLongitude());
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
		}

		public void onProviderEnabled(String provider) {
		}

		public void onProviderDisabled(String provider) {
		}
	};
	private final static int DEFAULT_RADIUS = 5; // 5 meters, for you americans out there.
	private final static int DEFAULT_TIMEOUT = 5000;
	private static Location mDefault;

	public static Place findClosestPlace(Location location, List<Place> placeList) {
		/* 
		 * Returns a Place which has the closest coordinate to the given Location.
		 * I made it take a List<Place> because someone might need to find a Place
		 * 	in a custom List, like the nearest academic building or the nearest
		 * 	buiding that has a tornado shelter (GASP!!!)
		 */
		double CurrDist = Double.MAX_VALUE;
		int count = 0;

		for (int n = 0; n < placeList.size(); n++) {
			double dist = findDistance(
					placeList.get(n).getLatitude(),
					placeList.get(n).getLongitude(), 
					location.getLatitude(),
					location.getLongitude());
			if (dist < CurrDist) {
				CurrDist = dist;
				count = n;
			}
		}
		Place result = placeList.get(count);
		logger.trace("Closest is {} at position {}", result.getName(), count);
		return placeList.get(count);
	}

	public static void activateGeolocation(Context context) {
		/*
		 * Setup the mechanism for determining device location.
		 * 	this method is called by GuideMain on application loading.
		 * Any activity that needs the device's location simply need to
		 * 	call getDeviceLocation()
		 */
		if (mLocationManager == null) {
			// Acquire a reference to the system Location Manager
			mLocationManager = (LocationManager) context
					.getSystemService(Context.LOCATION_SERVICE);
		}
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		//criteria.setPowerRequirement(Criteria.POWER_LOW);
		criteria.setAltitudeRequired(false);
		criteria.setBearingRequired(false);
		criteria.setSpeedRequired(false);
		criteria.setCostAllowed(true);

		List<String> matchingProviders = mLocationManager.getProviders(criteria, false);
		logger.trace("Found {} providers.", matchingProviders.size());
		if (!matchingProviders.isEmpty()) {
			String provider = matchingProviders.get(0);
			mLocationManager.requestLocationUpdates(provider, DEFAULT_TIMEOUT, DEFAULT_RADIUS,
					mLocationListener);
		} else {
			mLocationManager.requestLocationUpdates(
					LocationManager.NETWORK_PROVIDER, DEFAULT_TIMEOUT, DEFAULT_RADIUS,
					mLocationListener);
		}
		logger.trace("Geolocation init done.");
	}

	public static double findDistance(double x1, double y1, double x2, double y2) {
		return Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
	}
	
	public static Location getDeviceLocation(){
		if (CurrLocation == null){
			CurrLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		}
		if (CurrLocation == null){
			CurrLocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		}
		if (CurrLocation == null){
			CurrLocation = mLocationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
		}
		if (CurrLocation == null){
			mDefault = new Location("Me");
			mDefault.setTime((new Date()).getTime());
			mDefault.setLatitude(GlobalState.getPlaceById(10).getLatitude());
			mDefault.setLongitude((GlobalState.getPlaceById(10).getLongitude()));
			CurrLocation = mDefault;
		}
		
		return CurrLocation;
	}
	
	public static void setDeviceLocation(Location loc){
		CurrLocation = loc;
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

