
package edu.vanderbilt.vm.guide.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.MenuItem;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import edu.vanderbilt.vm.guide.R;
import edu.vanderbilt.vm.guide.ui.adapter.AgendaAdapter;
import edu.vanderbilt.vm.guide.util.GlobalState;

public class AgendaFragment extends SherlockFragment {

    private static final Logger logger = LoggerFactory.getLogger("ui.AgendaFragment");

    private View mRoot;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRoot = inflater.inflate(R.layout.single_list, container, false);
        return mRoot;
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        
        ListView lv = (ListView) mRoot.findViewById(R.id.s_l_listview1);
        
        lv.setAdapter(new AgendaAdapter(getActivity(), GlobalState.getUserAgenda()));

        // Add an empty agenda indicator
        TextView emptyIndicator = (TextView) getActivity().getLayoutInflater().inflate(R.layout.agenda_empty, null, false);
        ((ViewGroup) lv.getParent()).addView(emptyIndicator);
        lv.setEmptyView(emptyIndicator);
        
        setHasOptionsMenu(true);
    }

    /* TODO Remove if unnecessary or done debugging
    @Override
    public void onResume() {
        super.onResume();
        logger.trace("AgendaFragment: OnResume called");
    }
    */
    
    public void onReselect() {
        try {
            ((ListView) mRoot.findViewById(R.id.s_l_listview1)).invalidateViews();
            
        } catch (IllegalStateException e) {
            logger.info("Caught IllegalStateException: ", e);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_sort_alphabetic:
                GlobalState.getUserAgenda().sortAlphabetically();
                ((ListView) mRoot.findViewById(R.id.s_l_listview1)).invalidateViews();
                Toast.makeText(getActivity(), "Agenda is sorted alphabetically", Toast.LENGTH_SHORT)
                        .show();
                return true;

            case R.id.menu_sort_distance:
                GlobalState.getUserAgenda().sortByDistance();
                ((ListView) mRoot.findViewById(R.id.s_l_listview1)).invalidateViews();
                Toast.makeText(getActivity(), "Agenda is sorted by distance", Toast.LENGTH_SHORT)
                        .show();
                return true;
                
            default:
                return false;
        }
    }

}
