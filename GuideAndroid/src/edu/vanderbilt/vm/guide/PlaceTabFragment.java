package edu.vanderbilt.vm.guide;

/**
 * @author Athran, Nick
 * This Fragment shows the categories of places and the user's current location
 */

import com.parse.FindCallback;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.app.ListFragment;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import edu.vanderbilt.vm.guide.util.Geomancer;
import edu.vanderbilt.vm.guide.util.GlobalState;
import edu.vanderbilt.vm.guide.util.GuideConstants;
import edu.vanderbilt.vm.guide.util.Place;
import edu.vanderbilt.vm.guide.util.PlaceListAdapter;

@TargetApi(11)
public class PlaceTabFragment extends Fragment {
	private static final String LOG_TAG = "PlaceMainFragment";
	private ListView mListView;
	private TextView mCurrPlaceName;
	private TextView mCurrPlaceDesc;
	private final int DESCRIPTION_LENGTH = 35;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_place_list, container, false);
    }

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		mListView = (ListView) getActivity().findViewById(R.id.placeTablistView);
		mListView.setAdapter(new PlaceListAdapter(getActivity(),
				GlobalState.getPlaceList(getActivity())));
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Place place = (Place) mListView.getItemAtPosition(position);
				Intent i = new Intent().setClass(getActivity(),
						PlaceDetailActivity.class).putExtra(
						GuideConstants.PLACE_ID_EXTRA, place.getUniqueId());
				startActivity(i);
			}

		});
		
		/*
		 * Tells you what is the closest building to your location right now
		 */
		Location loc = Geomancer.getDeviceLocation();
		Place currPlace = null;
		if (loc != null){
			currPlace = Geomancer.findClosestPlace(loc, GlobalState.getPlaceList(getActivity()));
		}
		
		if (currPlace != null){
			mCurrPlaceName = (TextView)getActivity().findViewById(R.id.currentPlaceName);
				mCurrPlaceName.setText(currPlace.getName());
			
			mCurrPlaceDesc = (TextView)getActivity().findViewById(R.id.currentPlaceDesc);
				mCurrPlaceDesc.setText(currPlace.getDescription().substring(0, DESCRIPTION_LENGTH) + "...");
		}
	}

}
