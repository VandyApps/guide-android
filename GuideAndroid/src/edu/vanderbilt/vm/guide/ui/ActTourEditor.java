package edu.vanderbilt.vm.guide.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockFragmentActivity;

import edu.vanderbilt.vm.guide.R;
import edu.vanderbilt.vm.guide.ui.controllers.Controller;
import edu.vanderbilt.vm.guide.ui.controllers.TourManagerController;

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


// Model
public static List<String> mModel = new ArrayList<String>(); 

// Controller
private Controller mController;
public void setController(Controller controller) {
    mController = controller;
}

// Messages



@Override
public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.single_pane);
    mController = TourManagerController.getInstance(this, R.id.sp_pane1);
}

@Override
public void onBackPressed() {
    if (!mController.handleMessage(ATEMemo.MESSAGE_BACKPRESSED))
        super.onBackPressed();
}

public static class ATEMemo {

public static final int MESSAGE_BACKPRESSED = 0;

// TourManagerFragment
public static final int EVENT_CREATE_CLICKED = 3;
public static final int EVENT_DELETE_CLICKED = 4;
public static final int EVENT_MAP_CLICKED = 5;


public static final int TEC_MESSAGE_DONE = 6;

}


}








