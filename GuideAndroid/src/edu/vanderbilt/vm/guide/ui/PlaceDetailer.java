package edu.vanderbilt.vm.guide.ui;

import java.io.InputStream;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import edu.vanderbilt.vm.guide.R;
import edu.vanderbilt.vm.guide.container.Place;
import edu.vanderbilt.vm.guide.db.GuideDBOpenHelper;
import edu.vanderbilt.vm.guide.util.DBUtils;
import edu.vanderbilt.vm.guide.util.GlobalState;
import edu.vanderbilt.vm.guide.util.GuideConstants;

/**
 * The activity that shows the details page for a place.  This activity shows
 * the place name, description, picture, hours, etc.  It also allows the user to
 * add or remove the place from the agenda and pin the place on the map.
 * @author nicholasking, athran
 *
 */
@TargetApi(16)
public class PlaceDetailer extends Activity {
	private Menu mMenu;
	private ActionBar mAction;
	
	private static final Logger logger = LoggerFactory
			.getLogger("ui.PlaceDetailer");
	private static final String PLACE_ID_EXTRA = "placeId";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_place_detail);
		
		// Setup ActionBar
		mAction = getActionBar();
		mAction.setTitle("Place Detail");
		mAction.setDisplayHomeAsUpEnabled(true);
		mAction.setBackgroundDrawable(new ColorDrawable(
				Color.rgb(189, 187, 14)));
		
	}
	// ---------- END onCreate() ---------- //

	/**
	 * Get the id of the place from the intent and query the SQLite database for
	 * that place
	 * @return The place we were given
	 */
	private Place getPlaceFromIntent() {
		Intent myIntent = getIntent();
		if (myIntent == null)
			return null;
		int placeId = myIntent.getIntExtra(GuideConstants.PLACE_ID_EXTRA,
				GuideConstants.BAD_PLACE_ID);
		GuideDBOpenHelper helper = new GuideDBOpenHelper(this);
		SQLiteDatabase db = helper.getReadableDatabase();
		Place place = DBUtils.getPlaceById(placeId, db);
		db.close();
		return place;
	}
	
	/**
	 * Use this method to open the Details page
	 * @param ctx The starting Activity
	 * @param placeid The Id of the Place that you want to detail
	 */
	public static void open(Context ctx, int placeid){
		Intent i = new Intent(ctx, PlaceDetailer.class);
		i.putExtra(PLACE_ID_EXTRA, placeid);
		ctx.startActivity(i);
	}
	
	public static Fragment getPlaceDetailFragment(int PlaceId) {
		//TODO
		return null;
	}
	
	public static class PlaceDetailerFragment extends Fragment{
		private Place mPlace;
		private TextView tvPlaceName;
		private TextView tvPlaceDesc;
		private TextView tvPlaceHours;
		private ImageView ivPlaceImage;
		private Bitmap mPlaceBitmap;
		private View mView;
		private boolean isOnAgenda = false;
		private Menu mMenu;
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			mView = inflater.inflate(R.layout.fragment_place_detailer, 
					container, false);
			return mView;
		}
		
		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);
			
			GuideDBOpenHelper helper = new GuideDBOpenHelper(getActivity());
			SQLiteDatabase db = helper.getReadableDatabase();
			Place place = DBUtils.getPlaceById(getActivity().getIntent()
					.getIntExtra("placeId", GuideConstants.BAD_PLACE_ID), db);
			db.close();
			mPlace = place;
			// XXX: We can get a null place here right now.  This intentionally not
			// being handled at the moment.  I want the app to crash if we get a
			// null place so we'll get a stack trace and find out what went wrong.
			// We'll handle null places at a later time (after we've switched to a
			// Content Provider model instead of a list-based model).
			
			tvPlaceName = (TextView) mView.findViewById(R.id.PlaceName);
			tvPlaceName.setText(mPlace.getName());
			
			tvPlaceHours = (TextView) mView.findViewById(R.id.PlaceHours);
			tvPlaceHours.setText(mPlace.getHours());
			
			tvPlaceDesc = (TextView) mView.findViewById(R.id.PlaceDescription);
			tvPlaceDesc.setText(mPlace.getDescription());
			
			ivPlaceImage = (ImageView) mView.findViewById(R.id.PlaceImage);
			
			// Download image
			Thread downloadImage = new Thread() {
				@Override
				public void run() {
					try {
						InputStream is = (InputStream) new URL(mPlace.getPictureLoc()).getContent();
						logger.trace("Download succeeded");
						mPlaceBitmap = BitmapFactory.decodeStream(is);
					} catch (Exception e) {
						logger.error("Download failed", e);
						mPlaceBitmap = null;
					}
				}
			};
			downloadImage.start();
			try {
				downloadImage.join();
				ivPlaceImage.setImageBitmap(mPlaceBitmap);
			} catch (InterruptedException e) {
				logger.error("Download failed", e);
			}
			// END Download image
			
			/* Check if this place is already on Agenda */
			if(GlobalState.getUserAgenda().isOnAgenda(mPlace)) {
				isOnAgenda = true;
			} else {
				isOnAgenda = false;
			}
			
			// add to History
			GlobalState.addHistory(mPlace);
			
			setHasOptionsMenu(true);
		}
		
		@Override
		public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		    inflater.inflate(R.menu.place_detail_activity, menu);
		    this.mMenu = menu;
		    
		    if (isOnAgenda){
		    	/*
		    	 * The default icon is a "+"
		    	 * therefore change to "-"
		    	 */
		    	mMenu.findItem(R.id.menu_add_agenda).setIcon(
		    			(Drawable)getResources().getDrawable(
		    					R.drawable.content_remove));
		    } else {
		    	// Use default icon "+" as defined in xml
		    }
		}
		
		public boolean onOptionsItemSelected(MenuItem item) {
			
			switch (item.getItemId()){
			case R.id.menu_add_agenda:
				addRemoveToAgenda();
				return true;
			case R.id.menu_map:
				MapViewer.openPlace(getActivity(), mPlace.getUniqueId());
				return true;
			case android.R.id.home:
				GuideMain.open(getActivity());
				return true;
			case R.id.menu_about:
				About.open(getActivity());
				return true;
			default: 
				return false;
			}
		}
		
		private void addRemoveToAgenda() {
			
			if (isOnAgenda) {
				GlobalState.getUserAgenda().remove(mPlace);
				mMenu.findItem(R.id.menu_add_agenda).setIcon((Drawable)
						getResources().getDrawable(R.drawable.content_new));
				Toast.makeText(getActivity(),"Removed from Agenda",
						Toast.LENGTH_SHORT).show();
			} else {
				GlobalState.getUserAgenda().add(mPlace);
				mMenu.findItem(R.id.menu_add_agenda).setIcon((Drawable)
						getResources().getDrawable(R.drawable.content_remove));
				Toast.makeText(getActivity(),"Added to Agenda",
						Toast.LENGTH_SHORT).show();
			}
			isOnAgenda = !isOnAgenda;
		}
		
		void setPlaceDetailed(Place plc) {
			mPlace = plc;
		}
	}
}
