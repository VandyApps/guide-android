package edu.vanderbilt.vm.guide.ui;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import edu.vanderbilt.vm.guide.container.MapVertex;

/**
 * Interface for a type that can draw a SimpleWeightedGraph on a map
 * @author nicholasking
 *
 */
public interface IGraphMapper {

    /** Maps the graph, setting a marker for every node with an ID less
     * than the maximum place ID
     * @param graph The graph to map
     */
    public void mapGraph(SimpleWeightedGraph<MapVertex, DefaultWeightedEdge> graph);
    
}
