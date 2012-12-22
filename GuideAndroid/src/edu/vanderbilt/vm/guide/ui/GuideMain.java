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
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;
import edu.vanderbilt.vm.guide.R;
import edu.vanderbilt.vm.guide.container.Place;
import edu.vanderbilt.vm.guide.db.GuideDBOpenHelper;
import edu.vanderbilt.vm.guide.ui.adapter.SwipingTabsAdapter;
import edu.vanderbilt.vm.guide.ui.listener.ActivityTabListener;
import edu.vanderbilt.vm.guide.ui.listener.FragmentTabListener;
import edu.vanderbilt.vm.guide.util.Geomancer;
import edu.vanderbilt.vm.guide.util.GlobalState;
import edu.vanderbilt.vm.guide.util.GuideConstants;

/**
 * The main Activity of the Guide app.  Contains the 4 main tabs:
 * Map, Tours, Places, and Agenda.  Currently the launch activity.
 * @author nicholasking
 *
 */
@TargetApi(13)
public class GuideMain extends Activity {

	private ActionBar mAction;
	ViewPager mViewPager;
    SwipingTabsAdapter mTabsAdapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Geomancer.activateGeolocation(this);
		
		mViewPager = new ViewPager(this);
        mViewPager.setId(R.id.swiper_1);
        setContentView(mViewPager);

        mAction = getActionBar();
        mAction.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        mAction.setDisplayOptions(0, ActionBar.DISPLAY_SHOW_TITLE);

        mTabsAdapter = new SwipingTabsAdapter(this, mViewPager);
        mTabsAdapter.addTab(mAction.newTab().setText("Places"),
        		PlaceTabFragment.class, null);
        mTabsAdapter.addTab(mAction.newTab().setText("Agenda"),
        		AgendaFragment.class, null);
        mTabsAdapter.addTab(mAction.newTab().setText("Tours"),
        		TourFragment.class, null);
        mTabsAdapter.addTab(mAction.newTab().setText("Stats"),
        		StatsFragment.class, null);
        
        if (savedInstanceState != null) {
            mAction.setSelectedNavigationItem(savedInstanceState.getInt("tab", 0));
        }
	}

	/**
	 * Configure the action bar with the appropriate tabs and options
	 */
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

		Tab tab = mAction.newTab()
				.setText("Places")
				.setTabListener(
						new FragmentTabListener<PlaceTabFragment>(this,
								"places", PlaceTabFragment.class));
		mAction.addTab(tab, 0, placesSelected);

		tab = mAction.newTab()
				.setText("Agenda")
				.setTabListener(
						new FragmentTabListener<AgendaFragment>(this, "agenda",
								AgendaFragment.class));
		mAction.addTab(tab, 1, agendaSelected);
		
		tab = mAction.newTab()
				.setText("Tours")
				.setTabListener(
						new FragmentTabListener<TourFragment>(this, "tours",
								TourFragment.class));
		mAction.addTab(tab, 2, toursSelected);
		
		tab = mAction.newTab()
				.setText("Stats")
				.setTabListener( //TODO
						new FragmentTabListener<StatsFragment>(this, "stats",
								StatsFragment.class));
		mAction.addTab(tab, 3, false);
		
	}
	
	public boolean onCreateOptionsMenu(Menu menu){
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.activity_guide_main, menu);
	    return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item){
		
		switch (item.getItemId()){
		case R.id.menu_map:
			Intent i = new Intent(this, ViewMapActivity.class);
			i.putExtra(GuideConstants.MAP_AGENDA, "");
			startActivity(i);
			return true;
		case R.id.menu_refresh:
			//TODO
			Toast.makeText(this, "Current Location Updated", Toast.LENGTH_SHORT).show();
			return true;
		
		default: return false;
		}
	}
	
	public void onSaveInstanceState(Bundle state){
		state.putInt("tab", mAction.getSelectedTab().getPosition());
	}
	// ---------- END setup and lifecycle related methods ---------- //
	

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
