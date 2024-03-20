package UserInterface;


import DataManagers.GetUserData;
import DataManagers.PostCode;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.SubScene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class PaneCreators extends GetUserData {
    private final String textStyle = "-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #1b2940";
    private final String titleStyle = "-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #1b2940";
    private final String buttonStyle = "-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #1b2940";
    private int min;
    private double distance;
    private ChoiceBox<String> modeBox = new ChoiceBox<>();
    private TextField startCodeField = new TextField();
    private TextField endCodeField = new TextField();
    private Label timeLabelValue;
    private Label distanceLabelValue;
    private PostCode startPostCode, endPostCode;
    private int outerFrameHeight;
    private int outerFrameWidth;


    public PaneCreators(int outerFrameWidth, int outerFrameHeight) {
        this.outerFrameHeight = outerFrameHeight;
        this.outerFrameWidth = outerFrameWidth;
    }

    public BorderPane createTopPane() {
        BorderPane topPane = new BorderPane();
        BorderPane startCodePane = createStartCodePane();
        BorderPane endCodePane = createEndCodePane();
        BorderPane centerPane = createTopCenterPane();
        topPane.setLeft(startCodePane);
        topPane.setRight(endCodePane);
        BorderPane.setMargin(startCodePane, new Insets(0, 0, 0, 50));
        BorderPane.setMargin(endCodePane, new Insets(0, 45, 0, 0));
        topPane.setCenter(centerPane);

        return topPane;
    }

    private BorderPane createStartCodePane() {
        BorderPane startCodePane = new BorderPane();
        Label startCodeLabel = new Label("Start Zipcode: ");
        startCodeLabel.setStyle(titleStyle);

        startCodePane.setTop(startCodeLabel);
        startCodePane.setCenter(startCodeField);
        BorderPane.setMargin(startCodeLabel, new Insets(0, 0, 5, 20));
        return startCodePane;
    }

    private BorderPane createTopCenterPane() {
        BorderPane topCenterPane = new BorderPane();
        Button calculateButton = new Button("Calculate");
        calculateButton.setOnAction(e -> {
            System.out.println("Calculating");
            createHashMap();
            try {
                startPostCode = getStartZip();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
            try {
                endPostCode = getEndZip();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
            MapViewer.updateCord(startPostCode,endPostCode);
            distance = Math.round(calculateAfterPressedButton(startPostCode,endPostCode)* 100d) / 100d;
            setDistance(distance);

        });

        calculateButton.setPrefSize(200, 40);
        calculateButton.setPrefSize(200, 40);
        calculateButton.setStyle(buttonStyle);
        calculateButton.setCursor(Cursor.HAND);

        modeBox.getItems().addAll("Walk", "Bike");
        modeBox.setValue("Walk");
        modeBox.setStyle(buttonStyle);
        modeBox.setCursor(Cursor.HAND);
        modeBox.setPrefSize(100, 10);

        VBox frameBox = new VBox(calculateButton, modeBox);
        frameBox.setAlignment(Pos.CENTER);
        topCenterPane.setCenter(frameBox);
        frameBox.setSpacing(10);

        return topCenterPane;
    }

    private PostCode getStartZip() throws Exception {
        String startCode = startCodeField.getText().toUpperCase();
        validatePostcode(startCode);
        startPostCode = getZipCode(dataMap, startCode );
        return startPostCode;
    }
    private PostCode getEndZip() throws Exception{

        String endCode = endCodeField.getText().toUpperCase();
        validatePostcode(endCode);
        endPostCode = getZipCode(dataMap, endCode);
        return endPostCode;
    }

    private void validatePostcode(String postcode) throws Exception {
        if (postcode.length() != 6) {
            throw new Exception("Postcode " + postcode + " is invalid: incorrect length.");
        } else if (Character.isDigit(postcode.charAt(4)) || Character.isDigit(postcode.charAt(5))) {
            throw new Exception("Postcode " + postcode + " is invalid: incorrect format.");
        } 
        
        for (int i = 0; i < 4; i++) {
            if(!Character.isDigit(postcode.charAt(i))) {
                throw new Exception("Postcode " + postcode + " is invalid: incorrect format.");
            }
        }
    }


    private void setTime(int min) {
        this.min = min;
        timeLabelValue.setText(min + " min");
    }

    public void setDistance(double distance) {
        this.distance = distance;
        distanceLabelValue.setText(distance + " km");
    }

    private BorderPane createEndCodePane() {
        BorderPane endCodePane = new BorderPane();
        Label endCodeLabel = new Label("Destination Zipcode: ");
        endCodeLabel.setStyle(titleStyle);
        endCodePane.setTop(endCodeLabel);
        endCodePane.setCenter(endCodeField);
        BorderPane.setMargin(endCodeLabel, new Insets(0, 0, 5, 5));
        return endCodePane;
    }

    public BorderPane createCenterPane() {
        BorderPane centerPane = new BorderPane();
        SubSceneHandler SSHandler = new SubSceneHandler(outerFrameWidth, outerFrameHeight);
        SubScene mapSubscene = SSHandler.createMapSubscene();
        centerPane.setCenter(mapSubscene);
        return centerPane;
    }

    public BorderPane createBottomPane() {
        BorderPane bottomPane = new BorderPane();

        Button exitButton = new Button("Exit");
        exitButton.setStyle(buttonStyle);
        exitButton.setPrefSize(100, 30);
        exitButton.setOnAction(e -> {
            System.exit(0);
        });

        Label timeLabelTitle = new Label("Time:");
        timeLabelTitle.setStyle(titleStyle);
        timeLabelValue = new Label(min + " min");
        timeLabelValue.setStyle(textStyle);

        Label distanceLabelTitle = new Label("Distance: ");
        distanceLabelTitle.setStyle(titleStyle);
        distanceLabelValue = new Label(Double.toString(distance) + " km");
        distanceLabelValue.setStyle(textStyle);


        VBox buttonBox = new VBox(exitButton);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        bottomPane.setRight(buttonBox);

        VBox timeVBox = new VBox(timeLabelTitle, timeLabelValue);
        timeVBox.setAlignment(Pos.CENTER);
        bottomPane.setCenter(timeVBox);

        VBox distanceVBox = new VBox(distanceLabelTitle, distanceLabelValue);
        distanceVBox.setAlignment(Pos.CENTER);
        bottomPane.setLeft(distanceVBox);

        return bottomPane;
    }
}