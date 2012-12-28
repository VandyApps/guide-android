package edu.vanderbilt.vm.guide.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.widget.LinearLayout;
import edu.vanderbilt.vm.guide.R;
import edu.vanderbilt.vm.guide.container.Place;
import edu.vanderbilt.vm.guide.db.GuideDBOpenHelper;
import edu.vanderbilt.vm.guide.util.DBUtils;
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
	private ActionBar mAction;
	
	private static final Logger logger = LoggerFactory
			.getLogger("ui.PlaceDetailer");
	private static final String PLACE_ID_EXTRA = "placeId";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Fragment frag = PlaceDetailerFragment.newInstance(getIntent()
				.getIntExtra(GuideConstants.PLACE_ID_EXTRA, -1));
		
		LinearLayout layout = new LinearLayout(this);
		layout.setId(1000);
		{
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			ft.add(1000, frag, "detailer_fragment");
			ft.commit();
		}
		setContentView(layout);
		//setContentView(R.layout.activity_place_detail);
		
		//Setup ActionBar
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
}
