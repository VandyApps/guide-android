
package edu.vanderbilt.vm.guide.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import edu.vanderbilt.vm.guide.R;
import edu.vanderbilt.vm.guide.ui.SearchDialog.SearchConfig;
import edu.vanderbilt.vm.guide.ui.SearchDialog.SearchConfigReceiver;
import edu.vanderbilt.vm.guide.ui.listener.FragmentTabListener;
import edu.vanderbilt.vm.guide.util.Geomancer;
import edu.vanderbilt.vm.guide.util.GuideConstants;

/**
 * The main Activity of the Guide app. Contains the 4 main tabs: Map, Tours,
 * Places, and Agenda. Currently the launch activity.
 * 
 * @author nicholasking
 */
public class GuideMain extends SherlockFragmentActivity implements SearchConfigReceiver {

    @SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory.getLogger("ui.GuideMain");

    private static final String TAB_CACHE = "tab_cache";
    
    private ActionBar mAction;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupActionBar();
        
        Geomancer.activateGeolocation(this);
        
        if (savedInstanceState != null) {
            mAction.setSelectedNavigationItem(savedInstanceState.getInt(TAB_CACHE, 0));
        }
    }

    /*
     * Configure the action bar with the appropriate tabs and options
     */
    private void setupActionBar() {
        mAction = getSupportActionBar();
        mAction.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        mAction.setDisplayShowTitleEnabled(true);
        mAction.setBackgroundDrawable(GuideConstants.DECENT_GOLD);
        mAction.setSplitBackgroundDrawable(GuideConstants.DECENT_GOLD);
        mAction.setTitle(getResources().getText(R.string.university_name));
        
        Tab tab;
        tab = mAction
                .newTab()
                .setText("Places")
                .setTabListener(new FragmentTabListener<PlaceTabFragment>(this, "places", PlaceTabFragment.class));
        mAction.addTab(tab);
        
        tab = mAction
                .newTab()
                .setText("Agenda")
                .setTabListener(new FragmentTabListener<AgendaFragment>(this, "agenda", AgendaFragment.class));
        mAction.addTab(tab);
        
        tab = mAction
                .newTab()
                .setText("Tours")
                .setTabListener(new FragmentTabListener<TourFragment>(this, "tours", TourFragment.class));
        mAction.addTab(tab);
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getSupportMenuInflater().inflate(R.menu.activity_guide_main, menu);
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
                
            case R.id.menu_search:
                SearchDialog.newInstance(this).show(getSupportFragmentManager(), "search_dialog");
                return true;
                
            default:
                return false;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle state) {
        state.putInt(TAB_CACHE, mAction.getSelectedTab().getPosition());
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

    @Override
    public void receiveSearchConfig(SearchConfig config) {
        // TODO Auto-generated method stub
        
    }

}
