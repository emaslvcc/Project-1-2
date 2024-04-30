package GUI;

import javax.swing.*;

public class mapFrame extends javax.swing.JFrame {
    private javax.swing.JPanel backPanel;
    private javax.swing.JButton calculateButton;
    private javax.swing.JTextField destinationCodeField;
    private javax.swing.JLabel destinationCodeLabel;
    private javax.swing.JLabel distanceNumberLabel;
    private javax.swing.JLabel distanceTextLabel;
    private javax.swing.JLabel exitButton;
    private javax.swing.JPanel mapPanel;
    private javax.swing.JLabel minimizeButton;
    private javax.swing.JComboBox<String> modeBox;
    private javax.swing.JTextField startCodeField;
    private javax.swing.JLabel startCodeLabel;
    private javax.swing.JLabel timeNumberLabel;
    private javax.swing.JLabel timeTextLabel;


    public mapFrame() {
        initComponents();
    }


    private void initComponents() {
        setIconImage(new javax.swing.ImageIcon(getClass().getResource("/Images/mapLogo.png")).getImage());
        backPanel = new javax.swing.JPanel();
        calculateButton = new javax.swing.JButton();
        exitButton = new javax.swing.JLabel();
        minimizeButton = new javax.swing.JLabel();
        mapPanel = new javax.swing.JPanel();
        modeBox = new javax.swing.JComboBox<>();
        startCodeLabel = new javax.swing.JLabel();
        distanceNumberLabel = new javax.swing.JLabel();
        distanceTextLabel = new javax.swing.JLabel();
        destinationCodeLabel = new javax.swing.JLabel();
        startCodeField = new javax.swing.JTextField();
        destinationCodeField = new javax.swing.JTextField();
        timeTextLabel = new javax.swing.JLabel();
        timeNumberLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setName("mapFrame");
        setUndecorated(true);
        setResizable(false);

        backPanel.setBackground(new java.awt.Color(244, 244, 244));
        backPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        calculateButton.setBackground(new java.awt.Color(255, 255, 255));
        calculateButton.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        calculateButton.setForeground(new java.awt.Color(0, 0, 0));
        calculateButton.setText("Calculate");
        calculateButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        calculateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                calculateButtonActionPerformed(evt);
            }
        });

        exitButton.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        exitButton.setForeground(new java.awt.Color(131, 162, 172));
        exitButton.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        exitButton.setText("X");
        exitButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        exitButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                exitButtonMouseClicked(evt);
            }
        });

        minimizeButton.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        minimizeButton.setForeground(new java.awt.Color(131, 162, 172));
        minimizeButton.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        minimizeButton.setText("-");
        minimizeButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        minimizeButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                minimizeButtonMouseClicked(evt);
            }
        });

        mapPanel.setBackground(new java.awt.Color(0, 0, 0));

        javax.swing.GroupLayout mapPanelLayout = new javax.swing.GroupLayout(mapPanel);
        mapPanel.setLayout(mapPanelLayout);
        mapPanelLayout.setHorizontalGroup(
                mapPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 0, Short.MAX_VALUE)
        );
        mapPanelLayout.setVerticalGroup(
                mapPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 454, Short.MAX_VALUE)
        );
        mapPanel = createMap.createMapPanel();

        modeBox.setOpaque(false);
        modeBox.setBackground(new java.awt.Color(170, 211, 223));
        modeBox.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        modeBox.setForeground(new java.awt.Color(255, 255, 255));
        modeBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[]{"Walk", "Bike", "Bus"}));
        modeBox.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        startCodeLabel.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        startCodeLabel.setForeground(new java.awt.Color(0, 0, 0));
        startCodeLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        startCodeLabel.setText("Start Zipcode:");

        distanceNumberLabel.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        distanceNumberLabel.setForeground(new java.awt.Color(0, 0, 0));
        distanceNumberLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        distanceNumberLabel.setText("0 m");

        distanceTextLabel.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        distanceTextLabel.setForeground(new java.awt.Color(0, 0, 0));
        distanceTextLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        distanceTextLabel.setText("Distance:");

        destinationCodeLabel.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        destinationCodeLabel.setForeground(new java.awt.Color(0, 0, 0));
        destinationCodeLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        destinationCodeLabel.setText("Destination Zipcode:");

        startCodeField.setForeground(new java.awt.Color(0, 0, 0));

        destinationCodeField.setForeground(new java.awt.Color(0, 0, 0));

        timeTextLabel.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        timeTextLabel.setForeground(new java.awt.Color(0, 0, 0));
        timeTextLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        timeTextLabel.setText("Time:");

        timeNumberLabel.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        timeNumberLabel.setForeground(new java.awt.Color(0, 0, 0));
        timeNumberLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        timeNumberLabel.setText("0 min");

        javax.swing.GroupLayout backPanelLayout = new javax.swing.GroupLayout(backPanel);
        backPanel.setLayout(backPanelLayout);
        backPanelLayout.setHorizontalGroup(
                backPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(backPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(mapPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addContainerGap())
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, backPanelLayout.createSequentialGroup()
                                .addGroup(backPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(backPanelLayout.createSequentialGroup()
                                                .addGap(135, 135, 135)
                                                .addGroup(backPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                        .addComponent(distanceTextLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(distanceNumberLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                        .addGroup(backPanelLayout.createSequentialGroup()
                                                .addGroup(backPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addGroup(backPanelLayout.createSequentialGroup()
                                                                .addGap(41, 41, 41)
                                                                .addComponent(startCodeField, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                        .addGroup(backPanelLayout.createSequentialGroup()
                                                                .addGap(93, 93, 93)
                                                                .addComponent(startCodeLabel)))
                                                .addGroup(backPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addGroup(backPanelLayout.createSequentialGroup()
                                                                .addGap(169, 169, 169)
                                                                .addComponent(modeBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, backPanelLayout.createSequentialGroup()
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 116, Short.MAX_VALUE)
                                                                .addComponent(calculateButton, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addGap(121, 121, 121)))))
                                .addGroup(backPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, backPanelLayout.createSequentialGroup()
                                                .addGroup(backPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(timeTextLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(timeNumberLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addGap(154, 154, 154))
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, backPanelLayout.createSequentialGroup()
                                                .addComponent(destinationCodeLabel)
                                                .addGap(42, 42, 42)
                                                .addComponent(minimizeButton)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(exitButton)
                                                .addGap(15, 15, 15))
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, backPanelLayout.createSequentialGroup()
                                                .addComponent(destinationCodeField, javax.swing.GroupLayout.PREFERRED_SIZE, 197, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(49, 49, 49))))
        );
        backPanelLayout.setVerticalGroup(
                backPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(backPanelLayout.createSequentialGroup()
                                .addGroup(backPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addGroup(backPanelLayout.createSequentialGroup()
                                                .addGroup(backPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addGroup(backPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, backPanelLayout.createSequentialGroup()
                                                                        .addGap(17, 17, 17)
                                                                        .addComponent(startCodeLabel))
                                                                .addGroup(backPanelLayout.createSequentialGroup()
                                                                        .addContainerGap()
                                                                        .addGroup(backPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                                                .addComponent(exitButton)
                                                                                .addComponent(minimizeButton))))
                                                        .addComponent(destinationCodeLabel, javax.swing.GroupLayout.Alignment.TRAILING))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(backPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(destinationCodeField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(startCodeField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addGap(18, 18, 18))
                                        .addGroup(backPanelLayout.createSequentialGroup()
                                                .addComponent(calculateButton)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(modeBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(8, 8, 8)))
                                .addComponent(mapPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(backPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(timeTextLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(distanceTextLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(backPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(timeNumberLabel)
                                        .addComponent(distanceNumberLabel))
                                .addGap(15, 15, 15))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(backPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(backPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        setSize(new java.awt.Dimension(900, 598));
        setLocationRelativeTo(null);
    }

    private void calculateButtonActionPerformed(java.awt.event.ActionEvent evt) {
        if (startCodeField.getText().isEmpty() || destinationCodeField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Please fill in both fields.");
            return;
        }
        DataManagers.LogicManager logicManager = new DataManagers.LogicManager();
        logicManager.calculateLogic(startCodeField, destinationCodeField, modeBox);


    }

    private void exitButtonMouseClicked(java.awt.event.MouseEvent evt) {
        System.exit(0);
    }

    private void minimizeButtonMouseClicked(java.awt.event.MouseEvent evt) {
        this.setExtendedState(mapFrame.ICONIFIED);
    }


    public static void main(String args[]) {

        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(mapFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(mapFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(mapFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(mapFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new mapFrame().setVisible(true);
            }
        });
    }


}
