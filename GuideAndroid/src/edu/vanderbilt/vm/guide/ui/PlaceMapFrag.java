
package edu.vanderbilt.vm.guide.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import edu.vanderbilt.vm.guide.R;
import edu.vanderbilt.vm.guide.container.Place;
import edu.vanderbilt.vm.guide.util.GlobalState;
import edu.vanderbilt.vm.guide.util.GuideConstants;

public class PlaceMapFrag extends SupportMapFragment {

    private Place mPlace;

    private static final Logger logger = LoggerFactory.getLogger("ui.PlaceTabFragment");

    private Menu mMenu;

    /**
     * Instantiate a Map Fragment and sets the map focus to a place
     * 
     * @param ctx
     * @param plc
     * @return
     */
    public static PlaceMapFrag newInstance(Context ctx, Place plc) {

        PlaceMapFrag frag = (PlaceMapFrag) Fragment.instantiate(ctx,
                "edu.vanderbilt.vm.guide.ui.PlaceMapFrag");
        frag.mPlace = plc;
        frag.setHasOptionsMenu(true);
        return frag;
    }

    @Override
    public void onResume() {
        super.onResume();

        GoogleMap map = this.getMap();
        MapViewer.resetCamera(map);

        if (mPlace == null) {
            // Show default Place (Vanderbilt Uni: UniqueId 10)
            logger.error("No Place is specified. Showing default instead.");
            mPlace = MapViewer.getPlaceById(getActivity(), GuideConstants.DEFAULT_ID);
        }

        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(new LatLng(mPlace.getLatitude(),
                mPlace.getLongitude()), MapViewer.BUILDING_ZOOM);
        map.animateCamera(update);

        map.addMarker(new MarkerOptions().draggable(false).position(MapViewer.toLatLng(mPlace))
                .title(mPlace.getName()).snippet(mPlace.getCategories().get(0)));
        /*
         * if (GlobalState.getUserAgenda().isOnAgenda(mPlace)) { // Option to
         * remove mMenu.findItem(R.id.map_menu_add_agenda).setVisible(false);
         * mMenu.findItem(R.id.map_menu_remove_agenda).setVisible(true); } else
         * { // Option to add MenuItem item =
         * mMenu.findItem(R.id.map_menu_add_agenda); item.setVisible(true); item
         * = mMenu.findItem(R.id.map_menu_remove_agenda);
         * item.setVisible(false); }
         */
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.map_menu_add_agenda:
                MapViewer.addToAgenda(getActivity(), mPlace.getUniqueId());
                mMenu.findItem(R.id.map_menu_add_agenda).setVisible(false);
                mMenu.findItem(R.id.map_menu_remove_agenda).setVisible(true);
                return true;
            case R.id.map_menu_remove_agenda:
                MapViewer.removeFromAgenda(getActivity(), mPlace.getUniqueId());
                mMenu.findItem(R.id.map_menu_add_agenda).setVisible(true);
                mMenu.findItem(R.id.map_menu_remove_agenda).setVisible(false);
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        mMenu = menu;

        if (GlobalState.getUserAgenda().isOnAgenda(mPlace)) {
            // Option to remove
            mMenu.findItem(R.id.map_menu_add_agenda).setVisible(false);
            mMenu.findItem(R.id.map_menu_remove_agenda).setVisible(true);
        } else {
            // Option to add
            MenuItem item = mMenu.findItem(R.id.map_menu_add_agenda);
            item.setVisible(true);
            item = mMenu.findItem(R.id.map_menu_remove_agenda);
            item.setVisible(false);
        }

    }

}
