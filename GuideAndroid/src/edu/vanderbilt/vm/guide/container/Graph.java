
package edu.vanderbilt.vm.guide.container;

//Credit to Sjaak Priester ( mailto:sjaak@sjaakpriester.nl )

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("serial")
public class Graph extends ArrayList<Node> {

    private static final Logger logger = LoggerFactory.getLogger("container.Graph");

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
            g.add(new Node(plc));
        }

        return g;
    }

    public Graph findPath(Node start, Node end) {
        // Assert that "start" and "end" are elements of "g"
        // Assert that Graph is a typedef of Arraylist<Node>

        // Initialization routine
        for (Node node : this) {
            if (node.getId() == start.getId()) {
                node.setScore(0);
            } else {
                node.setScore(Double.MAX_VALUE);
            }

            node.setPrevious(-1);
        }

        // Create a set of nodes not yet examined. This initially contain all
        // the nodes in "g"
        Graph unvisited = (Graph)this.clone();

        // This is the bulk of the algorithm
        while (!unvisited.isEmpty()) {
            Node u = unvisited.getNodeWithLowestScore();
            unvisited.remove(u);
            if (u.getId() == end.getId()) {
                break;
            }

            if (u.getScore() == Double.MAX_VALUE) {
                break;
            }

            for (Node neigh : this.getNodeNeighbour(u)) {
                // getNeighbours() returns a Graph object
                if (!unvisited.contains(neigh)) {
                    continue;
                }
                double dist = u.getScore() + u.distanceTo(neigh);
                if (dist < neigh.getScore()) {
                    neigh.setScore(dist);
                    neigh.setPrevious(u.getId());
                }
            }
        }

        // backtracing the path
        // Since "start" and "end" are elements of "g", They should have
        // received
        // the result of the algorithm run
        Graph path = new Graph();

        int prev = end.getPrevious();
        while (prev != -1) {
            path.add(this.findNodeById(prev));
            prev = (this.findNodeById(prev)).getPrevious();
        }

        return path;
    }

    /**
     * Given a graph which contain a set of at least three points, build
     * adjacency list for each Nodes according to the rules laid out by the
     * Delaunay Triangulation. Adapted from an implementation by Sjaak Priester
     * ( mailto:sjaak@sjaakpriester.nl )
     */
    public void buildNetwork() {

        if (this.size() < 3) {
            // Can't build triangle with less than 3 points
            logger.debug("Can't build triangle with less than 3 points");
            return;
        }

        // Initialization
        ArrayList<Triangle> workingSet = new ArrayList<Triangle>();
        Collections.sort(this, new HorizontalComparator());
        
        // Find bounding rectangle
        double xMin = this.get(0).getLng();
        double yMin = this.get(0).getLat();
        double xMax = xMin;
        double yMax = yMin;

        for (Node n : this) {
            if (n.getLng() > xMax) {
                xMax = n.getLng();
            }
            if (n.getLat() > yMax) {
                yMax = n.getLat();
            }
            if (n.getLat() < yMin) {
                yMin = n.getLat();
            }
        }

        // Give a little padding to the bounding rectangle
        yMax += 2 * Node.EPSILON;
        xMax += 2 * Node.EPSILON;
        yMin -= 2 * Node.EPSILON;
        xMin -= 2 * Node.EPSILON;

        double h = yMax - yMin;
        double w = xMax - xMin;

        // logger.debug("Bounding Rect: " + xMin + " " + xMax + " " + yMin + " "
        // + yMax + " " + h + " " + w );

        // Create supertriangle that encompasses all Nodes
        Triangle superTri = new Triangle();

        superTri.add(new Node(-1, yMin, xMin - w, null));
        superTri.add(new Node(-2, yMin, xMax + w, null));
        superTri.add(new Node(-3, yMax + h, (xMin + xMax) / 2, null));

        workingSet.add(superTri);

        // [Delaunaaaaaaaaaaaay]
        // Add each nodes in this Graph one by one.
        // Each node is guaranteed to be inside a Triangle, due to the
        // super Triangle.
        // For each addition, find a list of triangles that encompasses the
        // node. Remove the Triangle but keep its edges in an edgebuffer.
        // Remove duplicate Edges, then build new triangles from each edge to
        // the Node being added
        ArrayList<Edge> edgeSet = new ArrayList<Edge>();
        for (Node n : this) {

            // Search for Triangles that contain this Vertex
            // store its edges, and then remove the Triangle
            edgeSet = new ArrayList<Edge>();
            //logger.debug("Size of workingSet before remove: " + workingSet.size());
            for (int k = 0; k < workingSet.size(); k++) {
                Triangle tri = workingSet.get(k);
		/*
                I'm getting weird bugs here, so this part is commented out for now.
		The functionality is not affected, but there may be some performance hit.
		*/ /*    
		if (tri.isLeftOf(n)) {
                    // should remove completed triangle here
                    workingSet.remove(k);
                    k--;

                } else 
                */
                if (tri.isEncompassing(n)) {
                    edgeSet.add(new Edge(tri.get(0), tri.get(1)));
                    edgeSet.add(new Edge(tri.get(1), tri.get(2)));
                    edgeSet.add(new Edge(tri.get(0), tri.get(2)));

                    workingSet.remove(k);
                    k--;

                } else {
                    // logger.debug("Not encompassing");
                }
            }
            // logger.debug("Size of workingSet after remove: " + workingSet.size());
            // logger.debug("Size of edgeSet before remove: " + edgeSet.size());
            removeDuplicateEdge(edgeSet);
            // logger.debug("Size of edgeSet after remove: " + edgeSet.size());
            for (Edge e : edgeSet) {

                Triangle newT = new Triangle();
                newT.add(e.get(0));
                newT.add(e.get(1));
                newT.add(n);
                workingSet.add(newT);
            }
        }

        // Remove super triangle
        workingSet.remove(superTri);
        // logger.debug("Size of workingSet: " + workingSet.size());

        // convert to adjacency list
        for (Triangle tri : workingSet) {
            // logger.debug("Id: "+tri.get(0).getId());

            if ((tri.get(0).getId() <= 0) || (tri.get(1).getId() <= 0) || (tri.get(2).getId() <= 0)) {
                continue;
            }

            Node nn = this.findNodeById(tri.get(0).getId());
            nn.addNeighbour(tri.get(1).getId());
            nn.addNeighbour(tri.get(2).getId());

            nn = this.findNodeById(tri.get(1).getId());
            nn.addNeighbour(tri.get(0).getId());
            nn.addNeighbour(tri.get(2).getId());

            nn = this.findNodeById(tri.get(2).getId());
            nn.addNeighbour(tri.get(0).getId());
            nn.addNeighbour(tri.get(1).getId());
        }

    }

    private class HorizontalComparator implements Comparator<Node> {

        @Override
        public int compare(Node n1, Node n2) {
            return (n1.getLng() > n2.getLng()) ? 1 : -1;
        }

    }

    private void removeDuplicateEdge(ArrayList<Edge> edgeSet) {

        for (int i = 0; i < edgeSet.size() - 1; i++) {

            for (int j = i + 1; j < edgeSet.size(); j++) {

                if ((edgeSet.get(i).get(0).getId() == edgeSet.get(j).get(0).getId() && edgeSet
                        .get(i).get(1).getId() == edgeSet.get(j).get(1).getId())
                        || (edgeSet.get(i).get(0).getId() == edgeSet.get(j).get(1).getId() && edgeSet
                                .get(i).get(1).getId() == edgeSet.get(j).get(0).getId())) {
                    // Remove duplicate
                    edgeSet.remove(j);
                    edgeSet.remove(i);
                    i--;
                    break;
                }
            }
        }
    }

    private static class Edge extends ArrayList<Node> {

        public Edge(Node n1, Node n2) {
            this.add(n1);
            this.add(n2);
        }
    }

    static class Triangle extends ArrayList<Node> {

        private Node mCentre;

        private double mRad;

        // private static final Logger logger =
        // LoggerFactory.getLogger("container.Triangle");

        @Override
        public boolean add(Node n) {
            if (this.size() > 3) {
                throw new IllegalStateException("Can't add more than three " + "Nodes to Triangle.");
            }

            super.add(n);

            if (this.size() == 3) {
                calculateCircle();
                // logger.debug("This triangle has " + mCentre.getLat() + " " +
                // mCentre.getLng() + " " + mRad);
            }

            return true;
        }

        @Override
        public Node get(int index) {
            if (index > 3) {
                throw new IllegalStateException("A triangle has only three points");
            }
            return super.get(index);
        }

        public boolean isEncompassing(Node n) {
            // logger.debug("Values for isEncompassing: "+n.naiveDist(mCentre)+" "+mRad
            // );
            return n.naiveDist(mCentre) <= mRad;
        }

        private void calculateCircle() {
            mCentre = new Node(0, 0);

            double x1 = this.get(0).getLng();
            double y1 = this.get(0).getLat();

            double x2 = this.get(1).getLng();
            double y2 = this.get(1).getLat();

            double x3 = this.get(2).getLng();
            double y3 = this.get(2).getLat();

            double dy21 = y2 - y1;
            double dy32 = y3 - y2;

            // logger.debug("Values: " + x1 + " " + y1 + " " + x2 + " " + y2 +
            // " " + x3 + " " + y3 );

            // Checking for edge cases
            // Mostly to prevent division by zero
            if (isZero(dy21) && isZero(dy32)) {
                // All three are on the same horizontal line
                if (x2 > x1) {
                    if (x3 > x2) {
                        x2 = x3;
                    }
                } else if (x3 < x1) {
                    x1 = x3;
                }

                mCentre.setLng((x1 + x2) / 2);
                mCentre.setLat(y1);
                // logger.debug("branch 1");
            } else if (isZero(dy21)) {
                // Node1 and Node2 are on the same horizontal line
                // logger.debug("branch 2: " + dy21 + " " +dy32);
                double m1 = -(x3 - x2) / dy32;

                double mx1 = (x2 + x3) / 2;
                double my1 = (y2 + y3) / 2;

                mCentre.setLng((x1 + x2) / 2);
                mCentre.setLat(m1 * (mCentre.getLng() - mx1) + my1);
                // logger.debug("branch 2: " + mCentre.getLat() + " " +
                // mCentre.getLng());
            } else if (isZero(dy32)) {
                // Node2 and Node3 are on the same horizontal line
                double m0 = -(x2 - x1) / dy21;

                double mx0 = (x1 + x2) / 2;
                double my0 = (y1 + y2) / 2;

                mCentre.setLng((x2 + x3) / 2);
                mCentre.setLat(m0 * (mCentre.getLng() - mx0) + my0);
                // logger.debug("branch 3");
                
            // Common case. No Nodes are on the same horizontal line
            } else {
                double m0 = -(x2 - x1) / dy21;
                double m1 = -(x3 - x2) / dy32;

                double mx0 = (x1 + x2) / 2;
                double my0 = (y1 + y2) / 2;

                double mx1 = (x2 + x3) / 2;
                double my1 = (y2 + y3) / 2;

                mCentre.setLng((m0 * mx0 - m1 * mx1 + my1 - my0) / (m0 - m1));
                mCentre.setLat(m0 * (mCentre.getLng() - mx0) + my0);
                // logger.debug("branch 4");
            }

            // Calculating circle's radius
            double rx = x3 - mCentre.getLng();
            double ry = y3 - mCentre.getLat();
            double r2 = rx * rx + ry * ry;
            mRad = Math.sqrt(r2);

            // make the radius slightly bigger
            mRad = mRad + 2 * Node.EPSILON;
        }

        private boolean isZero(double d) {
            return Math.abs(d) < Node.EPSILON;
        }

        public boolean isLeftOf(Node n) {
            return n.getLng() > (mCentre.getLng() + mRad);
        }

    }

}
