package edu.vanderbilt.vm.guide.ui;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.alg.KruskalMinimumSpanningTree;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.Dialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import edu.vanderbilt.vm.guide.R;
import edu.vanderbilt.vm.guide.db.GuideDBConstants;
import edu.vanderbilt.vm.guide.db.GuideDBOpenHelper;

public class GraphUtilsDebugActivity extends SherlockFragmentActivity implements GuideDBConstants {
    
    private GoogleMap mMap;
    private SparseArray<MapVertex> vertexMap = new SparseArray<MapVertex>();
    private SimpleWeightedGraph<MapVertex, DefaultWeightedEdge> mGraph = new SimpleWeightedGraph<MapVertex, DefaultWeightedEdge>(DefaultWeightedEdge.class);
    private static final Logger logger = LoggerFactory.getLogger("debug.graphutils");
    
    // Radius of Earth IN METERS
    private static final double EARTH_RADIUS = 6378100;
    private static final int SHOW_FULL_GRAPH = Menu.NONE + 1;
    private static final int MST = Menu.NONE + 2;
    private static final int SHORTEST_PATH = Menu.NONE + 3;
    private static final int CLEAR_MAP = Menu.NONE + 4;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph_utils_debug);
        
        GuideDBOpenHelper helper = new GuideDBOpenHelper(this);
        SQLiteDatabase db = helper.getReadableDatabase();
        /*
        Cursor placeCursor = db.query(PlaceTable.PLACE_TABLE_NAME, 
                new String[] {PlaceTable.ID_COL, PlaceTable.LATITUDE_COL, PlaceTable.LONGITUDE_COL, PlaceTable.NAME_COL},
                null, null, null, null, null);*/
        Cursor nodeCursor = db.query(NodeTable.NODE_TABLE_NAME, 
                new String[] {NodeTable.ID_COL, NodeTable.LAT_COL, NodeTable.LON_COL, NodeTable.NEIGHBOR_COL},
                null, null, null, null, null);
        
        mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.debug_graph_map)).getMap();
        
        /*
        if (placeCursor != null) {
            int id_ix = placeCursor.getColumnIndex(PlaceTable.ID_COL);
            int name_ix = placeCursor.getColumnIndex(PlaceTable.NAME_COL);
            int lat_ix = placeCursor.getColumnIndex(PlaceTable.LATITUDE_COL);
            int lon_ix = placeCursor.getColumnIndex(PlaceTable.LONGITUDE_COL);
            while (placeCursor.moveToNext()) {
                double lat = placeCursor.getDouble(lat_ix);
                double lon = placeCursor.getDouble(lon_ix);
                int id = placeCursor.getInt(id_ix);
                String name = placeCursor.getString(name_ix);
                mMap.addMarker(new MarkerOptions().position(
                        new LatLng(lat, lon))
                        .title(name)
                        .snippet("ID: " + id));
            }
        }*/
        
        if (nodeCursor != null) {
            int id_ix = nodeCursor.getColumnIndex(NodeTable.ID_COL);
            int lat_ix = nodeCursor.getColumnIndex(NodeTable.LAT_COL);
            int lon_ix = nodeCursor.getColumnIndex(NodeTable.LON_COL);
            int neighbors_ix = nodeCursor.getColumnIndex(NodeTable.NEIGHBOR_COL);
            
            while (nodeCursor.moveToNext()) {
                double lat = nodeCursor.getDouble(lat_ix);
                double lon = nodeCursor.getDouble(lon_ix);
                String neighbors = nodeCursor.getString(neighbors_ix);
                int id = nodeCursor.getInt(id_ix);
                
                // Make an object to hold the values and add it to 
                // a map for making a graph later on
                MapVertex mv = new MapVertex();
                mv.lat = lat;
                mv.lon = lon;
                mv.id = id;
                String[] neighborStrs = neighbors.split(",");
                mv.neighbors = new int[neighborStrs.length];
                for (int i=0; i<neighborStrs.length; i++) {
                    if (neighborStrs[i].length() > 0) {
                        mv.neighbors[i] = Integer.parseInt(neighborStrs[i]);
                    }
                }
                vertexMap.put(id, mv);
            }
        }
        
        
        // Build the graph and add the lines on the map to represent the edges
        for (int i=0; i<vertexMap.size(); i++) {
            int mv1Id = vertexMap.keyAt(i);
            MapVertex mv1 = vertexMap.get(mv1Id);
            mGraph.addVertex(mv1);
            for (int mv2Id : mv1.neighbors) {
                MapVertex mv2 = vertexMap.get(mv2Id);
                // XXX: Bad hack job!
                if (mv2 == null) continue;
                // Assuming the MapVertex equals() and hashCode() methods work
                // correctly, addVertex() will ensure there are no duplicate
                // vertices in the graph
                mGraph.addVertex(mv2);
                try {
                    // XXX: Needs debugging, for some reason there are vertices
                    // adjacent to a vertex with id "0"
                    LatLng latlng1 = new LatLng(mv1.lat, mv1.lon);
                    LatLng latlng2 = new LatLng(mv2.lat, mv2.lon);
                    DefaultWeightedEdge e = mGraph.addEdge(mv1, mv2);
                    // e will be null if the edge already existed
                    if (e != null) {
                        mGraph.setEdgeWeight(e, distanceBetween(latlng1, latlng2));
                    }
                } catch (NullPointerException e) {
                    logger.error("mv1 id: {}, mv2 id: {}", mv1Id, mv2Id);
                }
            }
        }
        
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
                drawGraphOnMap(mGraph);
                return true;
            case MST:
                drawGraphOnMap(mst(mGraph));
                return true;
            case SHORTEST_PATH:
                final Dialog dialog = new Dialog(this);
                dialog.setContentView(R.layout.graph_utils_debug_pt_dialog);
                final EditText node1Edit = (EditText)dialog.findViewById(R.id.graph_utils_debug_pt_dialog_node1);
                final EditText node2Edit = (EditText)dialog.findViewById(R.id.graph_utils_debug_pt_dialog_node2);
                final Button confirmButton = (Button) dialog.findViewById(R.id.graph_utils_debug_pt_dialog_button);
                confirmButton.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                        try {
                            int pt1Id = Integer.parseInt(node1Edit.getText().toString());
                            int pt2Id = Integer.parseInt(node2Edit.getText().toString());
                            drawGraphOnMap(shortestPath(mGraph, vertexMap.get(pt1Id), vertexMap.get(pt2Id)));
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
            logger.trace("Adding node to map: id={}, lat={}, lon={}, neighbors={}", mv.id, mv.lat, mv.lon, mv.neighbors);
            mMap.addMarker(new MarkerOptions().position(
                    new LatLng(mv.lat, mv.lon))
                    .title("" + mv.id)
                    .icon(BitmapDescriptorFactory
                    .defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        }
        for (DefaultWeightedEdge e : graph.edgeSet()) {
            MapVertex mv1 = graph.getEdgeSource(e);
            MapVertex mv2 = graph.getEdgeTarget(e);
            PolylineOptions opts = new PolylineOptions();
            opts.add(new LatLng(mv1.lat, mv1.lon))
                .add(new LatLng(mv2.lat, mv2.lon));
            mMap.addPolyline(opts);
        }
    }
    
    private static SimpleWeightedGraph<MapVertex, DefaultWeightedEdge> mst(SimpleWeightedGraph<MapVertex, DefaultWeightedEdge> graph) {
        KruskalMinimumSpanningTree<MapVertex, DefaultWeightedEdge> mstFinder = new KruskalMinimumSpanningTree<MapVertex, DefaultWeightedEdge>(graph);
        Set<DefaultWeightedEdge> mstEdges = mstFinder.getEdgeSet();
        SimpleWeightedGraph<MapVertex, DefaultWeightedEdge> mst = new SimpleWeightedGraph<MapVertex, DefaultWeightedEdge>(DefaultWeightedEdge.class);
        for (MapVertex mv : graph.vertexSet()) {
            mst.addVertex(mv);
        }
        for (DefaultWeightedEdge e : mstEdges) {
            mst.addEdge(graph.getEdgeSource(e), graph.getEdgeTarget(e));
            mst.setEdgeWeight(e, graph.getEdgeWeight(e));
        }
        return mst;
    }
    
    private static SimpleWeightedGraph<MapVertex, DefaultWeightedEdge> shortestPath(SimpleWeightedGraph<MapVertex, DefaultWeightedEdge> graph, MapVertex start, MapVertex end) {
        DijkstraShortestPath<MapVertex, DefaultWeightedEdge> pathSolver = new DijkstraShortestPath<MapVertex, DefaultWeightedEdge>(graph, start, end);
        SimpleWeightedGraph<MapVertex, DefaultWeightedEdge> path = new SimpleWeightedGraph<MapVertex, DefaultWeightedEdge>(DefaultWeightedEdge.class);
        List<DefaultWeightedEdge> pathEdges = pathSolver.getPath().getEdgeList();
        for (DefaultWeightedEdge e : pathEdges) {
            MapVertex mv1 = graph.getEdgeSource(e);
            MapVertex mv2 = graph.getEdgeTarget(e);
            path.addVertex(mv1);
            path.addVertex(mv2);
            path.addEdge(mv1, mv2);
        }
        return path;
    }
    
    public static double distanceBetween(LatLng point1, LatLng point2) {
        return distanceInRadians(point1, point2) * EARTH_RADIUS;
    }
    
    /**
     * Modified from LatLngTool.java of simplelatlng project at
     * https://code.google.com/p/simplelatlng/source/browse/src/main/java/com/javadocmd/simplelatlng/LatLngTool.java
     */
    public static double distanceInRadians(LatLng point1, LatLng point2) {
        double lat1R = Math.toRadians(point1.latitude);
        double lat2R = Math.toRadians(point2.latitude);
        double dLatR = Math.abs(lat2R - lat1R);
        double dLngR = Math.abs(Math.toRadians(point2.longitude
                        - point1.longitude));
        double a = Math.sin(dLatR / 2) * Math.sin(dLatR / 2) + Math.cos(lat1R)
                        * Math.cos(lat2R) * Math.sin(dLngR / 2) * Math.sin(dLngR / 2);
        return 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }
    
    
    private static class MapVertex {
        private int[] neighbors;
        private double lat;
        private double lon;
        private int id;
        
        @Override
        public boolean equals(Object o) {
            if (!(o instanceof MapVertex)) return false;
            MapVertex mv = (MapVertex) o;
            if (id != mv.id) return false;
            if (lat != mv.lat) return false;
            if (lon != mv.lon) return false;
            return Arrays.equals(neighbors, mv.neighbors);
        }
        
        @Override
        public int hashCode() {
            int result = 17;
            result = 31 * result + id;
            long n = Double.doubleToLongBits(lat);
            int c = (int)(n ^ (n >>> 32));
            result = 31 * result + c;
            n = Double.doubleToLongBits(lon);
            c = (int)(n ^ (n >>> 32));
            result = 31 * result + c;
            result = 31 * result + Arrays.hashCode(neighbors);
            return result;
        }
        
    }

}
