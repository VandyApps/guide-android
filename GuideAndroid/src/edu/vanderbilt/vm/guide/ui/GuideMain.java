
package edu.vanderbilt.vm.guide.ui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import android.view.KeyEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import edu.vanderbilt.vm.guide.R;
import edu.vanderbilt.vm.guide.container.Agenda;
import edu.vanderbilt.vm.guide.ui.SearchDialog.SearchConfig;
import edu.vanderbilt.vm.guide.ui.SearchDialog.SearchConfigReceiver;
import edu.vanderbilt.vm.guide.ui.listener.FragmentTabListener;
import edu.vanderbilt.vm.guide.util.Geomancer;
import edu.vanderbilt.vm.guide.util.GlobalState;
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
    
    private static final String FRAG_PLACES = "places";
    private static final String FRAG_HOME = "agenda";
    private static final String FRAG_TOUR = "tours";
    
    private ActionBar mAction;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupActionBar();
        
        Geomancer.activateGeolocation(this);
        
        if (savedInstanceState != null) {
            mAction.setSelectedNavigationItem(savedInstanceState.getInt(TAB_CACHE, 0));
        } else {
            mAction.setSelectedNavigationItem(1);
        }
        
        try {
            
            File output = new File(getExternalFilesDir(null).getAbsolutePath() + GuideConstants.CACHE_FILENAME);
            FileInputStream fis = new FileInputStream(output);
            
            //String path = cache.getAbsolutePath() + GuideConstants.CACHE_FILENAME;
            
            //LOGGER.info(path);
            
            JsonReader reader = new JsonReader(new InputStreamReader(fis));
            String name;
            
            if (reader.peek() == JsonToken.END_DOCUMENT) {
                //LOGGER.info("No cache found.");
                
            } else {
            
                reader.beginObject();
                while (reader.hasNext()) {
                    name = reader.nextName();
                    if (name.equals(GuideConstants.CACHE_TAG_AGENDA)) {
                        
                        GlobalState.getUserAgenda().coalesce(Agenda.build(this, reader));
                        
                    } else {
                        //LOGGER.info("skipped name: " + name);
                        reader.skipValue();
                    }
                }
                reader.endObject();
                
            }
        } catch (Exception e) {
            e.printStackTrace();
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
        
        Tab tab;
        tab = mAction
                .newTab()
                .setText("Places")
                .setTabListener(new FragmentTabListener<PlaceTabFragment>(this, FRAG_PLACES, PlaceTabFragment.class));
        mAction.addTab(tab);
        
        tab = mAction
                .newTab()
                .setText("Agenda")
                .setTabListener(new FragmentTabListener<AgendaFragment>(this, FRAG_HOME, AgendaFragment.class));
        mAction.addTab(tab);
        
        tab = mAction
                .newTab()
                .setText("Tours")
                .setTabListener(new FragmentTabListener<TourFragment>(this, FRAG_TOUR, TourFragment.class));
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        Agenda agenda = GlobalState.getUserAgenda();
        //LOGGER.info("onStop is called");
        
        try {
            FileOutputStream fos = new FileOutputStream(
                    getExternalFilesDir(null).getAbsolutePath() + GuideConstants.CACHE_FILENAME);
            
            JsonWriter writer = new JsonWriter(new OutputStreamWriter(fos));
            
            //LOGGER.info("Opening output stream");
            
            writer.beginObject();
            writer.name(GuideConstants.CACHE_TAG_AGENDA);
            agenda.write(writer);
            
            //LOGGER.info("Agenda done writing stuff");
            
            writer.endObject();
            writer.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
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
        
        // TODO SQL query
        
        
        Cursor cursor = null;
        //((PlaceTabFragment) getSupportFragmentManager().findFragmentByTag(FRAG_PLACES)).viewListFromCursor(cursor);
        
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_SEARCH) {
            SearchDialog.newInstance(this).show(getSupportFragmentManager(), "search_dialog");
            return true;

        } else {
            return super.onKeyUp(keyCode, event);
        }
    }

}
