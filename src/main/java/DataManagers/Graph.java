package DataManagers;

import java.util.*;

/**
 * The Graph class represents a graph data structure using an adjacency list.
 * It stores nodes and their adjacent edges.
 */
public class Graph {
    Map<Node, List<Edge>> adjacencyList = new HashMap<>();

    public void addNode(Node node) {
        adjacencyList.put(node, new ArrayList<>());
    }

    public void addEdge(Edge edge) {
        adjacencyList.get(edge.getSource()).add(edge);
    }

    public List<Edge> getEdges(Node nodeID) {
        return adjacencyList.get(nodeID);
    }

    /**
     * Finds the node in the graph that is closest to the specified latitude and
     * longitude coordinates.
     *
     * @param lat The latitude coordinate.
     * @param lon The longitude coordinate.
     * @return The node in the graph closest to the specified coordinates.
     */
    public Node getNodeByLatLon(double lat, double lon) {
        Node closestNode = null;
        double closestDistance = Double.MAX_VALUE;

        for (Node node : adjacencyList.keySet()) {
            double distance = Math.sqrt(Math.pow(node.getLat() - lat, 2) + Math.pow(node.getLon() - lon, 2));

            if (distance < closestDistance) {
                closestDistance = distance;
                closestNode = node;
            }
        }
        return closestNode;
    }
}
