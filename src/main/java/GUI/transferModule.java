package GUI;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class transferModule {
    private String mode;
    private String startTime;
    private String endTime;
    private String busNum;
    private String startBusStop;
    private String endBusStop;
    private static ArrayList<transferModule> transfers = new ArrayList<>();

    public static void addTransferModule(String mode, String startTime, String endTime, String busNumber,
            String startBusStop, String endBusStop) {
        transferModule newTransfer = new transferModule(mode, startTime, endTime, busNumber, startBusStop, endBusStop);
        transfers.add(newTransfer);
    }

    public static void addTransferModule(String mode, String startTime, String endTime) {
        transferModule newTransfer = new transferModule(mode, startTime, endTime);
        transfers.add(newTransfer);
    }

    public static ArrayList<transferModule> getTransfers() {
        return transfers;
    }

    public static void clearTransfers() {
        transfers.clear();
    }

    public transferModule(String mode, String startTime, String endTime) {
        this.mode = mode;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public transferModule(String mode, String startTime, String endTime, String busNum,
            String startBusStop, String endBusStop) {
        this.mode = mode;
        this.startTime = startTime;
        this.endTime = endTime;
        this.busNum = busNum;

        this.startBusStop = startBusStop;
        this.endBusStop = endBusStop;
    }

    public String getMode() {
        return mode;
    }

    public JPanel getTransferPanel() {

        JPanel transferPanel = new JPanel();
        transferPanel.setLayout(new BoxLayout(transferPanel, BoxLayout.X_AXIS));
        transferPanel.setPreferredSize(new Dimension(308, 100));
        transferPanel.setMaximumSize(new Dimension(308, 200));
        transferPanel.setBackground(new Color(244, 244, 244));

        JPanel labelsPanel = new JPanel();
        labelsPanel.setLayout(new BoxLayout(labelsPanel, BoxLayout.Y_AXIS));
        labelsPanel.setPreferredSize(new Dimension(248, 100));
        labelsPanel.setBackground(new Color(244, 244, 244));

        JLabel startTimeLabel = new JLabel("Start Time: " + startTime);
        startTimeLabel.setFont(new Font("Segoe UI", 1, 14));
        startTimeLabel.setForeground(new Color(0, 0, 0));

        JLabel endTimeLabel = new JLabel("End Time: " + endTime);
        endTimeLabel.setFont(new Font("Segoe UI", 1, 14));
        endTimeLabel.setForeground(new Color(0, 0, 0));

        labelsPanel.add(startTimeLabel);

        Image transportIcon = null;

        if (mode.equals("Bus")) {
            JLabel busNumLabel = new JLabel("Bus Number: " + busNum);
            busNumLabel.setFont(new Font("Segoe UI", 1, 14));
            busNumLabel.setForeground(new Color(0, 0, 0));

            JLabel startBusStopLabel = new JLabel("<html>Start Bus Stop: " + startBusStop + "</html>");
            startBusStopLabel.setFont(new Font("Segoe UI", 1, 14));
            startBusStopLabel.setForeground(new Color(0, 0, 0));

            JLabel endBusStopLabel = new JLabel("End Bus Stop: " + endBusStop);
            endBusStopLabel.setFont(new Font("Segoe UI", 1, 14));
            endBusStopLabel.setForeground(new Color(0, 0, 0));

            labelsPanel.setPreferredSize(new Dimension(175, 158));

            labelsPanel.add(startBusStopLabel);
            labelsPanel.add(busNumLabel);
            labelsPanel.add(endBusStopLabel);

            try {
                URL imageUrl = createMap.class.getResource("/Images/Bus.png");
                assert imageUrl != null;
                transportIcon = ImageIO.read(imageUrl);
                transportIcon = transportIcon.getScaledInstance(28, 25, Image.SCALE_SMOOTH);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }

        } else {

            try {
                URL imageUrl = createMap.class.getResource("/Images/Walk.png");
                assert imageUrl != null;
                transportIcon = ImageIO.read(imageUrl);
                transportIcon = transportIcon.getScaledInstance(17, 25, Image.SCALE_SMOOTH);

            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        labelsPanel.add(endTimeLabel);

        transferPanel.add(Box.createHorizontalStrut(20));

        assert transportIcon != null;

        JPanel iconPanel = new JPanel();
        iconPanel.setBackground(new Color(244, 244, 244));
        iconPanel.setLayout(new BoxLayout(iconPanel, BoxLayout.Y_AXIS));

        JLabel transportLabel = new JLabel(new ImageIcon(transportIcon));
        transportLabel.setAlignmentY(Component.CENTER_ALIGNMENT);
        transportLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        transportLabel.setPreferredSize(new Dimension(30, 100));
        iconPanel.add(transportLabel);

        transferPanel.add(iconPanel);
        transferPanel.add(Box.createHorizontalStrut(15));

        transferPanel.add(labelsPanel);
        transferPanel.add(Box.createHorizontalStrut(10));

        if (mode.equals("Bus")) {
            transferPanel.setPreferredSize(new Dimension(308, 150));
            transferPanel.setMaximumSize(new Dimension(308, 900));
        }

        return transferPanel;
    }

}
