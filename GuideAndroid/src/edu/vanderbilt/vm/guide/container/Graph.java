
package edu.vanderbilt.vm.guide.container;

import java.util.ArrayList;

@SuppressWarnings("serial")
public class Graph extends ArrayList<Node> {

    public Node getNodeWithLowestScore() {
        double score = Double.MAX_VALUE;
        Node lowest = null;
        for (Node node : this) {
            if (node.getScore() < score) {
                score = node.getScore();
                lowest = node;
            }
        }
        return lowest;
    }

    public Graph getNodeNeighbour(Node n) {

        Graph graph = new Graph();

        if (!this.contains(n)) {
            return graph;
        }

        for (int id : n.getNeighbours()) {
            Node nn = this.findNodeById(id);
            if (nn != null) {
                graph.add(nn);
            }
        }

        return null;
    }

    public Node findNodeById(int id) {

        for (Node n : this) {
            if (n.getId() == id) {
                return n;
            }
        }

        return null;
    }
    
    public static Graph createGraph(Agenda a) {
        Graph g = new Graph();
        
        for (Place plc : a) {
            g.add(new Node(plc.getLatitude(), plc.getLongitude()));
            
        }
        
        return g;
    }
}
