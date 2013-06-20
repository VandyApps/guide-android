package edu.vanderbilt.vm.guide.ui;

import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import edu.vanderbilt.vm.guide.util.Geomancer;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import edu.vanderbilt.vm.guide.R;
import edu.vanderbilt.vm.guide.container.MapVertex;
import edu.vanderbilt.vm.guide.db.GuideDBConstants;
import edu.vanderbilt.vm.guide.ui.adapter.AutoPlaceCursorAdapter;
import edu.vanderbilt.vm.guide.util.GlobalState;

public class PlaceNavigatorFragment extends NavigatorFragment {
    
    private Cursor mCursor;
    private SQLiteDatabase mDb;
    private AutoCompleteTextView mStartActv, mDestActv;
    private CheckBox mCheckBox;
    private boolean mIsShowingPath;

    private static final Logger LOGGER = LoggerFactory.getLogger("ui.NavPlaceChooser");
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_place_navigator, container, false);
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        
        mDb = GlobalState.getReadableDatabase(getActivity());
        mCursor = mDb.query(
                GuideDBConstants.PlaceTable.PLACE_TABLE_NAME,
                new String[] {
                        GuideDBConstants.PlaceTable.NAME_COL,
                        GuideDBConstants.PlaceTable.ID_COL,
                        GuideDBConstants.PlaceTable.LATITUDE_COL,
                        GuideDBConstants.PlaceTable.LONGITUDE_COL},
                null, null, null, null, null);
        
        mStartActv = (AutoCompleteTextView) getView().findViewById(R.id.nav_actv1);
        mDestActv = (AutoCompleteTextView) getView().findViewById(R.id.nav_actv2);
        
        AutoPlaceCursorAdapter adapter1 = new AutoPlaceCursorAdapter(getActivity(), mCursor);
        AutoPlaceCursorAdapter adapter2 = new AutoPlaceCursorAdapter(getActivity(), mCursor);
        mStartActv.setAdapter(adapter1);
        mDestActv.setAdapter(adapter2);

        mCheckBox = (CheckBox) getView().findViewById(R.id.nav_cb1);
        mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {    // Use current location
                    mStartActv.setEnabled(false);
                    mStartActv.setText(null);
                    mStartActv.setHint("Current location"); //TODO use string resource

                } else {            // Allow user input
                    mStartActv.setEnabled(true);
                    mStartActv.setHint("Enter name of starting location"); //TODO use string resource
                }

            }
        });

        mIsShowingPath = false;

        Button navBtn = (Button)getView().findViewById(R.id.nav_btn);
        navBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Toggle view state
                View layout = getView();
                if (mIsShowingPath) {
                    layout.findViewById(R.id.nav_ll_start).setVisibility(View.VISIBLE);
                    ((Button) v).setText("Find Path");              //TODO use string resource
                    mIsShowingPath = false;
                    return;

                } else {



                    layout.findViewById(R.id.nav_ll_start).setVisibility(View.GONE);
                    ((Button) v).setText("Do Another Search");              //TODO use string resource
                    mIsShowingPath = true;

                }


                String startName = mStartActv.getText().toString();
                String destName = mDestActv.getText().toString();

                // Return on the trivial case
                if ((!mCheckBox.isChecked() && (startName == null || startName.length() == 0))  ||
                        destName == null || destName.length() == 0)
                    return;

                int startId = -1, destId = -1;

                // Get current location if requested
                if (mCheckBox.isChecked()) {
                    mCursor.moveToPosition(
                            Geomancer.findClosestPlace(
                                    Geomancer.getDeviceLocation(),
                                    mCursor));
                    startId = mCursor.getInt(
                            mCursor.getColumnIndex(
                                    GuideDBConstants.PlaceTable.ID_COL));
                }


                // Do a linear search through mCursor for the two place IDs
                synchronized (mCursor) {
                    int nameIx = mCursor.getColumnIndex(GuideDBConstants.PlaceTable.NAME_COL);
                    int idIx = mCursor.getColumnIndex(GuideDBConstants.PlaceTable.ID_COL);
                    if (!mCursor.moveToFirst()) return;

                    do {
                        String name = mCursor.getString(nameIx);
                        if (startId == -1 && name.equals(startName)) {
                            startId = mCursor.getInt(idIx);
                        } else if (destId == -1 && name.equals(destName)) {
                            destId = mCursor.getInt(idIx);
                        }
                    } while(mCursor.moveToNext() && (startId == -1 || destId == -1));
                }

                if (startId == -1) {
                    Toast.makeText(getActivity(), "Could not find " + startName, Toast.LENGTH_LONG).show();
                    LOGGER.warn("Could not find an id for {}", startName);
                    return;
                } else if (destId == -1) {
                    Toast.makeText(getActivity(), "Could not find " + destName, Toast.LENGTH_LONG).show();
                    LOGGER.warn("Could not find an id for {}", destName);
                    return;
                }

                // Build the graph if it hasn't been built already
                SimpleWeightedGraph<MapVertex, DefaultWeightedEdge> graph = 
                        GlobalState.getWeightedGraph(getActivity());

                MapVertex startVertex = GlobalState.getMapVertexWithId(startId);
                MapVertex destVertex = GlobalState.getMapVertexWithId(destId);

                mMapper.mapGraph(GlobalState.shortestPath(graph, startVertex, destVertex));

                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);

            }
            
        });
        
        
    }
    
    //private static class GraphPathTask extends AsyncTask
    
    
}
