package edu.vanderbilt.vm.guide.ui;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import edu.vanderbilt.vm.guide.R;
import edu.vanderbilt.vm.guide.ui.listener.ActivityTabListener;
import edu.vanderbilt.vm.guide.ui.listener.FragmentTabListener;
import edu.vanderbilt.vm.guide.util.Geomancer;

/**
 * The main Activity of the Guide app.  Contains the 4 main tabs:
 * Map, Tours, Places, and Agenda.  Currently the launch activity.
 * @author nicholasking
 *
 */
@TargetApi(13)
public class GuideMain extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_guide_main);
		setupActionBar();
		Geomancer.activateGeolocation(this);
	}

	/**
	 * Configure the action bar with the appropriate tabs and options
	 */
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

	/**
	 * Determine whether a tab is selected
	 * @param n The number of the tab to test for (ex: 
	 * 			if Tours tab is #3, then n=3)
	 * @param selection The Integer that has the selected tab
	 * @return True if tab number n is selected, false otherwise
	 */
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
