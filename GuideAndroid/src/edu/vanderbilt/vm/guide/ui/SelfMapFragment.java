package edu.vanderbilt.vm.guide.ui;

import android.content.Context;
import android.location.Location;
import android.app.Fragment;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;

import edu.vanderbilt.vm.guide.util.Geomancer;

public class SelfMapFragment extends MapFragment {

	/**
	 * Instantiate a Map Fragment and focuses on the current location
	 * 
	 * @param ctx
	 * @return
	 */
	public static SelfMapFragment newInstance(Context ctx) {
		SelfMapFragment frag = (SelfMapFragment) Fragment.instantiate(ctx,
				"edu.vanderbilt.vm.guide.ui.SelfMapFragment");
		return frag;
	}

	@Override
	public void onResume() {
		super.onResume();

		GoogleMap map = this.getMap();

		map.setMyLocationEnabled(true);
		Location loc = map.getMyLocation();

		Geomancer.setDeviceLocation(loc);
		CameraUpdate u = CameraUpdateFactory.newLatLng(new LatLng(loc
				.getLatitude(), loc.getLongitude()));

		map.animateCamera(u);
	}

	@Override
	public void onPause() {
		super.onPause();
		this.getMap().setMyLocationEnabled(false);
	}
}
