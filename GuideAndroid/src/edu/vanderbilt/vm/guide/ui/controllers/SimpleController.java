package edu.vanderbilt.vm.guide.ui.controllers;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.actionbarsherlock.app.ActionBar;

import edu.vanderbilt.vm.guide.ui.ActTourEditor;

/*
 * An interface for the interaction between the Activity with its States.
 * The Activity acts as a Controller. It delegates message handling and view
 * managements to its child states.
 */
public interface SimpleController {

	FragmentManager fm();

	ActionBar ab();

	void view(Fragment fragment);

	void setControllerState(ControllerState state);

}