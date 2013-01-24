package edu.vanderbilt.vm.guide.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.Fragment;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
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

public class PlaceDetailerFragment extends Fragment{
	private Place mPlace;
	private TextView tvPlaceName;
	private TextView tvPlaceDesc;
	private TextView tvPlaceHours;
	private ImageView ivPlaceImage;
	private Bitmap mPlaceBitmap;
	private View mView;
	private boolean isOnAgenda = false;
	private Menu mMenu;
	
	private static final Logger logger = LoggerFactory
			.getLogger("ui.PlaceDetailerFragment");
	
	/**
	 * Create a new instance of the PlaceDetail fragment. This method returns a
	 * Fragment which you can add to a ViewGroup.
	 * 
	 * @param placeId the UniqueId of the place to be detailed.
	 * @return
	 */
	public static PlaceDetailerFragment newInstance(Context ctx, int id) {
		PlaceDetailerFragment frag = (PlaceDetailerFragment) Fragment
				.instantiate(ctx, 
				"edu.vanderbilt.vm.guide.ui.PlaceDetailerFragment");
		frag.mPlace = getPlaceById(ctx, id);
		return frag;
	}
	
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
		
		tvPlaceName = (TextView) mView.findViewById(R.id.PlaceName);
		tvPlaceHours = (TextView) mView.findViewById(R.id.PlaceHours);
		tvPlaceDesc = (TextView) mView.findViewById(R.id.PlaceDescription);
		ivPlaceImage = (ImageView) mView.findViewById(R.id.PlaceImage);
		
		setHasOptionsMenu(true);
		
		if (mPlace == null) {
			Bundle args = getArguments();
			logger.debug("Using information from argument");
			if (args != null) {
				int placeId = args.getInt(GuideConstants.PLACE_ID_EXTRA, 
						GuideConstants.BAD_PLACE_ID);
				mPlace = getPlaceById(getActivity(), placeId);
			}
		}
		
		updateInformation();
		
		/* Check if this place is already on Agenda */
		if(GlobalState.getUserAgenda().isOnAgenda(mPlace)) {
			isOnAgenda = true;
		} else {
			isOnAgenda = false;
		}
		
		
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		
		menu.clear();
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
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
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
	
	/**
	 * To change the Place detailed on the page.
	 * @param plc
	 */
	void setPlaceDetailed(Place plc) {
		mPlace = plc;
		updateInformation();
	}
	
	/*
	 * Update the information showed in the various Views based on mPlace
	 */
	private void updateInformation() {
		tvPlaceName.setText(mPlace.getName());
		tvPlaceHours.setText(mPlace.getHours());
		tvPlaceDesc.setText(mPlace.getDescription());
		ivPlaceImage.setImageBitmap(GlobalState.getBitmapForPlace(mPlace));
		
		// add to History
		GlobalState.addHistory(mPlace);
	}
	
	private static Place getPlaceById(Context ctx, int id) {
		// XXX: We can get a null place here right now.  This intentionally not
		// being handled at the moment.  I want the app to crash if we get a
		// null place so we'll get a stack trace and find out what went wrong.
		// We'll handle null places at a later time (after we've switched to a
		// Content Provider model instead of a list-based model).
		GuideDBOpenHelper helper = new GuideDBOpenHelper(ctx);
		SQLiteDatabase db = helper.getReadableDatabase();
		Place place = DBUtils.getPlaceById(id, db);
		db.close();
		return place;
	}
	
}