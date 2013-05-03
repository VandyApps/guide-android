package edu.vanderbilt.vm.guide.ui;

import android.app.Fragment;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import edu.vanderbilt.vm.guide.R;
import edu.vanderbilt.vm.guide.db.GuideDBConstants;
import edu.vanderbilt.vm.guide.db.GuideDBOpenHelper;
import edu.vanderbilt.vm.guide.ui.adapter.AutoPlaceCursorAdapter;

public class NavPlaceChooser extends Fragment implements OnClickListener{
    
    private Cursor mAutoSrc;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.navigator_chooser, container, false);
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        
        GuideDBOpenHelper helper = new GuideDBOpenHelper(getActivity());
        SQLiteDatabase db = helper.getReadableDatabase();
        mAutoSrc = db.query(GuideDBConstants.PlaceTable.PLACE_TABLE_NAME, new String[] {GuideDBConstants.PlaceTable.NAME_COL, GuideDBConstants.PlaceTable.ID_COL}, null, null, null, null, null);
        
        AutoCompleteTextView startActv = (AutoCompleteTextView) getView().findViewById(R.id.nav_actv1);
        AutoCompleteTextView destActv = (AutoCompleteTextView) getView().findViewById(R.id.nav_actv2);
        
        AutoPlaceCursorAdapter adapter1 = new AutoPlaceCursorAdapter(getActivity(), mAutoSrc);
        AutoPlaceCursorAdapter adapter2 = new AutoPlaceCursorAdapter(getActivity(), mAutoSrc);
        startActv.setAdapter(adapter1);
        destActv.setAdapter(adapter2);
        
    }
    
    
    @Override
    public void onClick(View v) {
        
    }
    
    
}
