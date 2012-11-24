package edu.vanderbilt.vm.guide;

import java.util.List;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import edu.vanderbilt.vm.guide.db.GuideDBOpenHelper;
import edu.vanderbilt.vm.guide.util.ActivityTabListener;
import edu.vanderbilt.vm.guide.util.FragmentTabListener;
import edu.vanderbilt.vm.guide.util.Geomancer;
import edu.vanderbilt.vm.guide.util.GlobalState;
import edu.vanderbilt.vm.guide.util.Place;

@TargetApi(13)
public class GuideMain extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_guide_main);
		setupActionBar();
		
		List<Place> placeList = GlobalState.getPlaceList(this);
		for (int i = 0; i < 7; i++){
			GlobalState.getUserAgenda().add(placeList.get(i));
		}
		
		Geomancer.activateGeolocation(this);
	}

	private void setupActionBar() {
		ActionBar ab = getActionBar();
		ab.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		ab.setDisplayShowTitleEnabled(false);

		Intent myIntent = getIntent();
		final Integer selection;
		if (myIntent != null && myIntent.hasExtra("selection")) {
			selection = (Integer) myIntent.getExtras().get("selection");
		} else {
			selection = null;
		}

		boolean toursSelected = isSelected(3, selection);
		boolean agendaSelected = isSelected(2, selection);
		boolean placesSelected = isSelected(1, selection)
				|| (!toursSelected && !agendaSelected);

		Tab tab = ab
				.newTab()
				.setText("Map")
				.setTabListener(
						new MyActivityTabListener(this, ViewMapActivity.class));
		ab.addTab(tab, 0, false);

		tab = ab.newTab()
				.setText("Places")
				.setTabListener(
						new FragmentTabListener<PlaceTabFragment>(this,
								"places", PlaceTabFragment.class));
		ab.addTab(tab, 1, placesSelected);

		tab = ab.newTab()
				.setText("Agenda")
				.setTabListener(
						new FragmentTabListener<AgendaFragment>(this, "agenda",
								AgendaFragment.class));
		ab.addTab(tab, 2, agendaSelected);
		
		tab = ab.newTab()
				.setText("Tours")
				.setTabListener(
						new FragmentTabListener<TourFragment>(this, "tours",
								TourFragment.class));
		ab.addTab(tab, 3, toursSelected);

	}

	private boolean isSelected(int n, Integer selection) {
		return selection != null && n == selection;
	}

	/**
	 * Adds a little hack to "forward" the user on to the map activity when the
	 * map tab is clicked. This effectively removes this activity from the back
	 * stack.
	 * 
	 * @author nicholasking
	 * 
	 */
	private class MyActivityTabListener extends ActivityTabListener {

		public MyActivityTabListener(Context packageCtx, Class<?> target) {
			super(packageCtx, target);
		}

		@Override
		public void onTabSelected(Tab tab, FragmentTransaction ft) {
			super.onTabSelected(tab, ft);
			finish();
		}

	}

}
