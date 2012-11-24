package edu.vanderbilt.vm.guide.ui.listener;

import android.annotation.TargetApi;
import android.app.ActionBar.Tab;
import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;

/**
 * This TabListener launches a new Activity when the tab is clicked. This is
 * allows us to use MapActivity since there is no MapFragment provided by the
 * Google APIs.
 * 
 * @author nicholasking
 * 
 */
@TargetApi(11)
public class ActivityTabListener implements ActionBar.TabListener {

	private Context mContext;
	private Class<?> mTarget;
	private final int mSelectionInt;

	public ActivityTabListener(Context packageCtx, Class<?> target) {
		this(packageCtx, target, Integer.MIN_VALUE);
	}
	
	public ActivityTabListener(Context packageCtx, Class<?> target, int selectionInt) {
		mContext = packageCtx;
		mTarget = target;
		mSelectionInt = selectionInt;
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		Intent intent = new Intent().setClass(mContext, mTarget).addFlags(
				Intent.FLAG_ACTIVITY_NO_HISTORY);
		if (mSelectionInt != Integer.MIN_VALUE) {
			intent.putExtra("selection", mSelectionInt);
		}
		mContext.startActivity(intent);

	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		// Do nothing
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		// Do nothing
	}

}
