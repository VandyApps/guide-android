package edu.vanderbilt.vm.guide.ui.controllers;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;

import edu.vanderbilt.vm.guide.ui.ActTourEditor;
import edu.vanderbilt.vm.guide.ui.TourManagerFragment;

public class TourManagerController extends Controller {

/**
 * Return an instance of this controller. I may make it a Singleton later on if
 * need be.
 * 
 * @param activity
 * @param containerID
 *            where to put the views
 * @return an instance of TourManagerController
 */
public static TourManagerController getInstance(
        SherlockFragmentActivity activity, int containerID) {
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
    mFragment = new TourManagerFragment();

    mActivity.getSupportFragmentManager().beginTransaction()
            .replace(mContainerID, mFragment).commit();

}

// Objects for View operations
private SherlockFragmentActivity mActivity;
private TourManagerFragment mFragment;
private int mContainerID;
private ControllerState mState;

// Private constructor. Do not use.
private TourManagerController() {
}

@Override
public boolean handleMessage(int what, Object data) {
    switch (what) {
    case ActTourEditor.MESSAGE_BACKPRESSED:
        return mState.handleMessage(what);

    case TourManagerFragment.EVENT_CREATE_CLICKED:

        return false;

    case TourManagerFragment.EVENT_DELETE_CLICKED:

        return false;

    case TourManagerFragment.EVENT_MAP_CLICKED:

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
    // TODO Auto-generated method stub
    return false;
}

@Override
public boolean handleMessage(int what, Object data) {
    // TODO Auto-generated method stub
    return false;
}

@Override
public void dispose() {
    // TODO Auto-generated method stub

}

}

}
