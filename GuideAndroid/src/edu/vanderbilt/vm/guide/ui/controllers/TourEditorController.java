package edu.vanderbilt.vm.guide.ui.controllers;

import edu.vanderbilt.vm.guide.ui.ActTourEditor;
import edu.vanderbilt.vm.guide.ui.ActTourEditor.ATEMemo;
import edu.vanderbilt.vm.guide.ui.TourEditorFragment;

public class TourEditorController extends Controller {


/**
 * Return an instance of this controller. I may make it a Singleton later on
 * if need be.
 * 
 * @param activity
 * @param containerID
 * @param tourID
 * @return an instance of TourEditorController
 */
public static TourEditorController getInstance(
        ActTourEditor activity,
        int containerID,
        int tourID)
{
    TourEditorController cont = new TourEditorController();
    cont.mActivity = activity;
    cont.mContainerID = containerID;
    cont.mTourID = tourID;
    cont.init();
    return cont;
}


//Objects for View operations
private ActTourEditor mActivity;
private TourEditorFragment mFragment;
private int mContainerID;
private int mTourID;

/*
 * Private constructor. Do not use.
 */
private TourEditorController() {
}


private void init() {
    mFragment = TourEditorFragment.getInstance(this);
    mActivity.getSupportFragmentManager()
            .beginTransaction()
            .replace(mContainerID, mFragment)
            .commit();
    
}


@Override
public boolean handleMessage(int what, Object data) {
    switch (what) {
    case ATEMemo.TEC_MESSAGE_DONE:
        String fakeTour = (String) data;
        ActTourEditor.mModel.add(fakeTour);
        mActivity.setController(
                TourManagerController.getInstance(mActivity, mContainerID));
        return true;
    
    default: return false;
    }
}



}









