package Bus;

import java.util.List;

import DataManagers.LogicManager;
import DataManagers.Node;

public class test {
    public static void main(String[] args) {
        List<Node> shortestPath = LogicManager.calculateRouteByCoordinates(50.838541, 5.73436281,
                50.8403, 5.73326,
                "walk");
        double distanceBetweenTwoZipCodes = LogicManager.calculateDistance(shortestPath);

        System.out.println(distanceBetweenTwoZipCodes);
    }
}
