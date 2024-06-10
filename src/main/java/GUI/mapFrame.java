package GUI;

import DataManagers.LogicManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Objects;

public class mapFrame extends JFrame {
        private JPanel backPanel;
        private JButton calculateButton;
        private JTextField destinationCodeField;
        private JLabel destinationCodeLabel;
        private static JLabel distanceNumberLabel;
        private JLabel distanceTextLabel;
        private JLabel exitButton;
        private JPanel mapPanel;
        private JLabel minimizeButton;
        private JComboBox<String> modeBox;
        private JTextField startCodeField;
        private JLabel startCodeLabel;
        private static JLabel timeNumberLabel;
        private JLabel timeTextLabel;
        private static JPanel busInfoPanel;
        private static JLabel busName;
        private static JLabel busNum;
        private static JLabel departureTime;
        private static JLabel arrivalTime;
        private static JLabel startBusStop;
        private static JLabel endBusStop;

        /**
         * Calls the component initializer.
         */
        public mapFrame() {
                initComponents();
        }

        /**
         * Initializes all the GUI components.
         */
        private void initComponents() {
                setIconImage(new ImageIcon(getClass().getResource("/Images/mapLogo.png")).getImage());
                backPanel = new JPanel();
                calculateButton = new JButton();
                exitButton = new JLabel();
                minimizeButton = new JLabel();
                mapPanel = new JPanel();
                modeBox = new JComboBox<>();
                startCodeLabel = new JLabel();
                distanceNumberLabel = new JLabel();
                distanceTextLabel = new JLabel();
                destinationCodeLabel = new JLabel();
                startCodeField = new JTextField();
                destinationCodeField = new JTextField();
                timeTextLabel = new JLabel();
                timeNumberLabel = new JLabel();
                busInfoPanel = new JPanel();

                setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                setName("mapFrame");
                setUndecorated(true);
                setResizable(false);

                backPanel.setBackground(new Color(244, 244, 244));
                backPanel.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0)));

                calculateButton.setBackground(new Color(255, 255, 255));
                calculateButton.setFont(new Font("Segoe UI", 1, 14)); // NOI18N
                calculateButton.setForeground(new Color(0, 0, 0));
                calculateButton.setText("Calculate");
                calculateButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
                calculateButton.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent evt) {
                                try {
                                        calculateButtonActionPerformed(evt, this);
                                } catch (Exception e) {

                                        e.printStackTrace();
                                }
                        }
                });

                exitButton.setFont(new Font("Segoe UI", 1, 14)); // NOI18N
                exitButton.setForeground(new Color(131, 162, 172));
                exitButton.setHorizontalAlignment(SwingConstants.CENTER);
                exitButton.setText("X");
                exitButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
                exitButton.addMouseListener(new java.awt.event.MouseAdapter() {
                        public void mouseClicked(java.awt.event.MouseEvent evt) {
                                exitButtonMouseClicked(evt);
                        }
                });

                minimizeButton.setFont(new Font("Segoe UI", 1, 14)); // NOI18N
                minimizeButton.setForeground(new Color(131, 162, 172));
                minimizeButton.setHorizontalAlignment(SwingConstants.CENTER);
                minimizeButton.setText("-");
                minimizeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
                minimizeButton.addMouseListener(new java.awt.event.MouseAdapter() {
                        public void mouseClicked(java.awt.event.MouseEvent evt) {
                                minimizeButtonMouseClicked(evt);
                        }
                });

                mapPanel.setBackground(new Color(0, 0, 0));

                GroupLayout mapPanelLayout = new GroupLayout(mapPanel);
                mapPanel.setLayout(mapPanelLayout);
                mapPanelLayout.setHorizontalGroup(
                                mapPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                .addGap(0, 0, Short.MAX_VALUE));
                mapPanelLayout.setVerticalGroup(
                                mapPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                .addGap(0, 454, Short.MAX_VALUE));
                mapPanel = createMap.createMapPanel();

                modeBox.setOpaque(false);
                modeBox.setBackground(new Color(170, 211, 223));
                modeBox.setFont(new Font("Segoe UI", 1, 12)); // NOI18N
                modeBox.setForeground(new Color(255, 255, 255));
                modeBox.setModel(new DefaultComboBoxModel<>(new String[] { "Walk", "Bike", "Bus" }));
                modeBox.setCursor(new Cursor(Cursor.HAND_CURSOR));

                startCodeLabel.setFont(new Font("Segoe UI", 1, 14)); // NOI18N
                startCodeLabel.setForeground(new Color(0, 0, 0));
                startCodeLabel.setHorizontalAlignment(SwingConstants.CENTER);
                startCodeLabel.setText("Start Zipcode:");

                distanceNumberLabel.setFont(new Font("Segoe UI", 1, 14)); // NOI18N
                distanceNumberLabel.setForeground(new Color(0, 0, 0));
                distanceNumberLabel.setHorizontalAlignment(SwingConstants.CENTER);
                distanceNumberLabel.setText("0 m");

                distanceTextLabel.setFont(new Font("Segoe UI", 1, 14)); // NOI18N
                distanceTextLabel.setForeground(new Color(0, 0, 0));
                distanceTextLabel.setHorizontalAlignment(SwingConstants.CENTER);
                distanceTextLabel.setText("Distance:");

                destinationCodeLabel.setFont(new Font("Segoe UI", 1, 14)); // NOI18N
                destinationCodeLabel.setForeground(new Color(0, 0, 0));
                destinationCodeLabel.setHorizontalAlignment(SwingConstants.CENTER);
                destinationCodeLabel.setText("Destination Zipcode:");

                startCodeField.setForeground(new Color(0, 0, 0));

                destinationCodeField.setForeground(new Color(0, 0, 0));

                timeTextLabel.setFont(new Font("Segoe UI", 1, 14)); // NOI18N
                timeTextLabel.setForeground(new Color(0, 0, 0));
                timeTextLabel.setHorizontalAlignment(SwingConstants.CENTER);
                timeTextLabel.setText("Time:");

                timeNumberLabel.setFont(new Font("Segoe UI", 1, 14)); // NOI18N
                timeNumberLabel.setForeground(new Color(0, 0, 0));
                timeNumberLabel.setHorizontalAlignment(SwingConstants.CENTER);
                timeNumberLabel.setText("0 min");

                GroupLayout backPanelLayout = new GroupLayout(backPanel);
                backPanel.setLayout(backPanelLayout);
                backPanelLayout.setHorizontalGroup(
                                backPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                .addGroup(backPanelLayout.createSequentialGroup()
                                                                .addContainerGap()
                                                                .addComponent(mapPanel,
                                                                                GroupLayout.DEFAULT_SIZE,
                                                                                GroupLayout.DEFAULT_SIZE,
                                                                                Short.MAX_VALUE)
                                                                .addContainerGap())
                                                .addGroup(GroupLayout.Alignment.TRAILING, backPanelLayout
                                                                .createSequentialGroup()
                                                                .addGroup(backPanelLayout.createParallelGroup(
                                                                                GroupLayout.Alignment.LEADING)
                                                                                .addGroup(backPanelLayout
                                                                                                .createSequentialGroup()
                                                                                                .addGap(135, 135, 135)
                                                                                                .addGroup(backPanelLayout
                                                                                                                .createParallelGroup(
                                                                                                                                GroupLayout.Alignment.TRAILING)
                                                                                                                .addComponent(distanceTextLabel,
                                                                                                                                GroupLayout.PREFERRED_SIZE,
                                                                                                                                78,
                                                                                                                                GroupLayout.PREFERRED_SIZE)
                                                                                                                .addComponent(distanceNumberLabel,
                                                                                                                                GroupLayout.PREFERRED_SIZE,
                                                                                                                                72,
                                                                                                                                GroupLayout.PREFERRED_SIZE))
                                                                                                .addPreferredGap(
                                                                                                                LayoutStyle.ComponentPlacement.RELATED,
                                                                                                                GroupLayout.DEFAULT_SIZE,
                                                                                                                Short.MAX_VALUE))
                                                                                .addGroup(backPanelLayout
                                                                                                .createSequentialGroup()
                                                                                                .addGroup(backPanelLayout
                                                                                                                .createParallelGroup(
                                                                                                                                GroupLayout.Alignment.LEADING)
                                                                                                                .addGroup(backPanelLayout
                                                                                                                                .createSequentialGroup()
                                                                                                                                .addGap(41, 41, 41)
                                                                                                                                .addComponent(startCodeField,
                                                                                                                                                GroupLayout.PREFERRED_SIZE,
                                                                                                                                                201,
                                                                                                                                                GroupLayout.PREFERRED_SIZE))
                                                                                                                .addGroup(backPanelLayout
                                                                                                                                .createSequentialGroup()
                                                                                                                                .addGap(93, 93, 93)
                                                                                                                                .addComponent(startCodeLabel)))
                                                                                                .addGroup(backPanelLayout
                                                                                                                .createParallelGroup(
                                                                                                                                GroupLayout.Alignment.LEADING)
                                                                                                                .addGroup(backPanelLayout
                                                                                                                                .createSequentialGroup()
                                                                                                                                .addGap(169, 169,
                                                                                                                                                169)
                                                                                                                                .addComponent(modeBox,
                                                                                                                                                GroupLayout.PREFERRED_SIZE,
                                                                                                                                                GroupLayout.DEFAULT_SIZE,
                                                                                                                                                GroupLayout.PREFERRED_SIZE))
                                                                                                                .addGroup(GroupLayout.Alignment.TRAILING,
                                                                                                                                backPanelLayout.createSequentialGroup()
                                                                                                                                                .addPreferredGap(
                                                                                                                                                                LayoutStyle.ComponentPlacement.RELATED,
                                                                                                                                                                116,
                                                                                                                                                                Short.MAX_VALUE)
                                                                                                                                                .addComponent(calculateButton,
                                                                                                                                                                GroupLayout.PREFERRED_SIZE,
                                                                                                                                                                173,
                                                                                                                                                                GroupLayout.PREFERRED_SIZE)
                                                                                                                                                .addGap(121, 121,
                                                                                                                                                                121)))))
                                                                .addGroup(backPanelLayout.createParallelGroup(
                                                                                GroupLayout.Alignment.LEADING)
                                                                                .addGroup(GroupLayout.Alignment.TRAILING,
                                                                                                backPanelLayout
                                                                                                                .createSequentialGroup()
                                                                                                                .addGroup(backPanelLayout
                                                                                                                                .createParallelGroup(
                                                                                                                                                GroupLayout.Alignment.LEADING)
                                                                                                                                .addComponent(timeTextLabel,
                                                                                                                                                GroupLayout.PREFERRED_SIZE,
                                                                                                                                                78,
                                                                                                                                                GroupLayout.PREFERRED_SIZE)
                                                                                                                                .addComponent(timeNumberLabel,
                                                                                                                                                GroupLayout.PREFERRED_SIZE,
                                                                                                                                                72,
                                                                                                                                                GroupLayout.PREFERRED_SIZE))
                                                                                                                .addGap(154, 154,
                                                                                                                                154))
                                                                                .addGroup(GroupLayout.Alignment.TRAILING,
                                                                                                backPanelLayout.createSequentialGroup()
                                                                                                                .addComponent(destinationCodeLabel)
                                                                                                                .addGap(42, 42, 42)
                                                                                                                .addComponent(minimizeButton)
                                                                                                                .addPreferredGap(
                                                                                                                                LayoutStyle.ComponentPlacement.RELATED)
                                                                                                                .addComponent(exitButton)
                                                                                                                .addGap(15, 15, 15))
                                                                                .addGroup(GroupLayout.Alignment.TRAILING,
                                                                                                backPanelLayout.createSequentialGroup()
                                                                                                                .addComponent(destinationCodeField,
                                                                                                                                GroupLayout.PREFERRED_SIZE,
                                                                                                                                197,
                                                                                                                                GroupLayout.PREFERRED_SIZE)
                                                                                                                .addGap(49, 49, 49)))));
                backPanelLayout.setVerticalGroup(
                                backPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                .addGroup(backPanelLayout.createSequentialGroup()
                                                                .addGroup(backPanelLayout
                                                                                .createParallelGroup(
                                                                                                GroupLayout.Alignment.TRAILING)
                                                                                .addGroup(backPanelLayout
                                                                                                .createSequentialGroup()
                                                                                                .addGroup(backPanelLayout
                                                                                                                .createParallelGroup(
                                                                                                                                GroupLayout.Alignment.LEADING)
                                                                                                                .addGroup(backPanelLayout
                                                                                                                                .createParallelGroup(
                                                                                                                                                GroupLayout.Alignment.LEADING)
                                                                                                                                .addGroup(GroupLayout.Alignment.TRAILING,
                                                                                                                                                backPanelLayout.createSequentialGroup()
                                                                                                                                                                .addGap(17, 17, 17)
                                                                                                                                                                .addComponent(startCodeLabel))
                                                                                                                                .addGroup(backPanelLayout
                                                                                                                                                .createSequentialGroup()
                                                                                                                                                .addContainerGap()
                                                                                                                                                .addGroup(backPanelLayout
                                                                                                                                                                .createParallelGroup(
                                                                                                                                                                                GroupLayout.Alignment.BASELINE)
                                                                                                                                                                .addComponent(exitButton)
                                                                                                                                                                .addComponent(minimizeButton))))
                                                                                                                .addComponent(destinationCodeLabel,
                                                                                                                                GroupLayout.Alignment.TRAILING))
                                                                                                .addPreferredGap(
                                                                                                                LayoutStyle.ComponentPlacement.RELATED)
                                                                                                .addGroup(backPanelLayout
                                                                                                                .createParallelGroup(
                                                                                                                                GroupLayout.Alignment.BASELINE)
                                                                                                                .addComponent(destinationCodeField,
                                                                                                                                GroupLayout.PREFERRED_SIZE,
                                                                                                                                GroupLayout.DEFAULT_SIZE,
                                                                                                                                GroupLayout.PREFERRED_SIZE)
                                                                                                                .addComponent(startCodeField,
                                                                                                                                GroupLayout.PREFERRED_SIZE,
                                                                                                                                GroupLayout.DEFAULT_SIZE,
                                                                                                                                GroupLayout.PREFERRED_SIZE))
                                                                                                .addGap(18, 18, 18))
                                                                                .addGroup(backPanelLayout
                                                                                                .createSequentialGroup()
                                                                                                .addComponent(calculateButton)
                                                                                                .addPreferredGap(
                                                                                                                LayoutStyle.ComponentPlacement.RELATED)
                                                                                                .addComponent(modeBox,
                                                                                                                GroupLayout.PREFERRED_SIZE,
                                                                                                                GroupLayout.DEFAULT_SIZE,
                                                                                                                GroupLayout.PREFERRED_SIZE)
                                                                                                .addGap(8, 8, 8)))
                                                                .addComponent(mapPanel,
                                                                                GroupLayout.PREFERRED_SIZE,
                                                                                GroupLayout.DEFAULT_SIZE,
                                                                                GroupLayout.PREFERRED_SIZE)
                                                                .addPreferredGap(
                                                                                LayoutStyle.ComponentPlacement.UNRELATED)
                                                                .addGroup(
                                                                                backPanelLayout.createParallelGroup(
                                                                                                GroupLayout.Alignment.BASELINE)
                                                                                                .addComponent(timeTextLabel,
                                                                                                                GroupLayout.PREFERRED_SIZE,
                                                                                                                14,
                                                                                                                GroupLayout.PREFERRED_SIZE)
                                                                                                .addComponent(distanceTextLabel,
                                                                                                                GroupLayout.PREFERRED_SIZE,
                                                                                                                14,
                                                                                                                GroupLayout.PREFERRED_SIZE))
                                                                .addPreferredGap(
                                                                                LayoutStyle.ComponentPlacement.RELATED)
                                                                .addGroup(
                                                                                backPanelLayout.createParallelGroup(
                                                                                                GroupLayout.Alignment.BASELINE)
                                                                                                .addComponent(timeNumberLabel)
                                                                                                .addComponent(distanceNumberLabel))
                                                                .addGap(15, 15, 15)));

                GroupLayout layout = new GroupLayout(getContentPane());
                getContentPane().setLayout(layout);
                layout.setHorizontalGroup(
                                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                .addComponent(backPanel, GroupLayout.DEFAULT_SIZE,
                                                                GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
                layout.setVerticalGroup(
                                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                .addComponent(backPanel, GroupLayout.DEFAULT_SIZE,
                                                                GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));

                setSize(new Dimension(900, 598));
                setLocationRelativeTo(null);
        }

        private boolean busMode = false;

        /**
         * Handles the action when the calculate button is pressed.
         *
         * @param evt   The action event.
         * @param frame The action listener for the frame.
         * @throws Exception Exception if an error occurs during calculation.
         */
        private void calculateButtonActionPerformed(ActionEvent evt, ActionListener frame) throws Exception {
                if (Objects.requireNonNull(modeBox.getSelectedItem()).toString().equals("Bus") && !busMode) {
                        createMap.clearMap();
                        addPanelForBusInfo(frame);
                        busMode = true;
                } else if (!Objects.requireNonNull(modeBox.getSelectedItem()).toString().equals("Bus") && busMode) {
                        removePanelForBusInfo(frame);
                        busMode = false;
                }
                Bus.BusConnectionDev.resetLists();

                if (startCodeField.getText().isEmpty() || destinationCodeField.getText().isEmpty()) {
                        JOptionPane.showMessageDialog(null, "Please fill in both fields.");
                        return;
                }
                LogicManager logicManager = new LogicManager();
                logicManager.calculateLogic(startCodeField, destinationCodeField, modeBox);

        }

        /**
         * Updates the time field on the map frame.
         *
         * @param time The time value to be displayed.
         */
        public static void updateTimeField(int time) {
                timeNumberLabel.setText(time + " min");
        }

        /**
         * Updates the distance field on the map frame.
         *
         * @param distance The distance value to be displayed.
         */
        public static void updateDistanceField(double distance) {
                String displayed = String.format("%.2f", distance);
                distanceNumberLabel.setText(displayed + " km");
        }

        /**
         * Handles the action when the exit button is clicked.
         *
         * @param evt The mouse event.
         */
        private void exitButtonMouseClicked(java.awt.event.MouseEvent evt) {
                System.exit(0);
        }

        /**
         * Handles the action when the minimize button is clicked.
         *
         * @param evt The mouse event.
         */
        private void minimizeButtonMouseClicked(java.awt.event.MouseEvent evt) {
                this.setExtendedState(mapFrame.ICONIFIED);
        }

        /**
         * Adds a panel for displaying bus information to the map frame.
         *
         * @param frame The action listener for the frame.
         */
        private void addPanelForBusInfo(ActionListener frame) {
                this.setSize(new Dimension(1100, 598));
                this.setLayout(new BorderLayout());

                busInfoPanel.setPreferredSize(new Dimension(200, 598));
                busInfoPanel.setBackground(new Color(244, 244, 244)); // Set the background color to white
                busInfoPanel.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0)));
                busInfoPanel.setLayout(new BoxLayout(busInfoPanel, BoxLayout.Y_AXIS));

                busName = new JLabel("Bus Name: ");
                busName.setFont(new Font("Segoe UI", 1, 14));
                busName.setForeground(new Color(0, 0, 0));

                busNum = new JLabel("Bus Number: ");
                busNum.setFont(new Font("Segoe UI", 1, 14));
                busNum.setForeground(new Color(0, 0, 0));

                startBusStop = new JLabel("Start Bus Stop: ");
                startBusStop.setFont(new Font("Segoe UI", 1, 14));
                startBusStop.setForeground(new Color(0, 0, 0));

                endBusStop = new JLabel("End Bus Stop: ");
                endBusStop.setFont(new Font("Segoe UI", 1, 14));
                endBusStop.setForeground(new Color(0, 0, 0));

                arrivalTime = new JLabel("Arrival Time: ");
                arrivalTime.setFont(new Font("Segoe UI", 1, 14));
                arrivalTime.setForeground(new Color(0, 0, 0));

                departureTime = new JLabel("Departure Time: ");
                departureTime.setFont(new Font("Segoe UI", 1, 14));
                departureTime.setForeground(new Color(0, 0, 0));

                busName.setAlignmentX(Component.LEFT_ALIGNMENT);
                busNum.setAlignmentX(Component.LEFT_ALIGNMENT);
                startBusStop.setAlignmentX(Component.LEFT_ALIGNMENT);
                endBusStop.setAlignmentX(Component.LEFT_ALIGNMENT);
                arrivalTime.setAlignmentX(Component.LEFT_ALIGNMENT);
                departureTime.setAlignmentX(Component.LEFT_ALIGNMENT);

                busInfoPanel.add(new Box.Filler(new Dimension(0, 0), new Dimension(0, 0),
                                new Dimension(0, Integer.MAX_VALUE)));
                busInfoPanel.add(Box.createHorizontalStrut(20));

                busInfoPanel.add(busName);
                busInfoPanel.add(Box.createVerticalStrut(10)); // Add space
                busInfoPanel.add(busNum);
                busInfoPanel.add(Box.createVerticalStrut(100)); // Add space
                busInfoPanel.add(startBusStop);
                busInfoPanel.add(Box.createVerticalStrut(10)); // Add space
                busInfoPanel.add(endBusStop);
                busInfoPanel.add(Box.createVerticalStrut(100)); // Add space
                busInfoPanel.add(arrivalTime);
                busInfoPanel.add(Box.createVerticalStrut(10)); // Add space
                busInfoPanel.add(departureTime);

                busInfoPanel.add(new Box.Filler(new Dimension(0, 0), new Dimension(0, 0),
                                new Dimension(0, Integer.MAX_VALUE)));

                this.add(busInfoPanel, BorderLayout.EAST);
                this.revalidate();
                this.repaint();
                recenterWindow();
        }

        /**
         * Sets the bus information on the map frame.
         *
         * @param busName       The name of the bus.
         * @param busNum        The number of the bus.
         * @param startBusStop  The starting bus stop.
         * @param endBusStop    The ending bus stop.
         * @param arrivalTime   The arrival time of the bus.
         * @param departureTime The departure time of the bus.
         */
        public static void setBusInfo(String busName, String busNum, String startBusStop, String endBusStop,
                        String arrivalTime, String departureTime) {
                mapFrame.busName.setText("<html>Bus Name:<br>" + busName + "</html>");
                mapFrame.busNum.setText("<html>Bus Number:<br>" + busNum + "</html>");
                mapFrame.startBusStop.setText("<html>Start Bus Stop:<br>" + startBusStop + "</html>");
                mapFrame.endBusStop.setText("<html>End Bus Stop:<br>" + endBusStop + "</html>");
                mapFrame.arrivalTime.setText("<html>Arrival Time:<br>" + arrivalTime + "</html>");
                mapFrame.departureTime.setText("<html>Departure Time:<br>" + departureTime + "</html>");
        }

        /**
         * Removes the panel for displaying bus information from the map frame.
         *
         * @param frame The action listener for the frame.
         */
        private void removePanelForBusInfo(ActionListener frame) {
                this.setSize(new Dimension(900, 598));
                busInfoPanel.removeAll();
                this.remove(busInfoPanel);
                this.revalidate();
                this.repaint();
                recenterWindow();
        }

        /**
         * Recenter the window on the screen.
         */
        public void recenterWindow() {
                Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
                this.setLocation(dim.width / 2 - this.getSize().width / 2, dim.height / 2 - this.getSize().height / 2);
        }

        /**
         * Main method that starts the application.
         * 
         * @param args the command line arguments.
         */
        public static void main(String args[]) {
                LogicManager.createGraph();
                try {
                        for (UIManager.LookAndFeelInfo info : UIManager
                                        .getInstalledLookAndFeels()) {
                                if ("Nimbus".equals(info.getName())) {
                                        UIManager.setLookAndFeel(info.getClassName());
                                        break;
                                }
                        }
                } catch (ClassNotFoundException ex) {
                        java.util.logging.Logger.getLogger(mapFrame.class.getName()).log(java.util.logging.Level.SEVERE,
                                        null, ex);
                } catch (InstantiationException ex) {
                        java.util.logging.Logger.getLogger(mapFrame.class.getName()).log(java.util.logging.Level.SEVERE,
                                        null, ex);
                } catch (IllegalAccessException ex) {
                        java.util.logging.Logger.getLogger(mapFrame.class.getName()).log(java.util.logging.Level.SEVERE,
                                        null, ex);
                } catch (UnsupportedLookAndFeelException ex) {
                        java.util.logging.Logger.getLogger(mapFrame.class.getName()).log(java.util.logging.Level.SEVERE,
                                        null, ex);
                }

                EventQueue.invokeLater(new Runnable() {
                        public void run() {
                                new mapFrame().setVisible(true);
                        }
                });
        }
}
