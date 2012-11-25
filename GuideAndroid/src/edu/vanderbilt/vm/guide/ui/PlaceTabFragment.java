package edu.vanderbilt.vm.guide.ui;

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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import edu.vanderbilt.vm.guide.R;
import edu.vanderbilt.vm.guide.container.Place;
import edu.vanderbilt.vm.guide.ui.adapter.PlaceListAdapter;
import edu.vanderbilt.vm.guide.util.Geomancer;
import edu.vanderbilt.vm.guide.util.GlobalState;
import edu.vanderbilt.vm.guide.util.GuideConstants;

@TargetApi(11)
public class PlaceTabFragment extends Fragment implements OnClickListener{
	private static final String LOG_TAG = "PlaceMainFragment";
	private final int DESCRIPTION_LENGTH = 35;
	
	private ListView mListView;
	private TextView mCurrPlaceName;
	private TextView mCurrPlaceDesc;
	private EditText mSearchBox;
	private LinearLayout mCurrentPlaceBar;
	private Place mCurrPlace;
	
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
		mCurrPlace = null;
		if (loc != null){
			mCurrPlace = Geomancer.findClosestPlace(loc, GlobalState.getPlaceList(getActivity()));
		}
		
		if (mCurrPlace != null){
			mCurrPlaceName = (TextView)getActivity().findViewById(R.id.currentPlaceName);
			mCurrPlaceName.setText(mCurrPlace.getName());
			
			mCurrPlaceDesc = (TextView)getActivity().findViewById(R.id.currentPlaceDesc);
			String desc = mCurrPlace.getDescription();
			if (desc.length() > DESCRIPTION_LENGTH){
				mCurrPlaceDesc.setText(desc.substring(0, DESCRIPTION_LENGTH) + "...");
			} else {
				mCurrPlaceDesc.setText(desc + "...");
			}
			
		}
		
		// Prevent the soft keyboard from popping up at startup.
		getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		
		mSearchBox = (EditText)getActivity().findViewById(R.id.placeTabSearchEdit);
		mSearchBox.setOnClickListener(this);
		
		mCurrentPlaceBar = (LinearLayout) getActivity().findViewById(R.id.current_place_bar);
		mCurrentPlaceBar.setOnClickListener(this);

	}
	
	// this method of setting up OnClickListener seems to be necessary when you want to access class variables
	@Override
	public void onClick(View v) {
		if (v == mCurrPlaceDesc || v == mCurrPlaceName || v == mCurrentPlaceBar){
			Intent i = new Intent(getActivity(), PlaceDetailActivity.class);
			i.putExtra(GuideConstants.PLACE_ID_EXTRA, mCurrPlace.getUniqueId());
			startActivity(i);
		} else if (v == mSearchBox){
//			mSearchBox.setFocusable(true);
		}
		
	}

}
