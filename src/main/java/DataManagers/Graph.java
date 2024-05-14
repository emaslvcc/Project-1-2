package DataManagers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Graph {
    Map<Integer, Node> nodes = new HashMap<>();
    List<Edge> edges = new ArrayList<>();

    public void addNode(Node node) {
        nodes.put(node.id, node);
    }

    public void addEdge(Edge edge) {
        edges.add(edge);
    }

    public Node getNode(int id) {
        return nodes.get(id);
    }

    public List<Edge> getEdges(){
        return edges;
    }

    public Node getNodeByLatLon(double lat, double lon) {
        Node closestNode = null;
        double closestDistance = Double.MAX_VALUE;

        for (Node node : nodes.values()) {
            double distance = Math.sqrt(Math.pow(node.getLat() - lat, 2) + Math.pow(node.getLon() - lon, 2));

            if (distance < closestDistance) {
                closestDistance = distance;
                closestNode = node;
            }
        }

        return closestNode; // Return the closest node
    }

}
