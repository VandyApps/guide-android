package edu.vanderbilt.vm.guide.ui;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import edu.vanderbilt.vm.guide.R;

@SuppressLint("NewApi")
public class TourFragment extends Fragment {
	
	private GridView mGridView;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tour, container, false);
    }
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
//		mGridView = (GridView) getView().findViewById(R.id.tour_grid_view);
//		mGridView.setAdapter(adapter)
	}

}
