package edu.vanderbilt.vm.guide.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

import edu.vanderbilt.vm.guide.R;
import edu.vanderbilt.vm.guide.container.Agenda;
import edu.vanderbilt.vm.guide.container.Place;
import edu.vanderbilt.vm.guide.db.GuideDBOpenHelper;
import edu.vanderbilt.vm.guide.ui.listener.ActivityTabListener;
import edu.vanderbilt.vm.guide.util.Geomancer;
import edu.vanderbilt.vm.guide.util.GlobalState;
import edu.vanderbilt.vm.guide.util.GuideConstants;

@TargetApi(11)
public class ViewMapActivity extends MapActivity {

	private static final int MEDIUM_ZOOM = 18;
	private static final int BUILDING_ZOOM = 20;	// high zoom for viewing individual building
	private static final int WIDE_ZOOM = 16;		// wider zoom for viewing whole campus
	private static final Logger logger = LoggerFactory.getLogger("ui.ViewMapActivity");
	private static final String MAP_AGENDA = "map_agenda";
	private static final String MAP_FOCUS = "map_focus";
	private static final int VIEWING_PLACE = 666;
	private static final int VIEWING_AGENDA = 999;

	private Timer mUpdateLocation;
	private MapView mMapView;
	private MyLocationOverlay mDevice;
	private ActionBar mAction;
	private Menu mMenu;
	private int mPlaceIdFocused;
	private int mMapState;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);
		
		setupActionBar();
		
		/* Begin customizing MapView [athran] */
		mMapView = (MapView)findViewById(R.id.mapview);
		mMapView.setBuiltInZoomControls(true);
		mDevice = new MyLocationOverlay(this, mMapView);
		
			// Controller set where and how the map points to
		MapController control = mMapView.getController();
		List<Overlay> masterOverlay = mMapView.getOverlays();
		control.setZoom(WIDE_ZOOM);
		control.setCenter(convToGeoPoint(
				GlobalState.getPlaceById(GuideConstants.DEFAULT_ID)));
		
		Intent i = this.getIntent();
		if (i.hasExtra(MAP_FOCUS)){
			/*
			 * If the intent come with a PlaceId:
			 * - center the map to that place
			 * - show marker for that place only
			 */
			
			// a little fancy animation
			Place mapFocus = GlobalState.getPlaceById(i.getExtras()
					.getInt(GuideConstants.MAP_FOCUS));
			control.animateTo(convToGeoPoint(mapFocus));
			control.setZoom(BUILDING_ZOOM);
			
			// drawing the marker for one Place
			Drawable marker = (Drawable)getResources().getDrawable(R.drawable.marker);
			marker.setBounds(0, 0, marker.getIntrinsicWidth(), marker.getIntrinsicHeight());
			masterOverlay.add(new PlacesOverlay(marker,mapFocus));
			
			// We got a badass state machine here
			mMapState = VIEWING_PLACE;
			mPlaceIdFocused = mapFocus.getUniqueId();
			
		} else if (i.hasExtra(MAP_AGENDA)){
			/*
			 * If not, then:
			 * - show markers for all places on the agenda
			 * - center the map to current location
			 */
			
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
					/*
					 * It seems that Google put some black magic into the MyLocationOverlay
					 * because it can detect the current location faster and more accurately
					 * than Geomancer.
					 * 
					 * Until Geomancer's accuracy is improved, this is the temporary
					 * solution to get current location.
					 */
					Geomancer.setDeviceLocation(mDevice.getLastFix());
					mMapView.getController().animateTo(mDevice.getMyLocation());
					mMapView.getController().setZoom(MEDIUM_ZOOM);
				}
			});
			masterOverlay.add(mDevice);
			
			// Give it the bad Place Id
			mPlaceIdFocused = -1;
			mMapState = VIEWING_AGENDA;
		}
		
		/* End customizing MapView */
		
	}
	// ---------- END onCreate() ---------- //
	
	// ---------- BEGIN setup and lifecycle related methods ---------- //
	public void onPause(){
		super.onPause();
		cancelUpdater();
		mDevice.disableMyLocation();
		mDevice.disableCompass();
	}
	
	public void onResume(){
		super.onResume();
		mDevice.enableMyLocation();
		mDevice.enableCompass();
	}
	
	private void cancelUpdater(){
		if (mUpdateLocation != null){
			mUpdateLocation.cancel();
			logger.trace("Updater is cancelled.");
		}
	}

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
	    inflater.inflate(R.menu.map_view_activity, menu);
	    mMenu = menu;
	    
	    if (mMapState == VIEWING_PLACE){
			MenuItem item = mMenu.findItem(R.id.map_menu_add_agenda);
			item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | 
					MenuItem.SHOW_AS_ACTION_WITH_TEXT);
			
			GuideDBOpenHelper helper = new GuideDBOpenHelper(this);
			SQLiteDatabase db = helper.getReadableDatabase();
			Place place = Place.getPlaceById(mPlaceIdFocused, db);
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
			Place place = Place.getPlaceById(mPlaceIdFocused, db);
			db.close();
			
			GlobalState.getUserAgenda().add(place);
			
			Toast.makeText(this, "Added to Agenda", Toast.LENGTH_SHORT).show();
			return true;
		case R.id.map_menu_remove_agenda:
			
			GuideDBOpenHelper helper2 = new GuideDBOpenHelper(this);
			SQLiteDatabase db2 = helper2.getReadableDatabase();
			Place place2 = Place.getPlaceById(mPlaceIdFocused, db2);
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
	
	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
	
	/**
	 * Open the map, with markers on each Places in the Agenda
	 * @param ctx
	 */
	public static void openAgenda(Context ctx){
		Intent i = new Intent(ctx, ViewMapActivity.class);
		i.putExtra(MAP_AGENDA, "");
		ctx.startActivity(i);
	}
	
	/**
	 * Open the map, focused on a Place with high zoom.
	 * @param ctx
	 * @param placeid
	 */
	public static void openPlace(Context ctx, int placeid){
		Intent i = new Intent(ctx, ViewMapActivity.class);
		i.putExtra(MAP_FOCUS, placeid);
		ctx.startActivity(i);
	}
	
	public static void openList(Context ctx, Bundle list){
		Intent i = new Intent(ctx, ViewMapActivity.class);
		i.putExtras(list);
		ctx.startActivity(i);
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
					Intent i = new Intent(ViewMapActivity.this, PlaceDetailActivity.class);
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
			 * TODO clicking on the map pins should lead to the PlaceDetailActivity
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
	
	
	// ---------- END classes and other methods ---------- //
	
}
