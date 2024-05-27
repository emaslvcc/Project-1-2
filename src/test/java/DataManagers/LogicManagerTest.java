package DataManagers;

import Calculators.DistanceCalculatorHaversine;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LogicManagerTest {

    @Test
    void calculateDistance() {
        LogicManager lm = new LogicManager();
        List<Node> nodeList = new ArrayList<>();

        Node node1 = new Node(1,10,4.5);
        Node node2 = new Node(2,11.32,4.231);
        Node node3 = new Node(3,11.42,4.3);


        nodeList.add(node1);
        nodeList.add(node2);

        DistanceCalculatorHaversine calc1 = new DistanceCalculatorHaversine(new PostCode("",node1.lat, node1.lon), new PostCode("",node2.lat, node2.lon));

        nodeList.add(node3);

        DistanceCalculatorHaversine calc2 = new DistanceCalculatorHaversine(new PostCode("",node2.lat, node2.lon), new PostCode("",node3.lat, node3.lon));

        assertEquals(lm.calculateDistance(nodeList), calc1.getDistance() + calc2.getDistance());
    }
}