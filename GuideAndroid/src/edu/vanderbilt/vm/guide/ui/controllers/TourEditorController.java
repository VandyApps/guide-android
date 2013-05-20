package edu.vanderbilt.vm.guide.ui.controllers;

import com.actionbarsherlock.app.SherlockFragmentActivity;

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
        SherlockFragmentActivity activity,
        int containerID,
        int tourID)
{
    TourEditorController cont = new TourEditorController();
    cont.mActivity = activity;
    cont.mContainerID = containerID;
    cont.mTourID = tourID;
    return cont;
}


//Objects for View operations
private SherlockFragmentActivity mActivity;
private int mContainerID;
private int mTourID;


// Messages
public static final int MESSAGE_DONE = 0;


/*
 * Private constructor. Do not use.
 */
private TourEditorController() {
}


@Override
public boolean handleMessage(int what, Object data) {
    // TODO Auto-generated method stub
    return false;
}



}









