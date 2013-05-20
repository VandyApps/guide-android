package edu.vanderbilt.vm.guide.ui.controllers;

import edu.vanderbilt.vm.guide.ui.ActTourEditor;
import edu.vanderbilt.vm.guide.ui.ActTourEditor.ATEMemo;
import edu.vanderbilt.vm.guide.ui.TourManagerFragment;

public class TourManagerController extends Controller {

/**
 * Return an instance of this controller. I may make it a Singleton later on if
 * need be.
 * 
 * @param activity
 * @param containerID where to put the views
 * @return an instance of TourManagerController
 */
public static TourManagerController getInstance(
        ActTourEditor activity, 
        int containerID) {
    TourManagerController cont = new TourManagerController();
    cont.mActivity = activity;
    cont.mContainerID = containerID;
    cont.init();
    return cont;
}

/**
 * Initialize the controller. This include creating views.
 */
public void init() {
    mFragment = TourManagerFragment.getInstance(this);
    mActivity.getSupportFragmentManager()
            .beginTransaction()
            .replace(mContainerID, mFragment)
            .commit();

    mState = TMNormalState.getInstance();
}

// Objects for View operations
private ActTourEditor mActivity;
private TourManagerFragment mFragment;
private int mContainerID;
private ControllerState mState;

// Private constructor. Do not use.
private TourManagerController() {
}

@Override
public boolean handleMessage(int what, Object data) {
    switch (what) {
    case ATEMemo.MESSAGE_BACKPRESSED:
        return mState.handleMessage(what);

    case ATEMemo.EVENT_CREATE_CLICKED:
        // Switch to the creation page
        mActivity.setController(
                TourEditorController.getInstance(
                        mActivity, 
                        mContainerID, 
                        0));
        return true;

    case ATEMemo.EVENT_DELETE_CLICKED:
        
        return false;

    case ATEMemo.EVENT_MAP_CLICKED:
        // TODO Once the new Tour data structure is done
        return false;

    default:
        return false;
    }
}

// One of TourManagerController's States
private static class TMNormalState implements ControllerState {

public static TMNormalState getInstance() {
    TMNormalState state = new TMNormalState();
    return state;
}

private TMNormalState() {
}

@Override
public boolean handleMessage(int what) {
    return handleMessage(what, null);
}

@Override
public boolean handleMessage(int what, Object data) {
    switch (what) {
    case ATEMemo.MESSAGE_BACKPRESSED:
        return false;
    
    default: return false;
    }
}

@Override
public void dispose() {
    // TODO Auto-generated method stub

}

}


// One of TourManager's States


}
