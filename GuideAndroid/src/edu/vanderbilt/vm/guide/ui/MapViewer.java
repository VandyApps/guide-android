package edu.vanderbilt.vm.guide.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;

import edu.vanderbilt.vm.guide.R;
import edu.vanderbilt.vm.guide.container.Place;
import edu.vanderbilt.vm.guide.db.GuideDBOpenHelper;
import edu.vanderbilt.vm.guide.util.DBUtils;
import edu.vanderbilt.vm.guide.util.GlobalState;
import edu.vanderbilt.vm.guide.util.GuideConstants;

@TargetApi(11)
public class MapViewer extends Activity {

	@SuppressWarnings("unused")
	private static final int MEDIUM_ZOOM = 18;
	static final int BUILDING_ZOOM = 19;	// high zoom for viewing individual building
	static final int WIDE_ZOOM = 16;		// wider zoom for viewing whole campus
	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger("ui.MapViewer");
	private static final String MAP_AGENDA = "map_agenda";
	private static final String MAP_FOCUS = "map_focus";
	private static final String MAP_CURRENT = "map_current";

	private ActionBar mAction;
	private Menu mMenu;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		MapFragment frag = null;
		
		Intent i = this.getIntent();
		if (i.hasExtra(MAP_FOCUS)) {
			/* 
			 * If the intent come with a PlaceId:
			 * - center the map to that place
			 * - show marker for that place only
			 */
			
			Place plc = getPlaceById(this, i.getIntExtra(MAP_FOCUS, -1));
			frag = PlaceMapFrag.newInstance(this, plc);
			
		} else if (i.hasExtra(MAP_AGENDA)) {
			/*
			 * If not, then:
			 * - show markers for all places on the agenda
			 * - center the map to current location
			 */
			
			frag = AgendaMapFrag.newInstance(this,GlobalState.getUserAgenda());
			
		} else if (i.hasExtra(MAP_CURRENT)) {
			frag = SelfMapFragment.newInstance(this);
		}
		
		LinearLayout layout = new LinearLayout(this);
		layout.setId(1001);
		{
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			ft.add(1001, frag, "map_fragment");
			ft.commit();
		}
		setContentView(layout);
		setupActionBar();
	}
	// ---------- END onCreate() ---------- //
	
	// ---------- BEGIN setup and lifecycle related methods ---------- //
	private void setupActionBar() {
		mAction = getActionBar();
		mAction.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		mAction.setDisplayShowTitleEnabled(true);
		mAction.setDisplayHomeAsUpEnabled(true);
		mAction.setBackgroundDrawable(GuideConstants.DECENT_GOLD);
	}
	
	public boolean onCreateOptionsMenu(Menu menu){
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.map_viewer, menu);
	    mMenu = menu;
	    mMenu.findItem(R.id.map_menu_remove_agenda).setVisible(false);
		mMenu.findItem(R.id.map_menu_add_agenda).setVisible(false);
	    return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item){
		
		switch (item.getItemId()){
		case android.R.id.home:
			GuideMain.open(this);
			return true;
		case R.id.menu_about:
			About.open(this);
			return true;
		default:
			return false;
		}
	}
	
	/**
	 * Open the map, with markers on each Places in the Agenda
	 * @param ctx
	 */
	public static void openAgenda(Context ctx) {
		Intent i = new Intent(ctx, MapViewer.class);
		i.putExtra(MAP_AGENDA, 0);
		ctx.startActivity(i);
	}
	
	/**
	 * Open the map, focused on a Place with high zoom.
	 * @param ctx
	 * @param placeid
	 */
	public static void openPlace(Context ctx, int placeid) {
		Intent i = new Intent(ctx, MapViewer.class);
		i.putExtra(MAP_FOCUS, placeid);
		ctx.startActivity(i);
	}
	
	/**
	 * Open the map showing the device's current location
	 * @param ctx
	 */
	public static void openCurrent(Context ctx) {
		Intent i = new Intent(ctx, MapViewer.class);
		i.putExtra(MAP_CURRENT, 0);
		ctx.startActivity(i);
	}
	
	/**
	 * 
	 * @param ctx
	 * @param tourId
	 */
	public static void openTour(Context ctx, int tourId) {
		//TODO
	}
	
	/**
	 * 
	 * @param ctx
	 * @param tourId
	 */
	public static void getTourMapFragment(Context ctx, int tourId) {
		//TODO
	}
	
	/**
	 * Creates a MapFragment which centers over the Place specified in the
	 * argument
	 * 
	 * @param ctx
	 * @param placeId
	 * @return
	 */
	public static PlaceMapFrag getPlaceMapFragment(Context ctx, int placeId) {
		Place plc = getPlaceById(ctx, placeId);
		
		return PlaceMapFrag.newInstance(ctx, plc);
	}
	
	/**
	 * Creates a MapFragment with markers on each Places in the Agenda
	 * 
	 * @param ctx
	 * @return
	 */
	public static AgendaMapFrag getAgendaMapFragment(Context ctx) {
		return AgendaMapFrag.newInstance(ctx, GlobalState.getUserAgenda());
	}
	
	// ---------- END setup and lifecycle related methods ---------- //

	// ---------- BEGIN classes and other methods ---------- //
	
	/* 
	 * @author athran
	 * Extracts the coordinate information from Location or Place
	 * and create a GeoPoint from it
	 */
	static LatLng toLatLng(Place plc) {
		return new LatLng(plc.getLatitude(), plc.getLongitude());
	}
	
	static LatLng toLatLng(Location loc) {
		return new LatLng(loc.getLatitude(), loc.getLongitude());
	}
	
	/*
	 * Static utility methods commonly used by all the MapFragments
	 */
	static void addToAgenda(Context ctx, int placeId) {
		GlobalState.getUserAgenda().add(getPlaceById(ctx, placeId));
		
		if (ctx != null) {
			Toast.makeText(ctx, "Added to Agenda", Toast.LENGTH_SHORT).show();
		}
	}
	
	static void removeFromAgenda(Context ctx, int placeId) {
		GlobalState.getUserAgenda().remove(getPlaceById(ctx, placeId));
		
		if (ctx != null) {
			Toast.makeText(ctx, "Removed from Agenda", Toast.LENGTH_SHORT).show();
		}
	}
	
	static Place getPlaceById(Context ctx, int placeId) {
		GuideDBOpenHelper helper = new GuideDBOpenHelper(ctx);
		SQLiteDatabase db = helper.getReadableDatabase();
		Place place = DBUtils.getPlaceById(placeId, db);
		db.close();
		
		return place;
	}
	
	// Centers the camera in the middle of the campus on a wide zoom
	static void resetCamera(GoogleMap map) {
		CameraUpdate update = CameraUpdateFactory.newLatLngZoom(
				new LatLng(36.145205, -86.803987),
				MapViewer.WIDE_ZOOM);
		map.moveCamera(update);
	}
	// ---------- END classes and other methods ---------- //
	
}
