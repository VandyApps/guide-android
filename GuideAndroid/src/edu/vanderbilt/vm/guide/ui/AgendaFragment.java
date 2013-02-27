
package edu.vanderbilt.vm.guide.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.annotation.TargetApi;
import android.app.ListFragment;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;
import edu.vanderbilt.vm.guide.R;
import edu.vanderbilt.vm.guide.ui.adapter.AgendaAdapter;
import edu.vanderbilt.vm.guide.util.GlobalState;

@TargetApi(13)
public class AgendaFragment extends ListFragment {

    private static final Logger logger = LoggerFactory.getLogger("ui.AgendaFragment");

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setListAdapter(new AgendaAdapter(getActivity(), GlobalState.getUserAgenda()));

        // Add an empty agenda indicator
        TextView emptyIndicator = new TextView(getActivity());
        emptyIndicator.setLayoutParams(new ViewGroup.LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
        emptyIndicator.setGravity(Gravity.CENTER);
        emptyIndicator.setTextSize(16);
        emptyIndicator.setText("Your agenda is empty.  Add places"
                + " to your agenda by pressing the + " + "button at the top right of the screen "
                + "when viewing a tour or location.");
        emptyIndicator.setVisibility(View.GONE);
        ((ViewGroup)getListView().getParent()).addView(emptyIndicator);
        getListView().setEmptyView(emptyIndicator);
        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        logger.trace("AgendaFragment: OnResume called");
    }

    public void onReselect() {
        try {
            getListView().invalidateViews();
        } catch (IllegalStateException e) {
            logger.info("Caught IllegalStateException: ", e);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_sort_alphabetic:
                GlobalState.getUserAgenda().sortAlphabetically();
                this.getListView().invalidateViews();
                Toast.makeText(getActivity(), "Agenda is sorted alphabetically", Toast.LENGTH_SHORT)
                        .show();
                return true;

            case R.id.menu_sort_distance:
                GlobalState.getUserAgenda().sortByDistance();
                this.getListView().invalidateViews();
                Toast.makeText(getActivity(), "Agenda is sorted by distance", Toast.LENGTH_SHORT)
                        .show();
                return true;
            default:
                return false;
        }
    }

}
