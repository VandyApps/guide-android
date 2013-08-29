
package edu.vanderbilt.vm.guide.container;

import java.util.List;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

/**
 * An ordered list of the places on a route and a graph of the route itself
 * 
 * @author nicholasking
 */
public class Route {

    private List<Place> mPlacesOnRoute;

    private SimpleWeightedGraph<MapVertex, DefaultWeightedEdge> mRouteGraph;

    private boolean mIsFinished = false;

    private int mCurrent = 0;

    public Route(List<Place> placesOnRoute,
            SimpleWeightedGraph<MapVertex, DefaultWeightedEdge> routeGraph) {
        mPlacesOnRoute = placesOnRoute;
        mRouteGraph = routeGraph;
        if (mPlacesOnRoute.size() == 0)
            mIsFinished = true;
    }

    public List<Place> getPlacesOnRoute() {
        return mPlacesOnRoute;
    }

    public SimpleWeightedGraph<MapVertex, DefaultWeightedEdge> getRouteGraph() {
        return mRouteGraph;
    }

    /**
     * Get the current place on the route.
     * 
     * @return the current place being visited, or null if all places have been
     *         visited
     */
    public Place getCurrentPlace() {
        if (mIsFinished) {
            return null;
        } else {
            return mPlacesOnRoute.get(mCurrent);
        }
    }

    /**
     * Proceed to the next place on the route
     * 
     * @return true if there is another place to visit, false if all places have
     *         been visited
     */
    public boolean proceed() {
        if (mIsFinished) {
            return false;
        } else {
            mCurrent++;
            if (mCurrent >= mPlacesOnRoute.size()) {
                mIsFinished = true;
                return false;
            } else {
                return true;
            }
        }
    }

    /**
     * Returns whether all places on this route have been visited
     * 
     * @return true if the route is finished, false if not
     */
    public boolean isFinished() {
        return mIsFinished;
    }

}
