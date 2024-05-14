package DataManagers;

public class Edge {
    int id;
    Node source;
    Node destination;
    double weight;

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
