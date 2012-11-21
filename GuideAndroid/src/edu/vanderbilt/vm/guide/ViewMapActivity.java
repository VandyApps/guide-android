package edu.vanderbilt.vm.guide;

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
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

import edu.vanderbilt.vm.guide.util.ActivityTabListener;
import edu.vanderbilt.vm.guide.util.Agenda;
import edu.vanderbilt.vm.guide.util.Geomancer;
import edu.vanderbilt.vm.guide.util.GlobalState;
import edu.vanderbilt.vm.guide.util.GuideConstants;
import edu.vanderbilt.vm.guide.util.Place;

@TargetApi(11)
public class ViewMapActivity extends MapActivity {
	private static final int DEFAULT_ZOOM_LEVEL = 17;
	private static final int BUILDING_ZOOM = 20;
	private static final int WIDE_ZOOM = 15;
	private static final Logger logger = LoggerFactory.getLogger("ui.ViewMapActivity");
	private Timer mUpdateLocation;
	private MapView mMapView;
	private int UPDATE_ID;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);
		setupActionBar();
		
		/* Begin customizing MapView [athran] */
		mMapView = (MapView)findViewById(R.id.mapview);
		mMapView.setBuiltInZoomControls(true);
		
			// Controller set where and how the map points to
		MapController control = mMapView.getController();
		List<Overlay> masterOverlay = mMapView.getOverlays();
		control.setZoom(DEFAULT_ZOOM_LEVEL);	//Default zoom level, covers about half of campus
		
		Intent i = this.getIntent();
		if (i.hasExtra("map_focus")){
			/*
			 * If the intent come with a PlaceId:
			 * - center the map to that place
			 * - show marker for that place only
			 */
			Place MapFocus = GlobalState.getPlaceById(i.getExtras().getInt("map_focus"));
			control.setCenter(convToGeoPoint(MapFocus));
			control.setZoom(BUILDING_ZOOM);	// Higher zoom level for individual building
			
			Drawable marker = (Drawable)getResources().getDrawable(R.drawable.marker);
			marker.setBounds(0, 0, marker.getIntrinsicWidth(), marker.getIntrinsicHeight());
			masterOverlay.add(new PlacesOverlay(marker,MapFocus));
		} else {
			/*
			 * If not, then:
			 * - show markers for all places on the agenda
			 * - center the map to current location
			 */
			
			control.setZoom(WIDE_ZOOM);
			Drawable marker = (Drawable)getResources().getDrawable(R.drawable.marker_agenda);
			marker.setBounds(0, 0, marker.getIntrinsicWidth(), marker.getIntrinsicHeight());
			masterOverlay.add(new AgendaOverlay(marker));
			setMapFocus(true);
			
			mUpdateLocation = new Timer();
			mUpdateLocation.schedule(new TimerTask(){
					@Override
					public void run(){
						setMapFocus(false);
						logger.info("Updater", "Focusing map to current location.");
					}
				}, 5000L,5000L);

		}
		
		MyLocationOverlay self = new MyLocationOverlay(this, mMapView);
		masterOverlay.add(self);
		/* End customizing MapView */
		
		
	}
	
	public void onPause(){
		super.onPause();
		cancelUpdater();
	}
	
	public void onStop(){
		super.onStop();
		cancelUpdater();
	}
	
	public void onDestroy(){
		super.onDestroy();
		cancelUpdater();
	}
	
	private void cancelUpdater(){
		if (mUpdateLocation != null){
			mUpdateLocation.cancel();
			logger.trace("Updater is cancelled.");
		}
	}

	private void setupActionBar() {
		ActionBar ab = getActionBar();
		ab.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		ab.setDisplayShowTitleEnabled(false);

		Tab tab = ab.newTab().setText("Map") //TODO Enumerate these tab names maybe?
				.setTabListener(new DummyTabListener());
		ab.addTab(tab);

		tab = ab.newTab().setText("Tours").setTabListener(new ActivityTabListener(this, GuideMain.class, 1));
		ab.addTab(tab);
		
		tab = ab.newTab().setText("Places")
				.setTabListener(new ActivityTabListener(this, GuideMain.class, 2));
		ab.addTab(tab);
		
		tab = ab.newTab().setText("Agenda")
				.setTabListener(new ActivityTabListener(this, GuideMain.class, 3));
		ab.addTab(tab);
	}
	
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
						convToGeoPoint(mAgendaList.get(j)),	// GeoPoint
						mAgendaList.get(j).getName(),		// Pin tag
						"A Place in Vanderbilt. This is a ShortDescription"));// Pin snippet
			}

			populate();
		}
		
		 /*
		  * (non-Javadoc)
		  * @see com.google.android.maps.ItemizedOverlay#onTap(int)
		  * 
		  * Tapping on a marker brings you to the place's PlaceDetailActivity.
		  * This is assuming that both list in this class share the same index
		  * which they should
		  */
		protected boolean onTap(int index){
			Intent i = new Intent()	.setClass(ViewMapActivity.this, PlaceDetailActivity.class)
									.putExtra(GuideConstants.PLACE_ID_EXTRA, 
											mAgendaList.get(index).getUniqueId());
			startActivity(i);
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
					convToGeoPoint(pl),
					pl.getName(),
					"A Place in Vanderbilt. This is a ShortDescription"));
			
			populate();
		}
		
		public PlacesOverlay(Drawable marker, Location loc){
			super(marker);
			this.marker = marker;
			boundCenterBottom(marker);
			mItemList.add(new OverlayItem(
					convToGeoPoint(loc),
					"Current Location",
					""));
			
			populate();
		}
		
		protected boolean onTap(int i){
			/**
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
	
	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

	private static class DummyTabListener implements ActionBar.TabListener {

		@Override
		public void onTabSelected(Tab tab, FragmentTransaction ft) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onTabUnselected(Tab tab, FragmentTransaction ft) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onTabReselected(Tab tab, FragmentTransaction ft) {
			// TODO Auto-generated method stub

		}

	}
	
	/* @author athran
	 * Extracts the coordinate information from Location or Place
	 * and create a GeoPoint from it
	 */
	private static GeoPoint convToGeoPoint(Location loc){
		return new GeoPoint((int)(loc.getLatitude()*1000000),(int)(loc.getLongitude()*1000000));
	}
	
	private static GeoPoint convToGeoPoint(Place place){
		return new GeoPoint((int)(place.getLatitude()*1000000),(int)(place.getLongitude()*1000000));
	}
	/* End utility methods */
	
	private void setMapFocus(boolean first){
		// Marker for CurrentLocation
		Place currPlace = null;
		Location loc = Geomancer.getDeviceLocation();
		if (loc != null){
			currPlace = Geomancer.findClosestPlace(loc, GlobalState.getPlaceList(this));
			logger.trace("I found our location. We are in {}", currPlace.getName());
		} else {
			logger.warn("ViewMapActivity","Location service failed to get location data.");
		}
		
		if (currPlace == null){
			/*
			 * As a last resort, set default Place to FGH.
			 */
			logger.warn("MapViewActivity","Failed to get Device location.");
			currPlace = GlobalState.getPlaceById(1);
		}
		
		mMapView.getController().setCenter(convToGeoPoint(currPlace));
		Drawable marker_self = (Drawable)getResources().getDrawable(R.drawable.marker_device);
		marker_self.setBounds(0, 0, marker_self.getIntrinsicWidth(), 
				marker_self.getIntrinsicHeight());
		Drawable crosshair = (Drawable)getResources().getDrawable(R.drawable.device_location);
		int n = crosshair.getIntrinsicHeight()/2;
		crosshair.setBounds(-n, -n, n, n);
		
		List<Overlay> overlay = mMapView.getOverlays();
		if (first){
			UPDATE_ID = overlay.size();
			overlay.add(new PlacesOverlay(crosshair, loc));
			overlay.add(new PlacesOverlay(marker_self, currPlace));
		} else {
			overlay.set(UPDATE_ID, new PlacesOverlay(crosshair, loc));
			overlay.set(UPDATE_ID + 1, new PlacesOverlay(marker_self, currPlace));
			logger.trace("Overlay size: {}", overlay.size());
		}
		
	}
}
