package GUI;

import DataManagers.PostCode;
import com.graphhopper.ResponsePath;
import com.graphhopper.util.PointList;
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
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import DataManagers.Node;

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
        jXMapViewer.setPreferredSize(new Dimension(200, 440));
        init();

        JPanel Center = new JPanel();
        Center.setLayout(new BorderLayout());
        Center.add(jXMapViewer, BorderLayout.CENTER);

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
    }

    /**
     * Draws the path on the map.
     *
     * @param stops The list of stops along the path.
     */
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

                        // Draw a red circle at each bus stop
                        g.setColor(Color.RED); // Set the color for the bus stops
                        Ellipse2D.Double circle = new Ellipse2D.Double(pointMap.getX() - 5, pointMap.getY() - 5, 10,
                                10);
                        g.fill(circle);

                        // If this is not the first stop, draw a blue line from the previous stop to the
                        // current stop
                        if (i > 0) {
                            g.setColor(Color.BLUE); // Set the color for the lines
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
     * @param path The list of paths.
     */
    public static void drawPath(List<Node> path, List<Node> stops) {

        Painter<JXMapViewer> pathOverlay = new Painter<JXMapViewer>() {
            @Override
            public void paint(Graphics2D g, JXMapViewer map, int w, int h) {
                try {
                    g.setColor(Color.BLUE);
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
                    // Draw start and end markers
                    createStartAndEndPoints(g, map);

                    if (stops != null) {
                        g.setColor(Color.RED);
                        for (int i = 0; i < stops.size(); i++) {
                            Node node = stops.get(i);
                            GeoPosition point = new GeoPosition(node.getLat(), node.getLon());
                            Point2D pointMap = map.convertGeoPositionToPoint(point);
                            Ellipse2D.Double circle = new Ellipse2D.Double(pointMap.getX(), pointMap.getY(), 10, 10);
                            g.fill(circle);
                        }
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
     * Creates and draws start and end points on the map.
     *
     * @param g   The graphics context used for drawing.
     * @param map The map on which the points will be drawn.
     */
    private static void createStartAndEndPoints(Graphics2D g, JXMapViewer map) {
        GeoPosition startPos = new GeoPosition(startLatitude, startLongitude);
        GeoPosition endPos = new GeoPosition(endLatitude, endLongitude);

        Image PointerImage = returnImage();

        g = (Graphics2D) g.create();
        Point2D start = map.convertGeoPositionToPoint(startPos);
        Point2D end = map.convertGeoPositionToPoint(endPos);

        assert PointerImage != null;
        int imgX = PointerImage.getWidth(null);
        int imgY = PointerImage.getHeight(null);

        g.drawImage(PointerImage, (int) start.getX() - imgX / 2, (int) start.getY() - imgY, null);
        g.drawImage(PointerImage, (int) end.getX() - imgX / 2, (int) end.getY() - imgY, null);

    }

    /**
     * Creates and returns an image for the start and end points.
     *
     * @return The image for the start and end points.
     */
    public static Image returnImage() {
        Image pointerImage;
        try {
            URL imageUrl = createMap.class.getResource("/Images/pointer.png");
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
