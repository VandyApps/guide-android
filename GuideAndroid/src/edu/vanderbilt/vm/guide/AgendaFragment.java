package edu.vanderbilt.vm.guide;

import android.annotation.TargetApi;
import android.app.ListFragment;
import android.os.Bundle;
import edu.vanderbilt.vm.guide.util.GlobalState;

@TargetApi(13)
public class AgendaFragment extends ListFragment {

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setListAdapter(new AgendaAdapter(getActivity(), GlobalState.getUserAgenda()));
	}
	
}
