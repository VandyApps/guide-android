package edu.vanderbilt.vm.guide.ui;

import android.annotation.TargetApi;
import android.app.ListFragment;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import edu.vanderbilt.vm.guide.R;
import edu.vanderbilt.vm.guide.ui.adapter.AgendaAdapter;
import edu.vanderbilt.vm.guide.util.GlobalState;

@TargetApi(13)
public class AgendaFragment extends ListFragment {
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setListAdapter(new AgendaAdapter(getActivity(), 
				GlobalState.getUserAgenda()));
		setHasOptionsMenu(true);
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_sort_alphabetic:
			GlobalState.getUserAgenda().sortAlphabetically();
			this.getListView().invalidateViews();
			Toast.makeText(getActivity(), "Agenda is sorted alphabetically",
					Toast.LENGTH_SHORT).show();
			return true;
		
		case R.id.menu_sort_distance:
			GlobalState.getUserAgenda().sortByDistance();
			this.getListView().invalidateViews();
			Toast.makeText(getActivity(), "Agenda is sorted by distance",
					Toast.LENGTH_SHORT).show();
			return true;
		default: return false;
		}
	}
	
}
