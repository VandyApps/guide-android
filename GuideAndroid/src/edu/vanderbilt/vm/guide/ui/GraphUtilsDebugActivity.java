package edu.vanderbilt.vm.guide.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.SparseArray;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import edu.vanderbilt.vm.guide.R;
import edu.vanderbilt.vm.guide.db.GuideDBConstants;
import edu.vanderbilt.vm.guide.db.GuideDBOpenHelper;

public class GraphUtilsDebugActivity extends Activity implements GuideDBConstants {
    
    private GoogleMap mMap;
    private SparseArray<MapVertex> vertexMap = new SparseArray<MapVertex>();
    private static final Logger logger = LoggerFactory.getLogger("debug.graphutils");
    
    
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
        
        mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.debug_graph_map)).getMap();
        
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
                String[] neighborStrs = neighbors.split(",");
                mv.neighbors = new int[neighborStrs.length];
                for (int i=0; i<neighborStrs.length; i++) {
                    if (neighborStrs[i].length() > 0) {
                        mv.neighbors[i] = Integer.parseInt(neighborStrs[i]);
                    }
                }
                vertexMap.put(id, mv);
                
                logger.trace("Adding node to map: id={}, lat={}, lon={}, neighbors={}", id, lat, lon, neighbors);
                mMap.addMarker(new MarkerOptions().position(
                        new LatLng(lat, lon))
                        .title("" + id)
                        .icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
            }
        }
        
        // Add the lines on the map to represent the edges
        for (int i=0; i<vertexMap.size(); i++) {
            int mv1Id = vertexMap.keyAt(i);
            MapVertex mv1 = vertexMap.get(mv1Id);
            for (int mv2Id : mv1.neighbors) {
                MapVertex mv2 = vertexMap.get(mv2Id);
                PolylineOptions opts = new PolylineOptions();
                try {
                    opts.add(new LatLng(mv1.lat, mv1.lon))
                        .add(new LatLng(mv2.lat, mv2.lon));
                } catch (NullPointerException e) {
                    logger.error("mv1 id: {}, mv2 id: {}", mv1Id, mv2Id);
                }
                mMap.addPolyline(opts);
            }
        }        
        
        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(36.14624, -86.80309)));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(17));
    }
    
    private static class MapVertex {
        private int[] neighbors;
        private double lat;
        private double lon;
    }

}
