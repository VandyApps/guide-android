package edu.vanderbilt.vm.guide.ui;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.Fragment;
import android.content.Context;
import android.database.Cursor;
import android.location.Location;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import edu.vanderbilt.vm.guide.R;
import edu.vanderbilt.vm.guide.container.Agenda;
import edu.vanderbilt.vm.guide.container.Place;
import edu.vanderbilt.vm.guide.db.GuideDBConstants;
import edu.vanderbilt.vm.guide.db.GuideDBOpenHelper;
import edu.vanderbilt.vm.guide.util.DBUtils;
import edu.vanderbilt.vm.guide.util.Geomancer;
import edu.vanderbilt.vm.guide.util.GlobalState;

public class AgendaMapFrag extends MapFragment 
        implements OnMapLongClickListener,OnMarkerClickListener {

	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory
			.getLogger("ui.AgendaMapFrag");
	private Agenda mAgenda;
	private int mPlaceIdFocused;
	private Menu mMenu;
	private boolean showSelf = true;
	
	/**
	 * Instantiate a Map Fragment and puts markers on all the places on the
	 * Agenda
	 * 
	 * @param ctx
	 * @param agenda
	 * @return
	 */
	public static AgendaMapFrag newInstance(Context ctx, Agenda agenda) {

		AgendaMapFrag frag = (AgendaMapFrag) Fragment.instantiate(ctx,
				"edu.vanderbilt.vm.guide.ui.AgendaMapFrag");
		frag.mAgenda = agenda;
		frag.mPlaceIdFocused = -1;
		frag.setHasOptionsMenu(true);
		return frag;
	}

	@Override
	public void onResume() {
		super.onResume();

		GoogleMap map = getMap();
		MapViewer.resetCamera(map);

		if (mAgenda == null) {
			mAgenda = GlobalState.getUserAgenda();
		}

		ArrayList<LatLng> geopointList = new ArrayList<LatLng>();
		for (Place plc : mAgenda) {
			geopointList.add(MapViewer.toLatLng(plc));

			Location plcLoc = new Location("temp");
			plcLoc.setLatitude(plc.getLatitude());
			plcLoc.setLongitude(plc.getLongitude());

			// Set the marker for each Place
			// Title must be exactly as the PlaceName, in order to match
			// them later on
			map.addMarker(new MarkerOptions()
					.position(MapViewer.toLatLng(plc))
					.title(plc.getName())
					.draggable(false)
					.snippet(
							Geomancer.getDeviceLocation().distanceTo(plcLoc)
									+ " yards away"));
		}

		// Calculate the bounds that cover all places in Agenda
		if (geopointList.size() == 0) {

		} else {
			double minLat = Double.MAX_VALUE;
			double maxLat = -Double.MAX_VALUE;
			double minLng = Double.MAX_VALUE;
			double maxLng = -Double.MAX_VALUE;

			for (LatLng point : geopointList) {
				if (point.latitude < minLat) {
					minLat = point.latitude;
				}
				if (point.latitude > maxLat) {
					maxLat = point.latitude;
				}
				if (point.longitude < minLng) {
					minLng = point.longitude;
				}
				if (point.longitude > minLng) {
					maxLng = point.longitude;
				}
			}

			// Sanitize
			if (minLat > 90 || minLat < -90) {
				minLat = 0;
			}
			if (maxLat > 90 || maxLat < -90) {
				maxLat = 0;
			}
			if (minLng > 180 || minLng < -180) {
				minLng = 0;
			}
			if (maxLng > 180 || maxLng < -180) {
				maxLng = 0;
			}

			CameraUpdate update = CameraUpdateFactory.newLatLng(new LatLng(
					(minLat + maxLat) / 2, (minLng + maxLng) / 2));
			map.animateCamera(update);

			// What happens when a marker is tapped
			map.setOnMarkerClickListener(this);

		}
		
		map.setMyLocationEnabled(showSelf);
		
		map.setOnMapLongClickListener(this);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.map_menu_add_agenda:
			MapViewer.addToAgenda(getActivity(), mPlaceIdFocused);
			mMenu.findItem(R.id.map_menu_add_agenda).setVisible(false);
			mMenu.findItem(R.id.map_menu_remove_agenda).setVisible(true);
			return true;
		case R.id.map_menu_remove_agenda:
			MapViewer.removeFromAgenda(getActivity(), mPlaceIdFocused);
			mMenu.findItem(R.id.map_menu_add_agenda).setVisible(true);
			mMenu.findItem(R.id.map_menu_remove_agenda).setVisible(false);
			return true;
		default:
			return false;
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		mMenu = menu;
	}
	
	/**
	 * Set whether the map has a current location marker. Default is true.
	 * 
	 * @param show
	 */
	public void setShowCurrentLocation(boolean show) {
		showSelf = show;
	}
	
	@Override
	public void onPause() {
		super.onPause();
		this.getMap().setMyLocationEnabled(false);
	}
	
	public void drawPath() {
		
		
		
	}
	
	public void redrawMarker() {
		
		for (Place plc : mAgenda) {

			Location plcLoc = new Location("temp");
			plcLoc.setLatitude(plc.getLatitude());
			plcLoc.setLongitude(plc.getLongitude());

			// Set the marker for each Place
			// Title must be exactly as the PlaceName, in order to match
			// them later on
			getMap().addMarker(new MarkerOptions()
					.position(MapViewer.toLatLng(plc))
					.title(plc.getName())
					.draggable(false)
					.snippet(
							Geomancer.getDeviceLocation().distanceTo(plcLoc)
									+ " yards away"));
		}
		
	}

    @Override
    public void onMapLongClick(LatLng point) {
        
        Location clicked = new Location("temp");
        clicked.setLatitude(point.latitude);
        clicked.setLongitude(point.longitude);
        
        GuideDBOpenHelper helper = new GuideDBOpenHelper(getActivity());
        String[] columns = {
                GuideDBConstants.PlaceTable.LATITUDE_COL,
                GuideDBConstants.PlaceTable.LONGITUDE_COL,
                GuideDBConstants.PlaceTable.ID_COL};
        Cursor cursor = DBUtils.getAllPlaces(columns, 
                helper.getReadableDatabase());
        
        int position = Geomancer.findClosestPlace(clicked, cursor);
        cursor.moveToPosition(position);
        int idColIx =cursor.getColumnIndex(GuideDBConstants.PlaceTable.ID_COL);
        PlaceDetailer.open(getActivity(), (int) cursor.getLong(idColIx));
        helper.close();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        
        if (marker.isInfoWindowShown()) {
            marker.hideInfoWindow();
            mPlaceIdFocused = -1;
        } else {
            marker.showInfoWindow();

            Place plc = null;
            Agenda agenda = GlobalState.getUserAgenda();

            // Had to match marker by title, because there is no
            // marker id
            for (Place agndPlc : agenda) {
                if (marker.getTitle().equals(agndPlc.getName())) {
                    plc = agndPlc;
                    break;
                }
            }

            if (plc != null) {
                mPlaceIdFocused = plc.getUniqueId();
                if (agenda.isOnAgenda(plc)) {
                    // Option to remove
                    MenuItem item = mMenu
                            .findItem(R.id.map_menu_remove_agenda);
                    item.setVisible(true);
                    item.setShowAsAction(
                            MenuItem.SHOW_AS_ACTION_ALWAYS);

                    item =mMenu.findItem(R.id.map_menu_add_agenda);
                    item.setVisible(false);
                    item = null;
                } else {
                    // Option to add
                    mMenu.findItem(R.id.map_menu_add_agenda)
                            .setVisible(true);
                    mMenu.findItem(R.id.map_menu_remove_agenda)
                            .setVisible(false);
                }
            }
        }
        return true;
    }
	

	
}
