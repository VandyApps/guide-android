
package edu.vanderbilt.vm.guide.ui;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;

import edu.vanderbilt.vm.guide.R;
import edu.vanderbilt.vm.guide.container.Agenda;
import edu.vanderbilt.vm.guide.util.GuideConstants;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.ArrayAdapter;

public class Navigator extends SherlockFragmentActivity {
    
    private static final String CHOOSER = "chooser";
    
    private static final String MAP = "map";
    
    private int mNavChoice = 0;
    private PlaceNavigatorFragment mPlaceNavFrag;
    private AgendaNavigatorFragment mAgendaNavFrag;
    private AgendaMapFrag mAgendaMapFrag;

    @SuppressWarnings({
            "unchecked", "rawtypes"
    })
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigator_main);
        
        // Setup Navigation on ActionBar
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(GuideConstants.OLD_GOLD);

        // Specify that a dropdown list should be displayed in the action bar.
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

        actionBar.setListNavigationCallbacks(
                // Specify a SpinnerAdapter to populate the dropdown list.
                new ArrayAdapter(
                        actionBar.getThemedContext(),
                        android.R.layout.simple_list_item_1,
                        android.R.id.text1,
                        new String[]{ "Place to Place", "Through the Agenda" }),

                // Provide a listener to be called when an item is selected.
                new ActionBar.OnNavigationListener() {
                    @Override
                    public boolean onNavigationItemSelected(
                            int position, long id) {
                        // Take action here, e.g. switching to the
                        // corresponding fragment.
                        
                        switchFragments(position, id);
                        
                        return true;
                    }
                });
        
        // Setup fragments
        FragmentTransaction ft = this.getSupportFragmentManager().beginTransaction();
        mAgendaMapFrag = AgendaMapFrag.newInstance(this, new Agenda());
        mPlaceNavFrag = new PlaceNavigatorFragment();
        mPlaceNavFrag.setGraphMapper(mAgendaMapFrag);
        mAgendaNavFrag = new AgendaNavigatorFragment();
        mAgendaNavFrag.setGraphMapper(mAgendaMapFrag);
        
        ft.add(R.id.nav_map, mAgendaMapFrag, MAP);
        ft.add(R.id.nav_chooser, mPlaceNavFrag,CHOOSER);
        
        ft.commit();
    }
    
    
    
    void setDisplayAgenda(Agenda a) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.remove( fm.findFragmentByTag(MAP) );
        ft.add(R.id.nav_map, AgendaMapFrag.newInstance(this, a), MAP);
        ft.commit();
    }
    
    private void switchFragments(int position, long id) {
        mAgendaMapFrag.clearMap();
        switch (position) {
            case 0:
                // Travel from Place to Place
                if (mNavChoice != 0) {
                    FragmentManager fm = getSupportFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    ft.remove( fm.findFragmentByTag(CHOOSER) );
                    ft.add(R.id.nav_chooser, mPlaceNavFrag, CHOOSER);
                    ft.commit();
                    mNavChoice = 0;
                }
                return;
                
            case 1:
                // Travel through the Agenda
                if (mNavChoice != 1) {
                    FragmentManager fm = getSupportFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    ft.remove( fm.findFragmentByTag(CHOOSER) );
                    ft.add(R.id.nav_chooser, mAgendaNavFrag, CHOOSER);
                    ft.commit();
                    mNavChoice = 1;
                }
                return;
                
            default:
                return;
        }
    }
    
    /**
     * Navigate to this Activity from another Activity.
     * 
     * @param ctx
     */
    public static void open(Context ctx) {

        Intent i = new Intent(ctx, Navigator.class);
        ctx.startActivity(i);

    }

}
