package edu.vanderbilt.vm.guide.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.actionbarsherlock.app.SherlockFragment;

import edu.vanderbilt.vm.guide.R;
import edu.vanderbilt.vm.guide.ui.ActTourEditor.ATEMemo;
import edu.vanderbilt.vm.guide.ui.controllers.Controller;

/**
 * A page for creating and creating a new tour.
 * 
 * This page should present a graphical interface for creating and editing the 
 * Tour. When User clicks DONE, it contructs a Tour object as specified by
 * user and sends a DONE message to its controller along with the Tour object.
 * 
 * @author athran
 * 
 */
public class TourEditorFragment extends SherlockFragment {

/**
 * Get an instance of this Fragment. If there's no obstacle, I may use Flyweight
 * pattern and make this a Singleton to be reused for each Tour being edited.
 * 
 * @return an instance of TourEditorFragment
 */
public static TourEditorFragment getInstance(Controller controller) {
    TourEditorFragment frag = new TourEditorFragment();
    frag.mController = controller;
    return frag;
}


private Controller mController;
private View mRoot;

// View Objects
private Button btnDone;
private EditText etTour;

@Override
public View onCreateView(
        LayoutInflater inflater, 
        ViewGroup container,
        Bundle savedInstanceState) {
    mRoot = inflater.inflate(R.layout.fragment_tour_editor, container, false);
    return mRoot;
}

@Override
public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    
    etTour = (EditText) mRoot.findViewById(R.id.t_e_et1);
    
    btnDone = (Button) mRoot.findViewById(R.id.t_e_btndone);
    btnDone.setOnClickListener(new OnClickListener() {
        @Override public void onClick(View v) {
            mController.handleMessage(ATEMemo.TEC_MESSAGE_DONE,
                    
                    // TODO Should Contruct a proper Tour Object. Using a
                    // String as a temporary placeholder
                    etTour.getText().toString());
        } 
    });
    
}

}




















