
package edu.vanderbilt.vm.guide.util;

import java.util.List;
import java.util.Set;

import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.alg.KruskalMinimumSpanningTree;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.SparseArray;

import com.google.android.gms.maps.model.LatLng;

import edu.vanderbilt.vm.guide.container.Agenda;
import edu.vanderbilt.vm.guide.container.MapVertex;
import edu.vanderbilt.vm.guide.db.GuideDBConstants.NodeTable;
import edu.vanderbilt.vm.guide.db.GuideDBOpenHelper;

/**
 * This class holds singletons of certain objects we need to share throughout
 * the application, such as the user's agenda. This is simpler and easier than
 * using a SQLite database to hold the agenda and allows us to use several
 * methods to make data transactions with the agenda easier.
 * 
 * @author nicholasking
 */
public class GlobalState {

    // Agenda singleton //
    private static Agenda sUserAgendaSingleton = new Agenda();

    private static Logger logger = LoggerFactory.getLogger("util.GlobalState");

    private GlobalState() {
        throw new AssertionError("Do not instantiate this class.");
    }

    public static Agenda getUserAgenda() {
        return sUserAgendaSingleton;
    }

    // End Agenda singleton

    // Database singleton //
    private static SQLiteDatabase sReadableDb, sWritableDb;

    private static GuideDBOpenHelper sHelper;

    public static SQLiteDatabase getReadableDatabase(Context c) {
        if (sHelper == null) {
            sHelper = new GuideDBOpenHelper(c.getApplicationContext());
        }
        if (sReadableDb == null) {
            sReadableDb = sHelper.getReadableDatabase();
        }
        return sReadableDb;
    }

    public static SQLiteDatabase getWritableDatabase(Context c) {
        if (sHelper == null) {
            sHelper = new GuideDBOpenHelper(c.getApplicationContext());
        }
        if (sWritableDb == null) {
            sWritableDb = sHelper.getWritableDatabase();
        }
        return sWritableDb;
    }

    // End database singleton //

    // Graph singleton //
    private static SimpleWeightedGraph<MapVertex, DefaultWeightedEdge> sGraph;

    private static SparseArray<MapVertex> sVertexMap = new SparseArray<MapVertex>();

    /**
     * Builds and returns a graph built from the node table. This method could
     * take a long time to execute the first time it is called since it will
     * have to build the graph, so consider calling this method on a background
     * thread the first time.
     * 
     * @param c The Context to use for accessing the database
     * @return a SimpleWeightedGraph built from the Node table
     */
    public static SimpleWeightedGraph<MapVertex, DefaultWeightedEdge> getWeightedGraph(Context c) {
        if (sGraph == null) {
            sGraph = new SimpleWeightedGraph<MapVertex, DefaultWeightedEdge>(
                    DefaultWeightedEdge.class);

            SQLiteDatabase db = getReadableDatabase(c);

            Cursor nodeCursor = db.query(NodeTable.NODE_TABLE_NAME, new String[] {
                    NodeTable.ID_COL, NodeTable.LAT_COL, NodeTable.LON_COL, NodeTable.NEIGHBOR_COL
            }, null, null, null, null, null);

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
                    for (int i = 0; i < neighborStrs.length; i++) {
                        if (neighborStrs[i].length() > 0) {
                            mv.neighbors[i] = Integer.parseInt(neighborStrs[i]);
                        }
                    }
                    sVertexMap.put(id, mv);
                }
            }

            // Build the graph
            for (int i = 0; i < sVertexMap.size(); i++) {
                int mvId = sVertexMap.keyAt(i);
                MapVertex mv = sVertexMap.get(mvId);
                /*
                 * Do not add the vertices for places to the graph. These
                 * vertices should only be present in the graph when finding
                 * paths and routes. Having these extra vertices in the graph
                 * results in the algorithms finding paths through buildings,
                 * which is not what we want.
                 */
                if (!isPlace(mv)) {
                    addVertexToGraph(sGraph, mv);
                }
            }
        }
        return sGraph;
    }

    private static void addVertexToGraph(SimpleWeightedGraph<MapVertex, DefaultWeightedEdge> graph,
            MapVertex mv) {
        graph.addVertex(mv);
        for (int neighborId : mv.neighbors) {
            MapVertex mv2 = sVertexMap.get(neighborId);
            // XXX: Bad hack job!
            if (mv2 == null)
                continue;
            /*
             * Assuming the MapVertex equals() and hashCode() methods work
             * correctly, addVertex() will ensure there are no duplicate
             * vertices in the graph
             */
            graph.addVertex(mv2);
            try {
                /*
                 * XXX: Needs debugging, for some reason there are vertices
                 * adjacent to a vertex with id "0"
                 */
                LatLng latlng1 = new LatLng(mv.lat, mv.lon);
                LatLng latlng2 = new LatLng(mv2.lat, mv2.lon);
                DefaultWeightedEdge e = graph.addEdge(mv, mv2);
                // e will be null if the edge already existed
                if (e != null) {
                    graph.setEdgeWeight(e, distanceBetween(latlng1, latlng2));
                }
            } catch (NullPointerException e) {
                logger.error("mv1 id: {}, mv2 id: {}", mv.id, neighborId);
            }
        }
    }

    public static boolean isPlace(MapVertex mv) {
        return mv.id <= GuideConstants.MAX_PLACE_ID;
    }

    /**
     * Returns the MapVertex with the given id in the graph. This will match the
     * node's id in the nodes.json file. If the graph has not been built yet,
     * this method will return null. Be sure to call getWeightedGraph() so the
     * graph will be built before calling this method.
     * 
     * @param id the id to match
     * @return the MapVertex with the given id
     */
    public static MapVertex getMapVertexWithId(int id) {
        if (sGraph == null) {
            return null;
        } else {
            return sVertexMap.get(id);
        }
    }

    public static SimpleWeightedGraph<MapVertex, DefaultWeightedEdge> mst(
            SimpleWeightedGraph<MapVertex, DefaultWeightedEdge> graph) {
        KruskalMinimumSpanningTree<MapVertex, DefaultWeightedEdge> mstFinder = new KruskalMinimumSpanningTree<MapVertex, DefaultWeightedEdge>(
                graph);

        Set<DefaultWeightedEdge> mstEdges = mstFinder.getEdgeSet();
        SimpleWeightedGraph<MapVertex, DefaultWeightedEdge> mst = new SimpleWeightedGraph<MapVertex, DefaultWeightedEdge>(
                DefaultWeightedEdge.class);

        for (MapVertex mv : graph.vertexSet()) {
            mst.addVertex(mv);
        }

        for (DefaultWeightedEdge e : mstEdges) {
            mst.addEdge(graph.getEdgeSource(e), graph.getEdgeTarget(e));
            mst.setEdgeWeight(e, graph.getEdgeWeight(e));
        }

        return mst;
    }

    public static SimpleWeightedGraph<MapVertex, DefaultWeightedEdge> shortestPath(
            SimpleWeightedGraph<MapVertex, DefaultWeightedEdge> graph, MapVertex start,
            MapVertex end) {
        // Temporarily add the start and end vertices to the graph
        addVertexToGraph(graph, start);
        addVertexToGraph(graph, end);

        // Find the path
        DijkstraShortestPath<MapVertex, DefaultWeightedEdge> pathSolver = new DijkstraShortestPath<MapVertex, DefaultWeightedEdge>(
                graph, start, end);
        SimpleWeightedGraph<MapVertex, DefaultWeightedEdge> path = new SimpleWeightedGraph<MapVertex, DefaultWeightedEdge>(
                DefaultWeightedEdge.class);
        List<DefaultWeightedEdge> pathEdges = pathSolver.getPath().getEdgeList();
        for (DefaultWeightedEdge e : pathEdges) {
            MapVertex mv1 = graph.getEdgeSource(e);
            MapVertex mv2 = graph.getEdgeTarget(e);
            path.addVertex(mv1);
            path.addVertex(mv2);
            path.addEdge(mv1, mv2);
        }

        // Remove the start and end vertices
        graph.removeVertex(start);
        graph.removeVertex(end);
        return path;
    }

    public static double distanceBetween(LatLng point1, LatLng point2) {
        return distanceInRadians(point1, point2) * GuideConstants.EARTH_RADIUS;
    }

    /**
     * Modified from LatLngTool.java of simplelatlng project at
     * https://code.google
     * .com/p/simplelatlng/source/browse/src/main/java/com/javadocmd
     * /simplelatlng/LatLngTool.java
     */
    public static double distanceInRadians(LatLng point1, LatLng point2) {
        double lat1R = Math.toRadians(point1.latitude);
        double lat2R = Math.toRadians(point2.latitude);
        double dLatR = Math.abs(lat2R - lat1R);
        double dLngR = Math.abs(Math.toRadians(point2.longitude - point1.longitude));
        double a = Math.sin(dLatR / 2) * Math.sin(dLatR / 2) + Math.cos(lat1R) * Math.cos(lat2R)
                * Math.sin(dLngR / 2) * Math.sin(dLngR / 2);
        return 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }
}
