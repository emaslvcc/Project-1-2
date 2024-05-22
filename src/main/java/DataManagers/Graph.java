package DataManagers;

import java.util.*;

public class Graph {
    Map<Node, List<Edge>> adjacencyList = new HashMap<>();

    public void addNode(Node node) {
        adjacencyList.put(node, new ArrayList<>());
    }

    public void addEdge(Edge edge) {
        adjacencyList.get(edge.getSource()).add(edge);
    }

    public List<Edge> getEdges(Node nodeID){
        return adjacencyList.get(nodeID);
    }

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

        return closestNode; // Return the closest node
    }

}
