package edu.vanderbilt.vm.guide.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;

import edu.vanderbilt.vm.guide.R;
import edu.vanderbilt.vm.guide.ui.controllers.Controller;

/**
 * View for TourManagerController
 * 
 * @author athran
 *
 */
public class TourManagerFragment extends SherlockFragment {
	

// View Events
public static final int EVENT_CREATE_CLICKED = 3;
public static final int EVENT_DELETE_CLICKED = 4;
public static final int EVENT_MAP_CLICKED = 5;


/**
 * Change the view mode.
 * 
 * @param mode
 */
public void changeViewMode(int mode) {
    switch (mode) {
    case MODE_NORMAL:
        btnCreate.setVisibility(View.VISIBLE);
        btnDelete.setVisibility(View.VISIBLE);
        btnMap.setVisibility(View.VISIBLE);
        
        // Fill the panel
        mMainPane.removeAllViews(); // possibly recycle some views
        TextView tv;                // later
        
        for (String str : ActTourEditor.mModel) {
            tv = new TextView(getActivity());
            tv.setText(str);
            mMainPane.addView(tv);
        }
        
        return;
    
    case MODE_DELETING:
        btnCreate.setVisibility(View.GONE);
        btnDelete.setVisibility(View.VISIBLE);
        btnMap.setVisibility(View.GONE);
        
        // Fill the panel
        mMainPane.removeAllViews();
        CheckBox cb;
        
        for (String str : ActTourEditor.mModel) {
            cb = new CheckBox(getActivity());
            cb.setText(str);
            mMainPane.addView(cb);
        }
        
        return;
        
    case MODE_MAPPING:
        return;
        
    default: return;
    }
}


// View modes
public static final int MODE_NORMAL = 0;
public static final int MODE_DELETING = 1;
public static final int MODE_MAPPING = 2;

public static TourManagerFragment getInstance(Controller controller) {
    TourManagerFragment frag = new TourManagerFragment();
    frag.setRetainInstance(true);
    frag.mController = controller;
    return frag;
}


private View mRoot;
private Controller mController;

// View objects
private Button btnCreate;
private Button btnDelete;
private Button btnMap;
private LinearLayout mMainPane;


@Override
public View onCreateView(
		LayoutInflater inflater, 
		ViewGroup container, 
		Bundle savedInstanceState) 
{
    mRoot = inflater.inflate(R.layout.tour_manager, container, false);
    return mRoot;
}

@Override
public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    
    btnCreate = (Button) mRoot.findViewById(R.id.t_m_btn1);
    btnDelete = (Button) mRoot.findViewById(R.id.t_m_btn2);
    btnMap = (Button) mRoot.findViewById(R.id.t_m_btn3);
    mMainPane = (LinearLayout) mRoot.findViewById(R.id.t_m_mainpanel);
    
    btnCreate.setOnClickListener(new OnClickListener() {
        @Override public void onClick(View arg0) {
            mController.handleMessage(EVENT_CREATE_CLICKED);
        }
    });
    btnDelete.setOnClickListener(new OnClickListener() {
        @Override public void onClick(View arg0) {
            mController.handleMessage(EVENT_DELETE_CLICKED);
        }
    });
    btnMap.setOnClickListener(new OnClickListener() {
        @Override public void onClick(View arg0) {
            mController.handleMessage(EVENT_MAP_CLICKED);
        }
    });
}

	
}

















