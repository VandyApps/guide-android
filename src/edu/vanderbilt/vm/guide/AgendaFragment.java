package edu.vanderbilt.vm.guide;

import android.annotation.TargetApi;
import android.app.ListFragment;
import android.os.Bundle;
import android.widget.ArrayAdapter;

@TargetApi(13)
public class AgendaFragment extends ListFragment {

	private static final String[] AGENDA_LIST = { "Featheringill Hall", "Rand",
			"Stevenson", "Central Library" };

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setListAdapter(new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_list_item_1, AGENDA_LIST));
	}

}
