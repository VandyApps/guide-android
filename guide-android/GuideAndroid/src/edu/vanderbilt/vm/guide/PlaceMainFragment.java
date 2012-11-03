package edu.vanderbilt.vm.guide;

/**
 * @author Athran
 * This Fragment is the content of the second tab that I added to the main page
 */
import java.io.IOException;

import android.annotation.TargetApi;
import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;
import edu.vanderbilt.vm.guide.util.GlobalState;
import edu.vanderbilt.vm.guide.util.GuideConstants;
import edu.vanderbilt.vm.guide.util.Place;
import edu.vanderbilt.vm.guide.util.PlaceListAdapter;

@TargetApi(11)
public class PlaceMainFragment extends ListFragment {

	private static final String LOG_TAG = "PlaceMainFragment";

	/**
	 * This is an empty constructor The Dev page says that this is necessary but
	 * the program seems to run fine without it
	 */
	public PlaceMainFragment() {
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		try {
			setListAdapter(new PlaceListAdapter(getActivity(),
					GlobalState.getPlaceList(getActivity())));
		} catch (IOException e) {
			Toast.makeText(getActivity(), "Couldn't get list of places!",
					Toast.LENGTH_LONG).show();
			Log.e(LOG_TAG, "couldn't get list of places");
		}
		getListView().setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Place place = (Place) getListView().getItemAtPosition(position);
				Intent i = new Intent().setClass(getActivity(),
						PlaceDetailActivity.class).putExtra(
						GuideConstants.PLACE_ID_EXTRA, place.getUniqueId());
				startActivity(i);
			}

		});
	}

}
