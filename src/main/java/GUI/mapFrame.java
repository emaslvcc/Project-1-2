package GUI;

import DataManagers.LogicManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalTime;
import java.util.ArrayList;
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
    private static JComboBox<String> startHour;
    private static JComboBox<String> startMinute;

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
        startHour = new javax.swing.JComboBox<>();
        startMinute = new javax.swing.JComboBox<>();

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
        modeBox.setModel(new DefaultComboBoxModel<>(new String[]{"Walk", "Bike", "Bus"}));
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

        String[] hours = new String[22];
        for (int i = 0; i < 22; i++) {
            hours[i] = String.format("%02d", i);
        }

        String[] minutes = new String[60];
        for (int i = 0; i <= 59; i++) {
            minutes[i] = String.format("%02d", i);
        }

        startHour.setOpaque(false);
        startHour.setBackground(new Color(170, 211, 223));
        startHour.setFont(new Font("Segoe UI", 1, 12)); // NOI18N
        startHour.setForeground(new Color(255, 255, 255));
        startHour.setModel(new DefaultComboBoxModel<>(hours));
        startHour.setCursor(new Cursor(Cursor.HAND_CURSOR));

        startMinute.setOpaque(false);
        startMinute.setBackground(new Color(170, 211, 223));
        startMinute.setFont(new Font("Segoe UI", 1, 12)); // NOI18N
        startMinute.setForeground(new Color(255, 255, 255));
        startMinute.setModel(new DefaultComboBoxModel<>(minutes));
        startMinute.setCursor(new Cursor(Cursor.HAND_CURSOR));

        int currentHour = LocalTime.now().getHour();
        String currentHourString = String.format("%02d", currentHour);
        startHour.setSelectedItem(currentHourString);

        int currentMinute = LocalTime.now().getMinute();
        String currentMinuteString = String.format("%02d", currentMinute);
        startMinute.setSelectedItem(currentMinuteString);

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

        javax.swing.GroupLayout backPanelLayout = new javax.swing.GroupLayout(backPanel);
        backPanel.setLayout(backPanelLayout);
        backPanelLayout.setHorizontalGroup(
                backPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(backPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(mapPanel,
                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                        Short.MAX_VALUE)
                                .addContainerGap())
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, backPanelLayout
                                .createSequentialGroup()
                                .addGroup(backPanelLayout.createParallelGroup(
                                                javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(backPanelLayout
                                                .createSequentialGroup()
                                                .addGap(135, 135, 135)
                                                .addGroup(backPanelLayout
                                                        .createParallelGroup(
                                                                javax.swing.GroupLayout.Alignment.TRAILING)
                                                        .addComponent(distanceTextLabel,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                78,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(distanceNumberLabel,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                72,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addGap(154, 154, 154)
                                                .addComponent(startHour,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(18, 18, 18)
                                                .addComponent(startMinute,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(
                                                        javax.swing.LayoutStyle.ComponentPlacement.RELATED,
                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                        Short.MAX_VALUE))
                                        .addGroup(backPanelLayout
                                                .createSequentialGroup()
                                                .addGroup(backPanelLayout
                                                        .createParallelGroup(
                                                                javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addGroup(backPanelLayout
                                                                .createSequentialGroup()
                                                                .addGap(41, 41, 41)
                                                                .addComponent(startCodeField,
                                                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                        201,
                                                                        javax.swing.GroupLayout.PREFERRED_SIZE))
                                                        .addGroup(backPanelLayout
                                                                .createSequentialGroup()
                                                                .addGap(93, 93, 93)
                                                                .addComponent(startCodeLabel)))
                                                .addGroup(backPanelLayout
                                                        .createParallelGroup(
                                                                javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addGroup(backPanelLayout
                                                                .createSequentialGroup()
                                                                .addGap(169, 169,
                                                                        169)
                                                                .addComponent(modeBox,
                                                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                        javax.swing.GroupLayout.PREFERRED_SIZE))
                                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
                                                                backPanelLayout.createSequentialGroup()
                                                                        .addPreferredGap(
                                                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED,
                                                                                116,
                                                                                Short.MAX_VALUE)
                                                                        .addComponent(calculateButton,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                173,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                        .addGap(121, 121,
                                                                                121)))))
                                .addGroup(backPanelLayout.createParallelGroup(
                                                javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
                                                backPanelLayout.createSequentialGroup()
                                                        .addGroup(backPanelLayout
                                                                .createParallelGroup(
                                                                        javax.swing.GroupLayout.Alignment.LEADING)
                                                                .addComponent(timeTextLabel,
                                                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                        78,
                                                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addComponent(timeNumberLabel,
                                                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                        72,
                                                                        javax.swing.GroupLayout.PREFERRED_SIZE))
                                                        .addGap(154, 154,
                                                                154))
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
                                                backPanelLayout.createSequentialGroup()
                                                        .addComponent(destinationCodeLabel)
                                                        .addGap(42, 42, 42)
                                                        .addComponent(minimizeButton)
                                                        .addPreferredGap(
                                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                        .addComponent(exitButton)
                                                        .addGap(15, 15, 15))
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
                                                backPanelLayout.createSequentialGroup()
                                                        .addComponent(destinationCodeField,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                197,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addGap(49, 49, 49)))));
        backPanelLayout.setVerticalGroup(
                backPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(backPanelLayout.createSequentialGroup()
                                .addGroup(backPanelLayout.createParallelGroup(
                                                javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addGroup(backPanelLayout
                                                .createSequentialGroup()
                                                .addGroup(backPanelLayout
                                                        .createParallelGroup(
                                                                javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addGroup(backPanelLayout
                                                                .createParallelGroup(
                                                                        javax.swing.GroupLayout.Alignment.LEADING)
                                                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
                                                                        backPanelLayout.createSequentialGroup()
                                                                                .addGap(17, 17, 17)
                                                                                .addComponent(startCodeLabel))
                                                                .addGroup(backPanelLayout
                                                                        .createSequentialGroup()
                                                                        .addContainerGap()
                                                                        .addGroup(backPanelLayout
                                                                                .createParallelGroup(
                                                                                        javax.swing.GroupLayout.Alignment.BASELINE)
                                                                                .addComponent(exitButton)
                                                                                .addComponent(minimizeButton))))
                                                        .addComponent(destinationCodeLabel,
                                                                javax.swing.GroupLayout.Alignment.TRAILING))
                                                .addPreferredGap(
                                                        javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(backPanelLayout
                                                        .createParallelGroup(
                                                                javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(destinationCodeField,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(startCodeField,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addGap(18, 18, 18))
                                        .addGroup(backPanelLayout
                                                .createSequentialGroup()
                                                .addComponent(calculateButton)
                                                .addPreferredGap(
                                                        javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(modeBox,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(8, 8, 8)))
                                .addComponent(mapPanel,
                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(backPanelLayout.createParallelGroup(
                                                javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(backPanelLayout
                                                .createSequentialGroup()
                                                .addPreferredGap(
                                                        javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addGroup(backPanelLayout
                                                        .createParallelGroup(
                                                                javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(timeTextLabel,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                14,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(distanceTextLabel,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                14,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addPreferredGap(
                                                        javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(backPanelLayout
                                                        .createParallelGroup(
                                                                javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(timeNumberLabel)
                                                        .addComponent(distanceNumberLabel)))
                                        .addGroup(backPanelLayout
                                                .createSequentialGroup()
                                                .addGap(18, 18, 18)
                                                .addGroup(backPanelLayout
                                                        .createParallelGroup(
                                                                javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(startMinute,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(startHour,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE))))
                                .addGap(15, 15, 15)));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(backPanel, javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(backPanel, javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));

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

        if (busMode) {
            transferModule.clearTransfers();
        }
        if (Objects.requireNonNull(modeBox.getSelectedItem()).toString().equals("Bus") && !busMode) {
            createMap.clearMapRoute();
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
        logicManager.calculateLogic(startCodeField, destinationCodeField, modeBox, startHour, startMinute);
        Calculators.TimeCalculator.hour = (String) startHour.getSelectedItem();
        Calculators.TimeCalculator.minute = (String) startMinute.getSelectedItem();

        if (busMode) {
            showBusInfo(transferModule.getTransfers());
        }

    }

    /**
     * Adds a panel for displaying bus information to the map frame.
     * The information that will be displayed on this Panel will be the bus transfers
     * that the user will have to take. (both walking and taking the bus)
     *
     * @param frame The action listener for the frame.
     */
    private void addPanelForBusInfo(ActionListener frame) {
        this.setSize(new Dimension(1210, 598)); // Adjust the size of the frame as well
        this.setLayout(new BorderLayout());

        busInfoPanel.setLayout(new BoxLayout(busInfoPanel, BoxLayout.Y_AXIS));
        busInfoPanel.setBackground(new Color(244, 244, 244));
        busInfoPanel.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0)));

        JScrollPane scrollPane = new JScrollPane(busInfoPanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
        verticalScrollBar.setUnitIncrement(16);
        verticalScrollBar.setBlockIncrement(100);

        this.add(scrollPane, BorderLayout.EAST);
        this.revalidate();
        this.repaint();
        recenterWindow();

    }

    /**
     * This method adds the transferModules to the busInfoPanel.
     *
     * @param transfers The list of transferModules to be displayed.
     */
    public void showBusInfo(ArrayList<transferModule> transfers) {
        busInfoPanel.removeAll(); // Clear previous components

        int totalHeight = 0;
        int strutHeight = 10; // Height of the vertical strut

        for (int i = 0; i < transfers.size(); i++) {
            JPanel transferPanel = transfers.get(i).getTransferPanel();
            busInfoPanel.add(transferPanel);

            totalHeight += transferPanel.getPreferredSize().height; // Update total height

            if (transfers.get(i).getMode().equals("Bus")
                    && (i + 1 < transfers.size() && transfers.get(i + 1).getMode().equals("Bus"))) {
                totalHeight += 30; // Extra space between consecutive bus modules
            }

            // Add vertical strut if not the last transfer
            if (i < transfers.size() - 1) {
                transferPanel.setSize(new Dimension(308,
                        transferPanel.getPreferredSize().height + strutHeight));
                busInfoPanel.add(Box.createVerticalStrut(strutHeight));
                totalHeight += strutHeight; // Update total height
            }
        }

        // Set the preferred size of busInfoPanel to accommodate all transfer panels
        busInfoPanel.setPreferredSize(new Dimension(308, totalHeight));
        busInfoPanel.revalidate();
        busInfoPanel.repaint();
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
