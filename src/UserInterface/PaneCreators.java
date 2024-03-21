package UserInterface;

import DataManagers.LogicManager;
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

public class PaneCreators extends LogicManager {
    private final String textStyle = "-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #1b2940";
    private final String titleStyle = "-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #1b2940";
    private final String buttonStyle = "-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #1b2940";
    private ChoiceBox<String> modeBox = new ChoiceBox<>();
    private TextField startCodeField = new TextField();
    private TextField endCodeField = new TextField();
    private Label timeLabelValue;
    private Label distanceLabelValue;
    private final int outerFrameHeight;
    private final int outerFrameWidth;


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
            calculateLogic(startCodeField, endCodeField, modeBox);
            setDistance(distance);
            setTime(time);
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

        // time is being recalculated if transportation mode changes
        modeBox.setOnAction((event) -> {
            calculateLogic(startCodeField, endCodeField, modeBox);
            setTime(time);
        });

        VBox frameBox = new VBox(calculateButton, modeBox);
        frameBox.setAlignment(Pos.CENTER);
        topCenterPane.setCenter(frameBox);
        frameBox.setSpacing(10);

        return topCenterPane;
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
        exitButton.setOnAction(e -> System.exit(0));

        Label timeLabelTitle = new Label("Time:");
        timeLabelTitle.setStyle(titleStyle);
        timeLabelValue = new Label(time + " min");
        timeLabelValue.setStyle(textStyle);

        Label distanceLabelTitle = new Label("Distance: ");
        distanceLabelTitle.setStyle(titleStyle);
        distanceLabelValue = new Label(distance + " km");
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
    public void setTime(int min) {
        this.time = min;
        timeLabelValue.setText(min + " min");
    }

    public void setDistance(double distance) {
        this.distance = distance;
        distanceLabelValue.setText(distance + " km");
    }
}