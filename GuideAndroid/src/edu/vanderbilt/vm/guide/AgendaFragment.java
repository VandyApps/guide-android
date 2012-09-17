package edu.vanderbilt.vm.guide;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.widget.ArrayAdapter;

public class AgendaFragment extends ListFragment {

	private static final String[] AGENDA_LIST = { "Featheringill Hall", "Rand",
			"Stevenson", "Central Library" };

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		setListAdapter(new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_list_item_1, AGENDA_LIST));
	}

}
