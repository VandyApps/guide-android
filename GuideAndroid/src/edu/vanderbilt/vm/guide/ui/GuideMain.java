package edu.vanderbilt.vm.guide.ui;

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
import edu.vanderbilt.vm.guide.R;
import edu.vanderbilt.vm.guide.container.Place;
import edu.vanderbilt.vm.guide.db.GuideDBOpenHelper;
import edu.vanderbilt.vm.guide.ui.listener.ActivityTabListener;
import edu.vanderbilt.vm.guide.ui.listener.FragmentTabListener;
import edu.vanderbilt.vm.guide.util.Geomancer;
import edu.vanderbilt.vm.guide.util.GlobalState;

@TargetApi(13)
public class GuideMain extends Activity {
	private ActionBar mAction;

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
		mAction = getActionBar();
		mAction.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		mAction.setDisplayShowTitleEnabled(true);
		mAction.setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_bg));
		
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

		Tab tab = mAction
				.newTab()
				.setText("Map")
				.setTabListener(
						new MyActivityTabListener(this, ViewMapActivity.class));
		mAction.addTab(tab, 0, false);

		tab = mAction.newTab()
				.setText("Places")
				.setTabListener(
						new FragmentTabListener<PlaceTabFragment>(this,
								"places", PlaceTabFragment.class));
		mAction.addTab(tab, 1, placesSelected);

		tab = mAction.newTab()
				.setText("Agenda")
				.setTabListener(
						new FragmentTabListener<AgendaFragment>(this, "agenda",
								AgendaFragment.class));
		mAction.addTab(tab, 2, agendaSelected);
		
		tab = mAction.newTab()
				.setText("Tours")
				.setTabListener(
						new FragmentTabListener<TourFragment>(this, "tours",
								TourFragment.class));
		mAction.addTab(tab, 3, toursSelected);

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
