package Bus;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class DistanceCache {
    // Nested class for the composite key
    private static class RouteKey {
        private final double startLat;
        private final double startLon;
        private final double endLat;
        private final double endLon;

        public RouteKey(double startLat, double startLon, double endLat, double endLon) {
            this.startLat = startLat;
            this.startLon = startLon;
            this.endLat = endLat;
            this.endLon = endLon;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;
            RouteKey routeKey = (RouteKey) o;
            return Double.compare(routeKey.startLat, startLat) == 0 &&
                    Double.compare(routeKey.startLon, startLon) == 0 &&
                    Double.compare(routeKey.endLat, endLat) == 0 &&
                    Double.compare(routeKey.endLon, endLon) == 0;
        }

        @Override
        public int hashCode() {
            return Objects.hash(startLat, startLon, endLat, endLon);
        }
    }

    // The actual cache using the RouteKey as keys
    private final static Map<RouteKey, Double> cache = new HashMap<>();

    public static Double getDistance(double startLat, double startLon, double endLat, double endLon) {
        RouteKey key = new RouteKey(startLat, startLon, endLat, endLon);
        return cache.get(key);
    }

    public void putDistance(double startLat, double startLon, double endLat, double endLon, double distance) {
        RouteKey key = new RouteKey(startLat, startLon, endLat, endLon);
        cache.put(key, distance);
    }
}
