package edu.vanderbilt.vm.guide;

import java.io.IOException;
import java.util.List;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import edu.vanderbilt.vm.guide.util.ActivityTabListener;
import edu.vanderbilt.vm.guide.util.FragmentTabListener;
import edu.vanderbilt.vm.guide.util.GlobalState;
import edu.vanderbilt.vm.guide.util.Place;

@TargetApi(13)
public class GuideMain extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_guide_main);
		setupActionBar();
		try {
			List<Place> placeList = GlobalState.getPlaceList(this);
			GlobalState.getUserAgenda().add(placeList.get(1));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
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

		boolean toursSelected = isSelected(1, selection);
		boolean placesSelected = isSelected(2, selection);
		boolean agendaSelected = isSelected(3, selection)
				|| (!toursSelected && !placesSelected);

		Tab tab = ab
				.newTab()
				.setText("Map")
				.setTabListener(
						new MyActivityTabListener(this, ViewMapActivity.class));
		ab.addTab(tab, 0, false);

		tab = ab.newTab()
				.setText("Tours")
				.setTabListener(
						new FragmentTabListener<TourFragment>(this, "tours",
								TourFragment.class));
		ab.addTab(tab, 1, toursSelected);

		tab = ab.newTab()
				.setText("Places")
				.setTabListener(
						new FragmentTabListener<PlaceMainFragment>(this,
								"places", PlaceMainFragment.class));
		ab.addTab(tab, 2, placesSelected);

		tab = ab.newTab()
				.setText("Agenda")
				.setTabListener(
						new FragmentTabListener<AgendaFragment>(this, "agenda",
								AgendaFragment.class));
		ab.addTab(tab, 3, agendaSelected);

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
