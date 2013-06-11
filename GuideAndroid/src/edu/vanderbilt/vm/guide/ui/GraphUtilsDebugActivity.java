
package edu.vanderbilt.vm.guide.ui;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import edu.vanderbilt.vm.guide.R;
import edu.vanderbilt.vm.guide.container.MapVertex;
import edu.vanderbilt.vm.guide.db.GuideDBConstants;
import edu.vanderbilt.vm.guide.util.GlobalState;

public class GraphUtilsDebugActivity extends SherlockFragmentActivity implements GuideDBConstants {

    private GoogleMap mMap;

    private static final Logger logger = LoggerFactory.getLogger("debug.graphutils");

    // Radius of Earth IN METERS
    private static final int SHOW_FULL_GRAPH = Menu.NONE + 1;

    private static final int MST = Menu.NONE + 2;

    private static final int SHORTEST_PATH = Menu.NONE + 3;

    private static final int CLEAR_MAP = Menu.NONE + 4;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph_utils_debug);
        SupportMapFragment mapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.debug_graph_map);
        mMap = mapFrag.getMap();
        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(36.14624, -86.80309)));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(17));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, SHOW_FULL_GRAPH, Menu.NONE, "Show Full Graph");
        menu.add(Menu.NONE, MST, Menu.NONE, "Minimum Spanning Tree");
        menu.add(Menu.NONE, SHORTEST_PATH, Menu.NONE, "Shortest Path");
        menu.add(Menu.NONE, CLEAR_MAP, Menu.NONE, "Clear Map");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case SHOW_FULL_GRAPH:
                drawGraphOnMap(GlobalState.getWeightedGraph(this));
                return true;
            case MST:
                drawGraphOnMap(GlobalState.mst(GlobalState.getWeightedGraph(this)));
                return true;
            case SHORTEST_PATH:
                final Dialog dialog = new Dialog(this);
                dialog.setContentView(R.layout.graph_utils_debug_pt_dialog);
                final EditText node1Edit = (EditText)dialog
                        .findViewById(R.id.graph_utils_debug_pt_dialog_node1);
                final EditText node2Edit = (EditText)dialog
                        .findViewById(R.id.graph_utils_debug_pt_dialog_node2);
                final Button confirmButton = (Button)dialog
                        .findViewById(R.id.graph_utils_debug_pt_dialog_button);
                confirmButton.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                        try {
                            int pt1Id = Integer.parseInt(node1Edit.getText().toString());
                            int pt2Id = Integer.parseInt(node2Edit.getText().toString());
                            drawGraphOnMap(GlobalState.shortestPath(
                                    GlobalState.getWeightedGraph(GraphUtilsDebugActivity.this),
                                    GlobalState.getMapVertexWithId(pt1Id),
                                    GlobalState.getMapVertexWithId(pt2Id)));
                        } catch (Throwable t) {
                            // just keep app from crashing for now
                        }
                    }

                });
                dialog.show();
                return true;
            case CLEAR_MAP:
                mMap.clear();
                return true;
            default:
                return false;
        }
    }

    private void drawGraphOnMap(SimpleWeightedGraph<MapVertex, DefaultWeightedEdge> graph) {
        for (MapVertex mv : graph.vertexSet()) {
            logger.trace("Adding node to map: id={}, lat={}, lon={}, neighbors={}", mv.id, mv.lat,
                    mv.lon, mv.neighbors);
            mMap.addMarker(new MarkerOptions().position(new LatLng(mv.lat, mv.lon))
                    .title("" + mv.id)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        }
        for (DefaultWeightedEdge e : graph.edgeSet()) {
            MapVertex mv1 = graph.getEdgeSource(e);
            MapVertex mv2 = graph.getEdgeTarget(e);
            PolylineOptions opts = new PolylineOptions();
            opts.add(new LatLng(mv1.lat, mv1.lon)).add(new LatLng(mv2.lat, mv2.lon));
            mMap.addPolyline(opts);
        }
    }

}
