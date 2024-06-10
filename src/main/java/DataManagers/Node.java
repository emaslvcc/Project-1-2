package DataManagers;

/**
 * Represents a node in a graph with an id, latitude, and longitude.
 */
public class Node {
    int id;
    double lat;
    double lon;

    /**
     * Constructs a new Node with the specified id, latitude, and longitude.
     *
     * @param id  The id of the node.
     * @param lat The latitude of the node.
     * @param lon The longitude of the node.
     */
    public Node(int id, double lat, double lon) {
        this.id = id;
        this.lat = lat;
        this.lon = lon;
    }

    public int getId() {
        return id;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    /**
     * Checks if this Node is equal to another Object.
     *
     * @param obj The Object to compare with this Node.
     * @return true if the Object is a Node and has the same id as this Node,
     *         otherwise false.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj.getClass() != this.getClass()) {
            return false;
        }

        final Node other = (Node) obj;

        return this.id == other.id;

    }

    /**
     * Generates a hash code for this Node.
     *
     * @return The hash code for this Node based on its id.
     */
    @Override
    public int hashCode() {
        return id;
    }
}