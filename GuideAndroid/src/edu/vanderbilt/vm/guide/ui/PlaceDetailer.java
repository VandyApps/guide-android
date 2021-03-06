
package edu.vanderbilt.vm.guide.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import edu.vanderbilt.vm.guide.R;
import edu.vanderbilt.vm.guide.util.GuideConstants;

/**
 * The activity that shows the details page for a place. This activity shows the
 * place name, description, picture, hours, etc. It also allows the user to add
 * or remove the place from the agenda and pin the place on the map.
 * 
 * @author nicholasking, athran
 */
public class PlaceDetailer extends SherlockFragmentActivity {
    private ActionBar mAction;
    
    @SuppressWarnings("unused")
    private static final Logger logger = LoggerFactory.getLogger("ui.PlaceDetailer");

    private static final String PLACE_ID_EXTRA = "placeId";

    private static final String FRAG = "detail";
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_pane);
        
        // Setup ActionBar
        mAction = getSupportActionBar();
        mAction.setTitle("Place Details");
        mAction.setDisplayHomeAsUpEnabled(true);
        mAction.setBackgroundDrawable(GuideConstants.DECENT_GOLD);
        
        // Setup the detailer fragment
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        SherlockFragment frag = (SherlockFragment)getSupportFragmentManager().findFragmentByTag(FRAG);
        
        if (frag == null) {
            frag = PlaceDetailerFragment.newInstance(this, getIntent()
                .getIntExtra(PLACE_ID_EXTRA, -1));
            ft.add(R.id.sp_pane1, frag, FRAG);
        }
        
        ft.commit();
    }

    /**
     * Use this method to open the Details page
     * 
     * @param ctx The starting Activity
     * @param placeid The Id of the Place that you want to detail
     */
    public static void open(Context ctx, int placeid) {
        Intent i = new Intent(ctx, PlaceDetailer.class);
        i.putExtra(PLACE_ID_EXTRA, placeid);
        ctx.startActivity(i);
    }
}
