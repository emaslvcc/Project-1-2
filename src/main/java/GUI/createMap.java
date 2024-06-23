package GUI;

import Calculators.AverageTimeCalculator;
import Calculators.TimeCalculator;
import DataManagers.LogicManager;
import DataManagers.Node;
import DataManagers.PostCode;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.input.PanMouseInputListener;
import org.jxmapviewer.input.ZoomMouseWheelListenerCenter;
import org.jxmapviewer.painter.Painter;
import org.jxmapviewer.viewer.DefaultTileFactory;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.TileFactoryInfo;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.net.URL;
import java.util.List;

/**
 * Manages the creation and display of the map.
 */
public class createMap {
    private static JXMapViewer jXMapViewer;
    private static double startLatitude = 0;
    private static double startLongitude = 0;
    private static double endLatitude = 0;
    private static double endLongitude = 0;

    /**
     * Creates a JPanel containing the map.
     *
     * @return The JPanel containing the map.
     */
    public static JPanel createMapPanel() {
        jXMapViewer = new JXMapViewer();
        jXMapViewer.setPreferredSize(new Dimension(900, 440));
        init();

        JCheckBox showAccessibilityBox = new JCheckBox("Show Accessibility");
        showAccessibilityBox.setFont(new Font("Segoe UI", 1, 12));
        showAccessibilityBox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    System.out.println("check");
                } else {
                    System.out.println("uncheck");
                }
            }
        });

        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(900, 440));

        jXMapViewer.setBounds(0, 0, 900, 440);
        layeredPane.add(jXMapViewer, Integer.valueOf(0));

        showAccessibilityBox.setBounds(10, 10, 130, 20);
        layeredPane.add(showAccessibilityBox, Integer.valueOf(1));

        JPanel Center = new JPanel();
        Center.setLayout(new BorderLayout());
        Center.add(layeredPane, BorderLayout.CENTER);

        Center.validate();
        Center.repaint();

        return Center;
    }

    /**
     * Initializes the map creation.
     */
    private static void init() {
        TileFactoryInfo info = new OSMTileFactoryInfo();
        DefaultTileFactory tileFactory = new DefaultTileFactory(info);
        jXMapViewer.setTileFactory(tileFactory);

        GeoPosition Maastricht = new GeoPosition(50.8513682, 5.6909725); // Coordinates for Maastricht
        jXMapViewer.setAddressLocation(Maastricht);
        jXMapViewer.setZoom(6);

        // The boundaries of Maastricht on the map
        double minLatitude = 50.80;
        double maxLatitude = 50.90;
        double minLongitude = 5.62;
        double maxLongitude = 5.76;

        // Define the maximum zoom level
        int maxZoom = 7;

        // Create a custom PanMouseInputListener
        MouseInputListener mm = new PanMouseInputListener(jXMapViewer) {
            private GeoPosition lastPosition;

            @Override
            public void mousePressed(MouseEvent e) {
                lastPosition = jXMapViewer.getCenterPosition();
                super.mousePressed(e);
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                super.mouseDragged(e);

                // Check if the new center position is within the boundaries of Maastricht
                GeoPosition newPosition = jXMapViewer.getCenterPosition();

                if (newPosition.getLatitude() < minLatitude || newPosition.getLatitude() > maxLatitude
                        || newPosition.getLongitude() < minLongitude || newPosition.getLongitude() > maxLongitude) {
                    // If not, reset the center position to the previous position
                    jXMapViewer.setCenterPosition(lastPosition);
                } else {
                    // If yes, update the last position
                    lastPosition = newPosition;
                }
            }
        };

        jXMapViewer.addMouseListener(mm);
        jXMapViewer.addMouseMotionListener(mm);

        // Create a custom ZoomMouseWheelListenerCenter
        jXMapViewer.addMouseWheelListener(new ZoomMouseWheelListenerCenter(jXMapViewer) {

            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                int lastZoom = jXMapViewer.getZoom();
                super.mouseWheelMoved(e);

                // Checks if the new zoom level is within the maximum zoom level
                int newZoom = jXMapViewer.getZoom();
                if (newZoom > maxZoom) {
                    // If not, reset the zoom level to the previous level
                    jXMapViewer.setZoom(lastZoom);
                }
            }
        });
    }

    /**
     * Updates the start and end coordinates for drawing on the map.
     *
     * @param startPostCode The starting post code.
     * @param endPostCode   The ending post code.
     */
    public static void updateCoord(PostCode startPostCode, PostCode endPostCode) {
        startLatitude = startPostCode.getLatitude();
        startLongitude = startPostCode.getLongitude();
        endLatitude = endPostCode.getLatitude();
        endLongitude = endPostCode.getLongitude();

        System.out.println("Start Coord: " + startLatitude + ", " + startLongitude);
        System.out.println("End Coord: " + endLatitude + ", " + endLongitude);
    }

    public static void drawPath(List<Node> stops, String firstTripColor) {

        Painter<JXMapViewer> pathOverlay = new Painter<JXMapViewer>() {
            @Override
            public void paint(Graphics2D g, JXMapViewer map, int w, int h) {
                try {
                    Color firstLineColor;
                    if ("blue".equals(firstTripColor) || firstTripColor == null) {
                        firstLineColor = Color.BLUE;
                    } else {
                        firstLineColor = hexToColor(firstTripColor);
                    }

                    // Draw start and end markers
                    // createStartAndEndPoints(g, map);
                    createStartAndEndPointsForBus(g, map, stops);
                    Point2D pointMapPrev = null; // Initialize a variable to hold the previous point

                    for (int i = 0; i < stops.size(); i++) {
                        Node node = stops.get(i);
                        GeoPosition point = new GeoPosition(node.getLat(), node.getLon());
                        Point2D pointMap = map.convertGeoPositionToPoint(point);

                        // Draw a red circle at each bus stop
                        g.setColor(Color.RED); // Set the color for the bus stops
                        Ellipse2D.Double circle = new Ellipse2D.Double(pointMap.getX() - 5, pointMap.getY() - 5, 10,
                                10);
                        g.fill(circle);

                        // If this is not the first stop, draw a blue line from the previous stop to the
                        // current stop
                        if (i > 0) {
                            g.setColor(firstLineColor);
                            // Set the color for the lines
                            g.setStroke(new BasicStroke(3));
                            g.drawLine((int) pointMapPrev.getX(), (int) pointMapPrev.getY(), (int) pointMap.getX(),
                                    (int) pointMap.getY());
                        }

                        // Update pointMapPrev to the current stop for the next iteration
                        pointMapPrev = pointMap;
                    }

                } catch (Exception e) {
                    System.out.println("Error in drawing path" + e);
                    e.printStackTrace();
                } finally {
                    g.dispose();
                }

            }
        };
        // Set the new painter
        jXMapViewer.setOverlayPainter(pathOverlay);
    }

    public static void drawPath(List<Node> stops) {

        Painter<JXMapViewer> pathOverlay = new Painter<JXMapViewer>() {
            @Override
            public void paint(Graphics2D g, JXMapViewer map, int w, int h) {
                try {
                    // Draw start and end markers
                    createStartAndEndPoints(g, map);
                    Point2D pointMapPrev = null; // Initialize a variable to hold the previous point

                    for (int i = 0; i < stops.size(); i++) {
                        Node node = stops.get(i);
                        GeoPosition point = new GeoPosition(node.getLat(), node.getLon());
                        Point2D pointMap = map.convertGeoPositionToPoint(point);

                        // If this is not the first stop, draw a blue line from the previous stop to the
                        // current stop
                        if (i > 0) {
                            g.setColor(Color.GREEN); // Set the color for the lines
                            g.setStroke(new BasicStroke(3));
                            g.drawLine((int) pointMapPrev.getX(), (int) pointMapPrev.getY(), (int) pointMap.getX(),
                                    (int) pointMap.getY());
                        }

                        // Update pointMapPrev to the current stop for the next iteration
                        pointMapPrev = pointMap;
                    }

                } catch (Exception e) {
                    System.out.println("Error in drawing path" + e);
                    e.printStackTrace();
                } finally {
                    g.dispose();
                }

            }
        };
        jXMapViewer.setOverlayPainter(pathOverlay);

    }

    public static void drawPath(List<Node> path, List<Node> stops, String firstTripColor) {

        Painter<JXMapViewer> pathOverlay = new Painter<JXMapViewer>() {
            @Override
            public void paint(Graphics2D g, JXMapViewer map, int w, int h) {
                try {
                    Color firstLineColor;
                    if ("blue".equals(firstTripColor)) {
                        firstLineColor = Color.BLUE;
                    } else {
                        firstLineColor = hexToColor(firstTripColor);
                    }

                    g.setColor(firstLineColor);
                    g.setStroke(new BasicStroke(3));

                    for (int i = 0; i < path.size() - 1; i++) {

                        Node startNode = path.get(i);
                        Node endNode = path.get(i + 1);
                        GeoPosition point1 = new GeoPosition(startNode.getLat(), startNode.getLon());
                        GeoPosition point2 = new GeoPosition(endNode.getLat(), endNode.getLon());
                        Point2D startP = map.convertGeoPositionToPoint(point1);
                        Point2D endP = map.convertGeoPositionToPoint(point2);
                        g.draw(new Line2D.Double(startP, endP));
                    }

                    if (stops != null) {
                        createStartAndEndPointsForBus(g, map, stops);

                        g.setColor(Color.RED);
                        for (int i = 0; i < stops.size(); i++) {
                            Node node = stops.get(i);
                            GeoPosition point = new GeoPosition(node.getLat(), node.getLon());
                            Point2D pointMap = map.convertGeoPositionToPoint(point);
                            Ellipse2D.Double circle = new Ellipse2D.Double(pointMap.getX(), pointMap.getY(), 10, 10);
                            g.fill(circle);
                        }
                    } else {
                        // Draw start and end markers
                        createStartAndEndPoints(g, map);
                    }

                } catch (Exception e) {
                    System.out.println("Error in drawing path" + e);
                    e.printStackTrace();
                } finally {
                    g.dispose();
                }

            }
        };
        // Set the new painter
        jXMapViewer.setOverlayPainter(pathOverlay);

    }

    /**
     * Draws the path on the map.
     *
     * @param stops The list of stops along the path.
     */
    public static void drawPath(List<Node> stops, int num, String firstTripColor, String secondTripColor) {

        Painter<JXMapViewer> pathOverlay = new Painter<JXMapViewer>() {
            @Override
            public void paint(Graphics2D g, JXMapViewer map, int w, int h) {
                try {
                    Color firstLineColor;
                    if ("blue".equals(firstTripColor)) {
                        firstLineColor = Color.BLUE;
                    } else {
                        firstLineColor = hexToColor(firstTripColor);
                    }

                    Color secondLineColor;
                    if ("blue".equals(secondTripColor)) {
                        secondLineColor = Color.BLUE;
                    } else {
                        secondLineColor = hexToColor(secondTripColor);
                    }
                    // Draw start and end markers
                    // createStartAndEndPoints(g, map);
                    createStartAndEndPointsForBus(g, map, stops);
                    Point2D pointMapPrev = null; // Initialize a variable to hold the previous point

                    for (int i = 0; i < stops.size(); i++) {
                        Node node = stops.get(i);
                        GeoPosition point = new GeoPosition(node.getLat(), node.getLon());
                        Point2D pointMap = map.convertGeoPositionToPoint(point);

                        // Draw a red circle at each bus stop
                        g.setColor(Color.RED); // Set the color for the bus stops
                        Ellipse2D.Double circle = new Ellipse2D.Double(pointMap.getX() - 5, pointMap.getY() - 5, 10,
                                10);
                        g.fill(circle);

                        // If this is not the first stop, draw a blue line from the previous stop to the
                        // current stop

                        g.setColor(firstLineColor);
                        if (0 < i && i < num) {
                            // Set the color for the lines
                            g.setStroke(new BasicStroke(3));
                            g.drawLine((int) pointMapPrev.getX(), (int) pointMapPrev.getY(), (int) pointMap.getX(),
                                    (int) pointMap.getY());

                        } else if (i >= num) {
                            g.setColor(secondLineColor);
                            g.setStroke(new BasicStroke(3));
                            g.drawLine((int) pointMapPrev.getX(), (int) pointMapPrev.getY(), (int) pointMap.getX(),
                                    (int) pointMap.getY());

                        }

                        // Update pointMapPrev to the current stop for the next iteration
                        pointMapPrev = pointMap;
                    }

                } catch (Exception e) {
                    System.out.println("Error in drawing path" + e);
                    e.printStackTrace();
                } finally {
                    g.dispose();
                }

            }
        };
        // Set the new painter
        jXMapViewer.setOverlayPainter(pathOverlay);

    }

    /**
     * Draws the path on the map.
     *
     * @param stops The list of stops along the path.
     * @param path  The list of paths.
     */
    public static void drawPath(List<Node> path, List<Node> stops, int num, String firstTripColor,
                                String secondTripColor) {

        Painter<JXMapViewer> pathOverlay = new Painter<JXMapViewer>() {
            @Override
            public void paint(Graphics2D g, JXMapViewer map, int w, int h) {
                try {
                    Color firstLineColor;
                    if ("blue".equals(firstTripColor)) {
                        firstLineColor = Color.BLUE;
                    } else {
                        firstLineColor = hexToColor(firstTripColor);
                    }

                    Color secondLineColor;
                    if ("blue".equals(secondTripColor)) {
                        secondLineColor = Color.BLUE;
                    } else {
                        secondLineColor = hexToColor(secondTripColor);
                    }

                    g.setColor(firstLineColor);
                    g.setStroke(new BasicStroke(3));

                    for (int i = 0; i < path.size() - 1; i++) {
                        if (i >= num) {
                            g.setColor(secondLineColor);
                        }
                        Node startNode = path.get(i);
                        Node endNode = path.get(i + 1);
                        GeoPosition point1 = new GeoPosition(startNode.getLat(), startNode.getLon());
                        GeoPosition point2 = new GeoPosition(endNode.getLat(), endNode.getLon());
                        Point2D startP = map.convertGeoPositionToPoint(point1);
                        Point2D endP = map.convertGeoPositionToPoint(point2);
                        g.draw(new Line2D.Double(startP, endP));
                    }

                    if (stops != null) {
                        createStartAndEndPointsForBus(g, map, stops);

                        g.setColor(Color.RED);
                        for (int i = 0; i < stops.size(); i++) {
                            Node node = stops.get(i);
                            GeoPosition point = new GeoPosition(node.getLat(), node.getLon());
                            Point2D pointMap = map.convertGeoPositionToPoint(point);
                            Ellipse2D.Double circle = new Ellipse2D.Double(pointMap.getX(), pointMap.getY(), 10, 10);
                            g.fill(circle);
                        }
                    } else {
                        // Draw start and end markers
                        createStartAndEndPoints(g, map);
                    }

                } catch (Exception e) {
                    System.out.println("Error in drawing path" + e);
                    e.printStackTrace();
                } finally {
                    g.dispose();
                }

            }
        };
        // Set the new painter
        jXMapViewer.setOverlayPainter(pathOverlay);

    }

    private static Color hexToColor(String colorStr) {
        if (!colorStr.startsWith("#")) {
            colorStr = "#" + colorStr; // Ensure the string starts with "#"
        }
        return Color.decode(colorStr);
    }

    private static void createStartAndEndPointsForBus(Graphics2D g, JXMapViewer map, List<Node> stops) {
        GeoPosition startBusStop = new GeoPosition(stops.get(0).getLat(), stops.get(0).getLon());
        GeoPosition endBusStop = new GeoPosition(stops.get(stops.size() - 1).getLat(),
                stops.get(stops.size() - 1).getLon());
        drawWalkingPath(g, startBusStop.getLatitude(), startBusStop.getLongitude(), endBusStop.getLatitude(),
                endBusStop.getLongitude(), map);
        createStartAndEndPoints(g, map);
    }


    private static List<Node> cachedPath1 = null;
    private static List<Node> cachedPath2 = null;
    private static PostCode cachedStartPostCode = null;
    private static PostCode cachedEndPostCode = null;
    private static PostCode cachedStartBus = null;
    private static PostCode cachedEndBus = null;


    private static void drawWalkingPath(Graphics2D g, double startBusLat, double startBusLong, double endBusLat,
                                        double endBusLong, JXMapViewer map) {
        PostCode startPostCode = new PostCode("Start", startLatitude, startLongitude);
        PostCode endPostCode = new PostCode("End", endLatitude, endLongitude);

        PostCode startBus = new PostCode("Start Bus", startBusLat, startBusLong);
        PostCode endBus = new PostCode("End Bus", endBusLat, endBusLong);
        List<Node> path1;
        List<Node> path2;

        LogicManager logicManager = new LogicManager();
        if (cachedPath1 != null && cachedStartPostCode.getLatitude() == startPostCode.getLatitude()
                && cachedStartPostCode.getLongitude() == startPostCode.getLongitude()
                && cachedEndBus.getLatitude() == endBus.getLatitude()
                && cachedEndBus.getLongitude() == endBus.getLongitude()) {
            path1 = cachedPath1;
        } else {
            logicManager.calculateRoute(startPostCode, startBus, "Walk");
            path1 = logicManager.getShortestPath();
            cachedPath1 = path1;
            cachedStartPostCode = startPostCode;
            cachedEndBus = endBus;
        }

        if (cachedPath2 != null && cachedStartBus.getLatitude() == startBus.getLatitude()
                && cachedStartBus.getLongitude() == startBus.getLongitude()
                && cachedEndPostCode.getLatitude() == endPostCode.getLatitude()
                && cachedEndPostCode.getLongitude() == endPostCode.getLongitude()) {
            path2 = cachedPath2;
        } else {
            logicManager.calculateRoute(endBus, endPostCode, "Walk");
            path2 = logicManager.getShortestPath();
            cachedPath2 = path2;
            cachedStartBus = startBus;
            cachedEndPostCode = endPostCode;
        }
        List<List<Node>> paths = new java.util.ArrayList<>();
        if (path1 == null || path1.isEmpty()) {
            drawLineBetweenPoints(g, map, startPostCode, startBus);
        } else {
            paths.add(path1);
        }

        if (path2 == null || path2.isEmpty()) {
            drawLineBetweenPoints(g, map, endPostCode, endBus);
        } else {
            paths.add(path2);
        }
        drawPaths(paths, g, map);
    }

    private static void drawLineBetweenPoints(Graphics2D g, JXMapViewer map, PostCode point1, PostCode point2) {
        GeoPosition geoPoint1 = new GeoPosition(point1.getLatitude(), point1.getLongitude());
        GeoPosition geoPoint2 = new GeoPosition(point2.getLatitude(), point2.getLongitude());
        Point2D mapPoint1 = map.convertGeoPositionToPoint(geoPoint1);
        Point2D mapPoint2 = map.convertGeoPositionToPoint(geoPoint2);
        g.draw(new Line2D.Double(mapPoint1, mapPoint2));
    }

    public static void drawPaths(List<List<Node>> paths, Graphics2D g, JXMapViewer map) {
        g.setColor(Color.GREEN);

        if (paths != null) {
            for (List<Node> path : paths) {
                for (int i = 0; i < path.size() - 1; i++) {
                    Node startNode = path.get(i);
                    Node endNode = path.get(i + 1);
                    GeoPosition point1 = new GeoPosition(startNode.getLat(), startNode.getLon());
                    GeoPosition point2 = new GeoPosition(endNode.getLat(), endNode.getLon());
                    Point2D startP = map.convertGeoPositionToPoint(point1);
                    Point2D endP = map.convertGeoPositionToPoint(point2);
                    g.draw(new Line2D.Double(startP, endP));
                }
            }
        }

    }

    /**
     * Creates and draws start and end points on the map.
     *
     * @param g   The graphics context used for drawing.
     * @param map The map on which the points will be drawn.
     */
    private static void createStartAndEndPoints(Graphics2D g, JXMapViewer map) {
        GeoPosition startPos = new GeoPosition(startLatitude, startLongitude);
        GeoPosition endPos = new GeoPosition(endLatitude, endLongitude);

        Image PointerImage = returnImage("/Images/pointer.png");
        Image PersonImage = returnImage("/Images/Person.png");

        g = (Graphics2D) g.create();
        Point2D start = map.convertGeoPositionToPoint(startPos);
        Point2D end = map.convertGeoPositionToPoint(endPos);

        assert PointerImage != null;
        assert PersonImage != null;

        int PointX = PointerImage.getWidth(null);
        int PointY = PointerImage.getHeight(null);
        int PersonX = PersonImage.getWidth(null);
        int PersonY = PersonImage.getHeight(null);


        g.drawImage(PersonImage, (int) start.getX() - PersonX / 2, (int) start.getY() - PersonY, null);
        g.drawImage(PointerImage, (int) end.getX() - PointX / 2, (int) end.getY() - PointY, null);

    }


    /**
     * Creates and returns an image for the start and end points.
     *
     * @return The image for the start and end points.
     */
    public static Image returnImage(String path) {
        Image pointerImage;
        try {
            URL imageUrl = createMap.class.getResource(path);
            assert imageUrl != null;
            pointerImage = ImageIO.read(imageUrl);
            pointerImage = pointerImage.getScaledInstance(25, 25, Image.SCALE_SMOOTH);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return pointerImage;
    }


    /**
     * Clear the map.
     */
    public static void clearMap() {
        jXMapViewer.setOverlayPainter(null);
        startLatitude = Double.NaN;
        startLongitude = Double.NaN;
        endLatitude = Double.NaN;
        endLongitude = Double.NaN;
    }
}
