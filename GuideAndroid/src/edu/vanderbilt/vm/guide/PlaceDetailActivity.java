package edu.vanderbilt.vm.guide;

/**
 * @author Athran
 * Origin: GuideMain
 * Desc: A home page for interaction with Place
 * NavigateTo: WebMap
 */

import java.io.InputStream;
import java.net.URL;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import edu.vanderbilt.vm.guide.util.GlobalState;
import edu.vanderbilt.vm.guide.util.GuideConstants;
import edu.vanderbilt.vm.guide.util.Place;

public class PlaceDetailActivity extends Activity implements OnClickListener {

	TextView mPlaceNameTv;
	ImageView mPlaceIv;
	TextView mPlaceDescTv;
	TextView mPlaceHoursTv;
	Button mMapButton;
	Bitmap mPlaceBitmap;
	Button mAgendaActionButton;
	
	private boolean mIsOnAgenda = false;
	private Place mPlace;
	
	OnClickListener mAgendaButtonListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if(mIsOnAgenda) {
				GlobalState.getUserAgenda().remove(mPlace);
				mAgendaActionButton.setText(ADD_STR);
			} else {
				GlobalState.getUserAgenda().add(mPlace);
				mAgendaActionButton.setText(REMOVE_STR);
			}
			mIsOnAgenda = !mIsOnAgenda;
		}
	};
	
	private static final String ADD_STR = "Add to Agenda";
	private static final String REMOVE_STR = "Remove from Agenda";

	@Override
	public void onCreate(Bundle SavedInstanceState) {
		super.onCreate(SavedInstanceState);
		setContentView(R.layout.activity_place_detail);
		
		findViews();

		/**
		 * Sets the content of the page based on data from the place we got
		 */
		mPlace = getPlaceFromIntent();
		// XXX: We can get a null place here right now.  This intentionally not
		// being handled at the moment.  I want the app to crash if we get a
		// null place so we'll get a stack trace and find out what went wrong.
		// We'll handle null places at a later time (after we've switched to a
		// Content Provider model instead of a list-based model).

		mPlaceNameTv.setText(mPlace.getName());
		mPlaceDescTv.setText(mPlace.getDescription());
		mPlaceHoursTv.setText("Hours of operation: " + mPlace.getHours());
		
		Thread downloadImage = new Thread() {
			@Override
			public void run() {
				try {
					InputStream is = (InputStream) new URL(mPlace.getPictureUri().getPath()).getContent();
					Log.d(getClass().getSimpleName(), "Download succeeded");
					mPlaceBitmap = BitmapFactory.decodeStream(is);
				} catch (Exception e) {
					Log.d(getClass().getSimpleName(), "Download failed");
					mPlaceBitmap = null;
				}
			}
		};
		downloadImage.start();
		try {
			downloadImage.join();
			mPlaceIv.setImageBitmap(mPlaceBitmap);
		} catch (InterruptedException e) {
			Log.d(getClass().getSimpleName(), "Download failed", e);
			// Error Handle
		}
		
		if(GlobalState.getUserAgenda().isOnAgenda(mPlace)) {
			mAgendaActionButton.setText(REMOVE_STR);
			mIsOnAgenda = true;
		} else {
			mAgendaActionButton.setText(ADD_STR);
			mIsOnAgenda = false;
		}
		mAgendaActionButton.setOnClickListener(mAgendaButtonListener);
		
		mMapButton.setOnClickListener(this);

	}

	public void onClick(View view) { // fixed to go to MapView instead. Need
										// cleanup
		Intent i = new Intent(this, ViewMapActivity.class);
		// i.putExtra("Lat",Double.toString(DUMMY_PLACE.getLatitude()));
		// i.putExtra("Long",Double.toString(DUMMY_PLACE.getLongitude()));
		startActivity(i);
	}

	private Place getPlaceFromIntent() {
		Intent myIntent = getIntent();
		if (myIntent == null)
			return null;
		int placeId = myIntent.getIntExtra(GuideConstants.PLACE_ID_EXTRA,
				GuideConstants.BAD_PLACE_ID);
		return GlobalState.getPlaceById(placeId);
	}
	
	private void findViews() {
		mPlaceNameTv = (TextView) findViewById(R.id.PlaceName);
		mPlaceIv = (ImageView) findViewById(R.id.PlaceImage);
		mPlaceDescTv = (TextView) findViewById(R.id.PlaceDescription);
		mMapButton = (Button) findViewById(R.id.BMap);
		mAgendaActionButton = (Button) findViewById(R.id.BAgendaAction);
		mPlaceHoursTv = (TextView) findViewById(R.id.PlaceHours);
	}

}
