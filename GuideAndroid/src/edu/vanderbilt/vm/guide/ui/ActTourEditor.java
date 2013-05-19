package edu.vanderbilt.vm.guide.ui;

import android.content.Context;
import android.content.Intent;

import com.actionbarsherlock.app.SherlockFragmentActivity;

public class ActTourEditor extends SherlockFragmentActivity {

	/**
	 * Opens the Tour Editor Activity.
	 * 
	 * @param ctx
	 */
	public static void open(Context ctx) {

		Intent i = new Intent();
		i.setClass(ctx, ActTourEditor.class);
		ctx.startActivity(i);

	}
	

}








