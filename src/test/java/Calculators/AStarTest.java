package Calculators;

import DataManagers.Edge;
import DataManagers.Graph;
import DataManagers.Node;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AStarTest {

    @Test
    void findShortestPath() {
        Graph graph = new Graph();
        Node node1 = new Node(1,10,20);
        Node node2 = new Node(2,10.5,15);
        Node node3 = new Node(3,20,10);
        Node node4 = new Node(4,21,9);

        Edge edge1 = new Edge(1,node1,node2,321);
        Edge edge2 = new Edge(2,node1,node3,30);
        Edge edge3 = new Edge(3,node2,node4,100);
        Edge edge4 = new Edge(4,node3,node4,55);

        graph.addNode(node1);
        graph.addNode(node2);
        graph.addNode(node3);
        graph.addNode(node4);

        graph.addEdge(edge1);
        graph.addEdge(edge2);
        graph.addEdge(edge3);
        graph.addEdge(edge4);

        AStar star = new AStar(graph);
        List<Node> nodeList = star.findShortestPath(node1,node4);
        assertEquals(node1, nodeList.getFirst());
        assertEquals(node3, nodeList.get(1));
        assertEquals(node4, nodeList.get(2));


    }
}