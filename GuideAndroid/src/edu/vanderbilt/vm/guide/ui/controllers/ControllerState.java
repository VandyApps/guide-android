package edu.vanderbilt.vm.guide.ui.controllers;

/*
 * Represents the states that the Activity(Controller) could be in.
 */
public interface ControllerState {

	boolean handleMessage(int what);

	boolean handleMessage(int what, Object data);

	void dispose();

}