package DataManagers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GraphTest {

    @Test
    @DisplayName("Testing of edge retrieval from graph")
    void getEdges() {
        Graph graph = new Graph();
        Node node1 = new Node(1,10,20);
        Node node2 = new Node(2,5,10);

        Edge edge1 = new Edge(1,node1,node2,321);
        Edge edge2 = new Edge(2,node1,node2,30);
        Edge edge3 = new Edge(3,node2,node1,200);

        graph.addNode(node1);
        graph.addNode(node2);

        graph.addEdge(edge1);
        graph.addEdge(edge2);
        graph.addEdge(edge3);

        assertTrue(graph.getEdges(node1).contains(edge1) && graph.getEdges(node1).contains(edge2));
        assertTrue(graph.getEdges(node2).contains(edge3));
    }

    @Test
    @DisplayName("Testing getting closest node")
    void getNodeByLatLon() {
        Graph graph = new Graph();
        Node node1 = new Node(1,10,20);
        Node node2 = new Node(2,5,10);
        Node node3 = new Node(3,0,0);

        graph.addNode(node1);
        graph.addNode(node2);
        graph.addNode(node3);

        assertEquals(graph.getNodeByLatLon(0,0), node3);
        assertEquals(graph.getNodeByLatLon(2,5), node3);
        assertEquals(graph.getNodeByLatLon(-1,-1.02), node3);
        assertEquals(graph.getNodeByLatLon(30,50), node1);
        assertEquals(graph.getNodeByLatLon(7,13.232390), node2);
    }
}