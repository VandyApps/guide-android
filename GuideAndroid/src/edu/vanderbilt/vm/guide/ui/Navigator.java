
package edu.vanderbilt.vm.guide.ui;

import edu.vanderbilt.vm.guide.R;
import edu.vanderbilt.vm.guide.container.Agenda;
import edu.vanderbilt.vm.guide.util.GuideConstants;
import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;

public class Navigator extends Activity {
    
    private static final String CHOOSER = "chooser";
    
    private static final String MAP = "map";
    
    private int mNavChoice = 0;

    @SuppressWarnings({
            "unchecked", "rawtypes"
    })
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigator_main);
        
        // Setup Navigation on ActionBar
        final ActionBar actionBar = getActionBar();
        actionBar.setBackgroundDrawable(GuideConstants.OLD_GOLD);

        // Specify that a dropdown list should be displayed in the action bar.
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

        actionBar.setListNavigationCallbacks(
                // Specify a SpinnerAdapter to populate the dropdown list.
                new ArrayAdapter(
                        actionBar.getThemedContext(),
                        android.R.layout.simple_list_item_1,
                        android.R.id.text1,
                        new String[]{ "Place to Place", "Through the Agenda", 
                            "Through a Tour" }),

                // Provide a listener to be called when an item is selected.
                new ActionBar.OnNavigationListener() {
                    public boolean onNavigationItemSelected(
                            int position, long id) {
                        // Take action here, e.g. switching to the
                        // corresponding fragment.
                        
                        switchFragments(position, id);
                        
                        return true;
                    }
                });
        
        // Setup fragments
        FragmentTransaction ft = this.getFragmentManager().beginTransaction();
        
        ft.add(R.id.nav_map, AgendaMapFrag.newInstance(this, new Agenda()), MAP);
        ft.add(R.id.nav_chooser, new NavPlaceChooser(),CHOOSER);
        
        ft.commit();
        
        
    }
    
    
    
    void setDisplayAgenda(Agenda a) {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.remove( fm.findFragmentByTag(MAP) );
        ft.add(R.id.nav_map, AgendaMapFrag.newInstance(this, a), MAP);
        ft.commit();
    }
    
    private void switchFragments(int position, long id) {
        switch (position) {
            case 0:
                // Travel from Place to Place
                if (mNavChoice != 0) {
                    FragmentManager fm = getFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    ft.remove( fm.findFragmentByTag(CHOOSER) );
                    ft.add(R.id.nav_chooser, new NavPlaceChooser(), CHOOSER);
                    ft.commit();
                    mNavChoice = 0;
                }
                return;
                
            case 1:
                // Travel through the Agenda
                if (mNavChoice != 1) {
                    FragmentManager fm = getFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    ft.remove( fm.findFragmentByTag(CHOOSER) );
                    ft.add(R.id.nav_chooser, new NavAgendaChooser(), CHOOSER);
                    ft.commit();
                    mNavChoice = 1;
                }
                return;
                
            case 2:
                // Travel through a Tour
                if (mNavChoice != 2) {
                    FragmentManager fm = getFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    ft.remove( fm.findFragmentByTag(CHOOSER) );
                    ft.add(R.id.nav_chooser, new NavTourChooser(), CHOOSER);
                    ft.commit();
                    mNavChoice = 2;
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
