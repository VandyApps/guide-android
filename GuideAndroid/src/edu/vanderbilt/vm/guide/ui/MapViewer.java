package edu.vanderbilt.vm.guide.ui;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.OverlayItem;

import edu.vanderbilt.vm.guide.R;
import edu.vanderbilt.vm.guide.container.Agenda;
import edu.vanderbilt.vm.guide.container.Place;
import edu.vanderbilt.vm.guide.db.GuideDBOpenHelper;
import edu.vanderbilt.vm.guide.util.DBUtils;
import edu.vanderbilt.vm.guide.util.Geomancer;
import edu.vanderbilt.vm.guide.util.GlobalState;
import edu.vanderbilt.vm.guide.util.GuideConstants;

@TargetApi(11)
public class MapViewer extends Activity {

	private static final int MEDIUM_ZOOM = 18;
	private static final int BUILDING_ZOOM = 20;	// high zoom for viewing individual building
	private static final int WIDE_ZOOM = 16;		// wider zoom for viewing whole campus
	private static final Logger logger = LoggerFactory.getLogger("ui.MapViewer");
	private static final String MAP_AGENDA = "map_agenda";
	private static final String MAP_FOCUS = "map_focus";
	private static final String MAP_CURRENT = "map_current";
	private static final int VIEWING_PLACE = 666;
	private static final int VIEWING_AGENDA = 999;

	private MapView mMapView;
	private MyLocationOverlay mDevice;
	private ActionBar mAction;
	private Menu mMenu;
	private int mPlaceIdFocused;
	private int mMapState;
	private GoogleMap mMap;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_map);
		
		MapFragment frag = MapFragment.newInstance();
		
		LinearLayout layout = new LinearLayout(this);
		layout.setId(1001);
		{
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			ft.add(1001, frag, "map_fragment");
			ft.commit();
		}
		setContentView(layout);
		setupActionBar();
		
		// Begin customizing Map
		frag = (MapFragment) getFragmentManager()
				.findFragmentByTag("map_fragment");
		if (frag == null) {
			Toast.makeText(this, "Map is not available. Please contact the" 
					+ " developer as noted in the (!) page for troubleshooting"
					, Toast.LENGTH_LONG).show();
			return;
		}
		mMap = frag.getMap();
		
		if (mMap == null) {
			Toast.makeText(this, "Map is not available. Please contact the" 
					+ " developer as noted in the (!) page for troubleshooting"
					, Toast.LENGTH_LONG).show();
			return;
		}
		
		// Set camera to middle the of the campus initially
		CameraUpdate update = CameraUpdateFactory.newLatLngZoom(
				new LatLng(36.145205, -86.803987),
				WIDE_ZOOM);
		mMap.moveCamera(update);
		
		Intent i = this.getIntent();
		if (i.hasExtra(MAP_FOCUS)){
			/*
			 * If the intent come with a PlaceId:
			 * - center the map to that place
			 * - show marker for that place only
			 */
			
			Place mapFocus = GlobalState.getPlaceById(i.getExtras()
					.getInt(MAP_FOCUS));
			update = CameraUpdateFactory.newLatLngZoom(toLatLng(mapFocus), 
					BUILDING_ZOOM);
			mMap.animateCamera(update);
			
			/*
			// drawing the marker for one Place
			Drawable marker = (Drawable)getResources().getDrawable(R.drawable.marker);
			marker.setBounds(0, 0, marker.getIntrinsicWidth(), marker.getIntrinsicHeight());
			masterOverlay.add(new PlacesOverlay(marker,mapFocus));
			*/
			
			// We got a badass state machine here
			mMapState = VIEWING_PLACE;
			//mPlaceIdFocused = mapFocus.getUniqueId();
			
		} else if (i.hasExtra(MAP_AGENDA)){
			/*
			 * If not, then:
			 * - show markers for all places on the agenda
			 * - center the map to current location
			 */
			
			// Extract the location data from Agenda
			ArrayList<LatLng> geopointList = new ArrayList<LatLng>();
			for (Place plc : GlobalState.getUserAgenda()) {
				geopointList.add(toLatLng(plc));
			}
			
			// Calculate the bounds that cover all places in Agenda
			if (geopointList.size() == 0) {
				
			} else {
				double minLat = Double.MAX_VALUE;
				double maxLat = Double.MIN_VALUE;
				double minLng = Double.MAX_VALUE;
				double maxLng = Double.MIN_VALUE;
				
				for (LatLng point : geopointList) {
					if (point.latitude < minLat) {
						minLat = point.latitude;
					}
					if (point.latitude > maxLat) {
						maxLat = point.latitude;
					}
					if (point.longitude < minLng) {
						minLng = point.longitude;
					}
					if (point.longitude > minLng) {
						maxLng = point.longitude;
					}
				}
				
				// Sanitize that shit
				if (minLat > 90 || minLat < -90) { minLat = 0; }
				if (maxLat > 90 || maxLat < -90) { maxLat = 0; }
				if (minLng > 180 || minLng < -180) { minLng = 0; }
				if (maxLng > 180 || maxLng < -180) { maxLng = 0; }
				
				LatLngBounds bounds = new LatLngBounds(
						new LatLng(minLat, minLng),new LatLng(maxLat, maxLng));
				update = CameraUpdateFactory.newLatLngBounds(bounds, 10);
				mMap.animateCamera(update);
			}
			/*
			// drawing the marker for everything in Agenda
			Drawable marker = (Drawable)getResources()
					.getDrawable(R.drawable.marker_agenda);
			marker.setBounds(0, 0, marker.getIntrinsicWidth(), 
					marker.getIntrinsicHeight());
			masterOverlay.add(new AgendaOverlay(marker));
			
			// drawing the MyLocation dot
			mDevice.enableMyLocation();
			mDevice.enableCompass();
			mDevice.runOnFirstFix(new Runnable(){
				@Override
				public void run() {
					
					 * It seems that Google put some black magic into the MyLocationOverlay
					 * because it can detect the current location faster and more accurately
					 * than Geomancer.
					 * 
					 * Until Geomancer's accuracy is improved, this is the temporary
					 * solution to get current location.
					 
					Geomancer.setDeviceLocation(mDevice.getLastFix());
					mMapView.getController().animateTo(mDevice.getMyLocation());
					mMapView.getController().setZoom(MEDIUM_ZOOM);
				}
			});
			masterOverlay.add(mDevice);
			*/
			
			// Give it the bad Place Id
			mPlaceIdFocused = -1;
			mMapState = VIEWING_AGENDA;
		} else if (i.hasExtra(MAP_CURRENT)) {
			
		}
		
		/* End customizing MapView */
		
	}
	// ---------- END onCreate() ---------- //
	
	// ---------- BEGIN setup and lifecycle related methods ---------- //
	public void onPause(){
		super.onPause();
		cancelUpdater();
		//mDevice.disableMyLocation();
		//mDevice.disableCompass();
	}
	
	public void onResume(){
		super.onResume();
		//mDevice.enableMyLocation();
		//mDevice.enableCompass();
	}
	
	private void cancelUpdater(){	}

	private void setupActionBar() {
		mAction = getActionBar();
		mAction.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		mAction.setDisplayShowTitleEnabled(true);
		mAction.setDisplayHomeAsUpEnabled(true);
		mAction.setBackgroundDrawable(
				new ColorDrawable(Color.rgb(189, 187, 14)));
	}
	
	public boolean onCreateOptionsMenu(Menu menu){
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.map_viewer, menu);
	    mMenu = menu;
	    
	    if (mMapState == VIEWING_PLACE){
			MenuItem item = mMenu.findItem(R.id.map_menu_add_agenda);
			item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | 
					MenuItem.SHOW_AS_ACTION_WITH_TEXT);
			
			GuideDBOpenHelper helper = new GuideDBOpenHelper(this);
			SQLiteDatabase db = helper.getReadableDatabase();
			Place place = DBUtils.getPlaceById(mPlaceIdFocused, db);
			db.close();
			
			if (GlobalState.getUserAgenda().isOnAgenda(place)){
				// Option to remove
				
				
			} else {
				// Option to add
			}
			
	    } else if (mMapState == VIEWING_AGENDA){
	    	
	    }
	    
	    return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item){
		
		switch (item.getItemId()){
		case R.id.map_menu_add_agenda:
			// TODO add the place to agenda
			// Must coordinate with AgendaOverlay
			
			GuideDBOpenHelper helper = new GuideDBOpenHelper(this);
			SQLiteDatabase db = helper.getReadableDatabase();
			Place place = DBUtils.getPlaceById(mPlaceIdFocused, db);
			db.close();
			
			GlobalState.getUserAgenda().add(place);
			
			Toast.makeText(this, "Added to Agenda", Toast.LENGTH_SHORT).show();
			return true;
		case R.id.map_menu_remove_agenda:
			
			GuideDBOpenHelper helper2 = new GuideDBOpenHelper(this);
			SQLiteDatabase db2 = helper2.getReadableDatabase();
			Place place2 = DBUtils.getPlaceById(mPlaceIdFocused, db2);
			db2.close();
			
			GlobalState.getUserAgenda().remove(place2);
			Toast.makeText(this, "Removed from Agenda", Toast.LENGTH_SHORT)
				.show();
			return true;
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
	
	public static void openTour(Context ctx, int tourId) {
		//TODO
	}
	
	public static void getTourMapFragment(Context ctx, int touId) {
		//TODO
	}
	
	public static void getPlaceMapFragment(Context ctx, int placeId) {
		//TODO
	}
	
	// ---------- END setup and lifecycle related methods ---------- //
	
	// ---------- BEGIN classes and other methods ---------- //
	
	/* @author athran
	 * these subclasses defines the layer that contains
	 * the Place pins on the map
	 * code from page 454
	 */
	private class AgendaOverlay extends ItemizedOverlay<OverlayItem>{
		/*
		 * Show all places on the agenda as pins on the map
		 */
		private List<OverlayItem> mItemList = new ArrayList<OverlayItem>();
		private Drawable marker = null;
		private List<Place> mAgendaList = new ArrayList<Place>();
		private int mClicked = -1;
		private RelativeLayout mPopup = (RelativeLayout)findViewById(R.map.popup);

		public AgendaOverlay(Drawable marker){
			super(marker);
			this.marker = marker;	
			boundCenterBottom(marker);
			Agenda agenda = GlobalState.getUserAgenda();
			
			// Why don't Agenda have a getList() ? TODO
			for (int i = 0; i<agenda.size();i++){
				mAgendaList.add(agenda.get(i));
			}
			
			// transcribing AgendaList into ItemList
			// may not be the best way to do it (?)
			for (int j = 0;j<mAgendaList.size();j++){
				mItemList.add(new OverlayItem(
					convToGeoPoint(mAgendaList.get(j)),						// GeoPoint
					mAgendaList.get(j).getName(),							// Pin tag
					"A Place in Vanderbilt. This is a ShortDescription"));	// Pin snippet
			}

			populate();
		}
		
		 /*
		  * Tapping on a marker brings up a popup that tell the name of the Place
		  * and its distance from current location.
		  * 
		  * This assumes that both lists in this class share the same index
		  * which they should
		  */
		protected boolean onTap(int index){
			
			// if the same marker is tapped again, the popup is dismissed
			if (mClicked == index){
				mClicked = -1;
				mPopup.setVisibility(View.GONE);
				mPlaceIdFocused = -1;
				return true;
			}
			mClicked = index;
			
			Place pl = mAgendaList.get(mClicked);
			mPlaceIdFocused = pl.getUniqueId();
			
			if (GlobalState.getUserAgenda().isOnAgenda(pl)){
				// Option to remove
				(mMenu.findItem(R.id.map_menu_remove_agenda))
					.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS |
							MenuItem.SHOW_AS_ACTION_WITH_TEXT);
			} else {
				// Option to add
				(mMenu.findItem(R.id.map_menu_add_agenda))
					.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS |
							MenuItem.SHOW_AS_ACTION_WITH_TEXT);
			}
			
			Location locA = new Location("pointA");
			locA.setLatitude(pl.getLatitude());
			locA.setLongitude(pl.getLongitude());
			
			// Setup what is on the popup card
			((TextView)mPopup.findViewById(R.map.popup_name)).setText(pl.getName());
			String dist = (int)(mDevice.getLastFix().distanceTo(locA)) + " yards away";
			((TextView)mPopup.findViewById(R.map.popup_desc)).setText(dist);
			
			// setup the popup's appearance
            mPopup.setLayoutParams(new MapView.LayoutParams(
            		MapView.LayoutParams.WRAP_CONTENT, 
            		MapView.LayoutParams.WRAP_CONTENT, 
                    mItemList.get(index).getPoint(), 
                    0, 
                    -marker.getIntrinsicHeight(), 
                    MapView.LayoutParams.BOTTOM_CENTER));
            mPopup.setVisibility(View.VISIBLE);
            
            // Clicking the popup should bring you to the Detail page
            OnClickListener listener = new OnClickListener(){
				@Override
				public void onClick(View v) {
					Intent i = new Intent(MapViewer.this, PlaceDetailer.class);
					i.putExtra(GuideConstants.PLACE_ID_EXTRA , mAgendaList.get(mClicked).getUniqueId());
				}
    		};
            mPopup.setOnClickListener(listener);
            ((TextView)mPopup.findViewById(R.map.popup_name)).setOnClickListener(listener);
			
			mMapView.getController().animateTo(
					mItemList.get(mClicked).getPoint());
			
			return true;
		}
		
		protected OverlayItem createItem(int i){
			return mItemList.get(i);
		}
		
		public int size(){
			return mItemList.size();
		}
		
	}
	
	private class PlacesOverlay extends ItemizedOverlay<OverlayItem>{
		/*
		 * Place one marker on the Place only
		 */
		private List<OverlayItem> mItemList = new ArrayList<OverlayItem>();
		private Drawable marker = null;
		private Place mFocus;
		
		public PlacesOverlay(Drawable marker,Place pl){
			super(marker);
			this.marker = marker;
			boundCenterBottom(marker);
			mFocus = pl;
			mItemList.add(new OverlayItem(
				convToGeoPoint(pl),			// Geopoint
				pl.getName(),				// pin name
				"A Place in Vanderbilt"));	// pin snippet
			
			populate();
			
			Location locA = new Location("pointA");
			locA.setLatitude(pl.getLatitude());
			locA.setLongitude(pl.getLongitude());
			
			// Also add a popup
			RelativeLayout popup = (RelativeLayout) findViewById(R.map.popup);
			
			// Setup what is on the popup card
			((TextView)popup.findViewById(R.map.popup_name)).setText(pl.getName());
			String dist = (int)(Geomancer.getDeviceLocation()
					.distanceTo(locA)) + " yards away";
			((TextView)popup.findViewById(R.map.popup_desc)).setText(dist);
			
			// setup the popup's appearance
            popup.setLayoutParams(new MapView.LayoutParams(
            		MapView.LayoutParams.WRAP_CONTENT, 
            		MapView.LayoutParams.WRAP_CONTENT, 
                    convToGeoPoint(mFocus), 
                    0, 
                    -marker.getIntrinsicHeight(), 
                    MapView.LayoutParams.BOTTOM_CENTER));
            popup.setVisibility(View.VISIBLE);
		}
		
		public PlacesOverlay(Drawable marker, Location loc){
			super(marker);
			this.marker = marker;
			boundCenterBottom(marker);
			mItemList.add(new OverlayItem(
				convToGeoPoint(loc),		// Geopoint
				"Current Location",			// pin name
				"A Place in Vanderbilt"));	// pin snippet
			
			populate();
		}
		
		protected boolean onTap(int i){
			/*
			 * TODO clicking on the map pins should lead to the PlaceDetailer
			 */
			
			return true;
		}
		
		protected OverlayItem createItem(int i){
			return mItemList.get(i);
		}
		
		public int size(){
			return mItemList.size();
		}
	}
	/* End subclass */
	
	/* 
	 * @author athran
	 * Extracts the coordinate information from Location or Place
	 * and create a GeoPoint from it
	 */
	private static GeoPoint convToGeoPoint(Location loc){
		return new GeoPoint((int)(loc.getLatitude()*1000000),(int)(loc.getLongitude()*1000000));
	}
	
	private static GeoPoint convToGeoPoint(Place place){
		return new GeoPoint((int)(place.getLatitude()*1000000),(int)(place.getLongitude()*1000000));
	}
	
	private static LatLng toLatLng(Place plc) {
		return new LatLng(plc.getLatitude(), plc.getLongitude());
	}
	
	private static LatLng toLatLng(Location loc) {
		return new LatLng(loc.getLatitude(), loc.getLongitude());
	}
	// ---------- END classes and other methods ---------- //
	
}
