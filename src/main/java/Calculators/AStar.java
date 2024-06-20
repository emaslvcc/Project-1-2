package Calculators;

import java.util.*;

import DataManagers.*;

/**
 * The AStar class implements the A* algorithm for finding the shortest path
 * between two nodes in a graph.
 */
public class AStar {
    private Graph graph;

    /**
     * Constructs an AStar instance with the given graph.
     *
     * @param graph the graph on which to perform the A* search
     */
    public AStar(Graph graph) {
        this.graph = graph;
    }

    /**
     * Finds the shortest path between the start and goal nodes using the A*
     * algorithm.
     *
     * @param start the starting node
     * @param goal  the goal node
     * @return a list of nodes representing the shortest path from start to goal, or
     *         null if no path is found
     */
    public List<Node> findShortestPath(Node start, Node goal) {
        Map<Node, Node> cameFrom = new HashMap<>();
        Map<Node, Double> costSoFar = new HashMap<>();
        PriorityQueue<Node> frontier = new PriorityQueue<>(
                Comparator.comparingDouble(n -> costSoFar.get(n) + heuristic(n, goal)));

        frontier.add(start);
        cameFrom.put(start, null);
        costSoFar.put(start, 0.0);
        // long startTime = System.currentTimeMillis(); // Start time
        // long timeLimit = 2000; // Time limit in milliseconds

        while (!frontier.isEmpty()) {
            // System.out.println(System.currentTimeMillis() - startTime);
            // if (System.currentTimeMillis() - startTime > timeLimit) {
            // break;
            // }
            Node current = frontier.poll();

            if (current.equals(goal)) {

                System.out.println("Path found");
                return constructPath(cameFrom, goal);
            }

            for (Edge edge : graph.getEdges(current)) {

                Node next = edge.getDestination();
                double newCost = costSoFar.get(current) + edge.getWeight();

                if (!costSoFar.containsKey(next) || newCost < costSoFar.get(next)) {
                    costSoFar.put(next, newCost);
                    frontier.add(next);
                    cameFrom.put(next, current);
                }
            }
        }

        System.out.println("No path found");
        return null;
    }

    /**
     * Calculates the estimate of the distance between two nodes using the Euclidean
     * formula.
     *
     * @param a the first node
     * @param b the second node
     * @return the heuristic estimate of the distance between node a and node b
     */
    private static double heuristic(Node a, Node b) {
        return Math.sqrt(Math.pow(a.getLat() - b.getLat(), 2) + Math.pow(a.getLon() - b.getLon(), 2));
    }

    private List<Node> constructPath(Map<Node, Node> cameFrom, Node goal) {
        List<Node> path = new ArrayList<>();
        Node current = goal;

        while (current != null) {
            path.add(current);
            current = cameFrom.get(current);
        }

        Collections.reverse(path);
        return path;
    }
}
