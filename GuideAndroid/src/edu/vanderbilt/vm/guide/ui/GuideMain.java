
package edu.vanderbilt.vm.guide.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import edu.vanderbilt.vm.guide.R;
import edu.vanderbilt.vm.guide.ui.adapter.SwipingTabsAdapter;
import edu.vanderbilt.vm.guide.util.Geomancer;
import edu.vanderbilt.vm.guide.util.GuideConstants;

/**
 * The main Activity of the Guide app. Contains the 4 main tabs: Map, Tours,
 * Places, and Agenda. Currently the launch activity.
 * 
 * @author nicholasking
 */
public class GuideMain extends SherlockFragmentActivity {

    @SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory.getLogger("ui.GuideMain");

    private ActionBar mAction;

    private ViewPager mViewPager;

    private SwipingTabsAdapter mTabsAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Geomancer.activateGeolocation(this);

        setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_guide_main);

        setupActionBar();

        if (savedInstanceState != null) {
            mAction.setSelectedNavigationItem(savedInstanceState.getInt("tab", 0));
        }
    }

    /*
     * Configure the action bar with the appropriate tabs and options
     */
    private void setupActionBar() {
        mAction = getSupportActionBar();
        mAction.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        mAction.setDisplayShowTitleEnabled(true);
        mAction.setBackgroundDrawable(GuideConstants.OLD_GOLD);
        mAction.setSplitBackgroundDrawable(GuideConstants.OLD_GOLD);
        mAction.setTitle(getResources().getText(R.string.university_name));

        mViewPager = (ViewPager)findViewById(R.id.swiper_1);
        mTabsAdapter = new SwipingTabsAdapter(this, mViewPager);
        mTabsAdapter.addTab(mAction.newTab().setText("Places"), PlaceTabFragment.class, null);
        mTabsAdapter.addTab(mAction.newTab().setText("Agenda"), AgendaFragment.class, null);
        mTabsAdapter.addTab(mAction.newTab().setText("Tours"), TourFragment.class, null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.activity_guide_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.menu_map:
                MapViewer.openAgenda(this);
                return true;

            case R.id.menu_about:
                About.open(this);
                return true;
            case R.id.menu_navigator:
                Navigator.open(this);
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle state) {
        state.putInt("tab", mAction.getSelectedTab().getPosition());
    }

    // ---------- END setup and lifecycle related methods ---------- //

    /**
     * Use this method to return to the Main. This will clear all in the stack
     * 
     * @param ctx
     */
    public static void open(Context ctx) {
        Intent i = new Intent(ctx, GuideMain.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        ctx.startActivity(i);
    }

}
