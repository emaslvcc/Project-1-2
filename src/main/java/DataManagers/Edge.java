package DataManagers;

/**
 * The Edge class represents a connection between two nodes in a graph with an
 * associated weight.
 */
public class Edge {
    int id;
    Node source;
    Node destination;
    double weight;

    /**
     * Constructs an Edge with the specified id, source node, destination node, and
     * weight.
     *
     * @param id          The unique identifier for this edge.
     * @param source      The source node of the edge.
     * @param destination The destination node of the edge.
     * @param weight      The weight of the edge, representing the cost or distance
     *                    between the nodes.
     */
    public Edge(int id, Node source, Node destination, double weight) {
        this.id = id;
        this.source = source;
        this.destination = destination;
        this.weight = weight;
    }

    public Node getSource() {
        return source;
    }

    public Node getDestination() {
        return destination;
    }

    public double getWeight() {
        return weight;
    }
}
