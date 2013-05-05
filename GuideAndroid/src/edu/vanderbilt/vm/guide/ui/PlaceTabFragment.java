
package edu.vanderbilt.vm.guide.ui;

/**
 * @author Athran, Nick
 * This Fragment shows the categories of places and the user's current location
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.MenuItem;

import android.annotation.TargetApi;
import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.Toast;
import edu.vanderbilt.vm.guide.R;
import edu.vanderbilt.vm.guide.db.GuideDBConstants;
import edu.vanderbilt.vm.guide.db.GuideDBOpenHelper;
import edu.vanderbilt.vm.guide.ui.adapter.AlphabeticalCursorAdapter;
import edu.vanderbilt.vm.guide.ui.adapter.DistanceCursorAdapter;
import edu.vanderbilt.vm.guide.ui.listener.PlaceListClickListener;
import edu.vanderbilt.vm.guide.util.DBUtils;
import edu.vanderbilt.vm.guide.util.Geomancer;

@TargetApi(16)
public class PlaceTabFragment extends SherlockFragment {

    private ListView mListView;

    private Cursor mAllPlacesCursor; // A cursor holding all places in the db

    private static final Logger logger = LoggerFactory.getLogger("ui.PlaceTabFragment");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_place_list, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setupUI();

        // Query for places and setup ListView
        GuideDBOpenHelper helper = new GuideDBOpenHelper(getActivity());
        String[] columns = {
                GuideDBConstants.PlaceTable.NAME_COL, GuideDBConstants.PlaceTable.CATEGORY_COL,
                GuideDBConstants.PlaceTable.LATITUDE_COL,
                GuideDBConstants.PlaceTable.LONGITUDE_COL, GuideDBConstants.PlaceTable.ID_COL,
                GuideDBConstants.PlaceTable.DESCRIPTION_COL,
                GuideDBConstants.PlaceTable.IMAGE_LOC_COL
        };
        mAllPlacesCursor = DBUtils.getAllPlaces(columns, helper.getReadableDatabase());
        mListView.setAdapter(new AlphabeticalCursorAdapter(getActivity(), mAllPlacesCursor));
        mListView.setOnItemClickListener(new PlaceListClickListener(getActivity()));
        helper.close();

        // Tells you what is the closest building to your location right now
        Location loc = Geomancer.getDeviceLocation();
        findAndSetClosestPlace(loc);

        // Prevent the soft keyboard from popping up at startup.
        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        setHasOptionsMenu(true);

    }

    //@SuppressWarnings("deprecation")
    private void setupUI() {

        mListView = (ListView)getActivity().findViewById(R.id.placeTablistView);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_sort_alphabetic:

                mListView.setAdapter(new AlphabeticalCursorAdapter(getActivity(), mAllPlacesCursor));

                return true;

            case R.id.menu_sort_distance:
                mListView.setAdapter(new DistanceCursorAdapter(getActivity(), mAllPlacesCursor));

                Toast.makeText(getActivity(), "PlacesList is sorted by distance",
                        Toast.LENGTH_SHORT).show();
                return true;

            default:
                return false;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //Geomancer.registerGeomancerListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        //Geomancer.removeGeomancerListener(this);
    }

    
    public void updateLocation(Location loc) {
        findAndSetClosestPlace(loc);
    }

    private void findAndSetClosestPlace(Location loc) {
        if (loc == null) {
            return;
        }

        int closestIx = Geomancer.findClosestPlace(loc, mAllPlacesCursor);
        if (closestIx == -1) {
            Toast.makeText(getActivity(), "Couldn't find closest place", Toast.LENGTH_LONG).show();
        } else {
            mAllPlacesCursor.moveToPosition(closestIx);
            //setCurrentPlace(DBUtils.getPlaceFromCursor(mAllPlacesCursor));
        }
    }

}
