package Calculators;

import java.util.*;

import DataManagers.*;

public class AStar {
    private Graph graph;

    public AStar(Graph graph) {
        this.graph = graph;
    }

    public List<Node> findShortestPath(Node start, Node goal) {
        Map<Node, Node> cameFrom = new HashMap<>();
        Map<Node, Double> costSoFar = new HashMap<>();
        PriorityQueue<Node> frontier = new PriorityQueue<>(Comparator.comparingDouble(n -> costSoFar.get(n) + heuristic(n, goal)));

        frontier.add(start);
        cameFrom.put(start, null);
        costSoFar.put(start, 0.0);

        while (!frontier.isEmpty()) {
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

    //euclidean distance
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
