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

/**
 * This class creates the panes that are placed in the frame of the application.
 * The panes include the top, center and bottom panes as well as the other panes placed inside of them.
 */
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

    /**
     * This is the constructor for the PaneCreators class.
     *
     * @param outerFrameWidth  This sets the width size for the frame of the window
     * @param outerFrameHeight This sets the height size for the frame of the window
     */
    public PaneCreators(int outerFrameWidth, int outerFrameHeight) {
        this.outerFrameHeight = outerFrameHeight;
        this.outerFrameWidth = outerFrameWidth;
    }

    /**
     * This method creates the top pane of the frame which includes the start and end zip code fields and the calculate button.
     *
     * @return This returns the top pane of the frame
     */
    public BorderPane createTopPane() {
        //creates the top pane and the panes that will be placed in it
        BorderPane topPane = new BorderPane();
        BorderPane startCodePane = createStartCodePane();
        BorderPane endCodePane = createEndCodePane();
        BorderPane centerPane = createTopCenterPane();

        //sets the areas that the panes will be placed in the top pane
        topPane.setLeft(startCodePane);
        topPane.setRight(endCodePane);
        topPane.setCenter(centerPane);

        //sets the margins for the start and end code panes. this creates space between them and other elements
        BorderPane.setMargin(startCodePane, new Insets(0, 0, 0, 50));
        BorderPane.setMargin(endCodePane, new Insets(0, 45, 0, 0));

        return topPane;
    }

    /**
     * This method creates the start code pane which includes the start code label and the start code field.
     * This pane is placed in the top pane.
     *
     * @return This returns the start code pane.
     */
    private BorderPane createStartCodePane() {

        BorderPane startCodePane = new BorderPane();
        Label startCodeLabel = new Label("Start Zipcode: ");
        startCodeLabel.setStyle(titleStyle);

        startCodePane.setTop(startCodeLabel);
        startCodePane.setCenter(startCodeField);
        BorderPane.setMargin(startCodeLabel, new Insets(0, 0, 5, 20));
        return startCodePane;
    }

    /**
     * This method creates the top center pane which includes the calculate button and the mode box.
     * This pane is placed in the top pane.
     *
     * @return This returns the top center pane.
     */
    private BorderPane createTopCenterPane() {
        BorderPane topCenterPane = new BorderPane();

        //creates the calculate button, which calculates the distance and time between the two zip codes
        Button calculateButton = new Button("Calculate");
        calculateButton.setOnAction(e -> {
            calculateLogic(startCodeField, endCodeField, modeBox);
            setDistance(distance);
            setTime(time);
        });

        //sets the size and style of the calculate button
        calculateButton.setPrefSize(200, 40);
        calculateButton.setStyle(buttonStyle);
        calculateButton.setCursor(Cursor.HAND);

        //creates the mode box which contains the options for the mode of transportation
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

        //creates the vertical frame box which contains the calculate button and the mode box
        VBox frameBox = new VBox(calculateButton, modeBox);
        frameBox.setAlignment(Pos.CENTER);
        topCenterPane.setCenter(frameBox);
        frameBox.setSpacing(10);

        return topCenterPane;
    }

    /**
     * This method creates the end code pane which includes the end code label and the end code field.
     * This pane is placed in the top pane.
     *
     * @return This returns the end code pane.
     */
    private BorderPane createEndCodePane() {

        BorderPane endCodePane = new BorderPane();
        Label endCodeLabel = new Label("Destination Zipcode: ");
        endCodeLabel.setStyle(titleStyle);
        endCodePane.setTop(endCodeLabel);
        endCodePane.setCenter(endCodeField);
        BorderPane.setMargin(endCodeLabel, new Insets(0, 0, 5, 5));

        return endCodePane;
    }

    /**
     * This method creates the center pane of the frame which includes the map subscene.
     *
     * @return This returns the center pane of the frame.
     */
    public BorderPane createCenterPane() {

        BorderPane centerPane = new BorderPane();
        //creates an instance of the SubSceneHandler class and creates the map subscene
        SubSceneHandler SSHandler = new SubSceneHandler(outerFrameWidth, outerFrameHeight);
        SubScene mapSubscene = SSHandler.createMapSubscene();
        centerPane.setCenter(mapSubscene);

        return centerPane;
    }

    /**
     * This method creates the bottom pane of the frame which includes the exit button and the time and distance labels.
     *
     * @return This returns the bottom pane of the frame.
     */
    public BorderPane createBottomPane() {

        BorderPane bottomPane = new BorderPane();

        //creates the exit button which closes the application
        Button exitButton = new Button("Exit");
        exitButton.setStyle(buttonStyle);
        exitButton.setPrefSize(100, 30);
        exitButton.setOnAction(e -> System.exit(0));

        //creates the time and distance labels
        Label timeLabelTitle = new Label("Time:");
        timeLabelTitle.setStyle(titleStyle);
        timeLabelValue = new Label(time + " min");
        timeLabelValue.setStyle(textStyle);

        Label distanceLabelTitle = new Label("Distance: ");
        distanceLabelTitle.setStyle(titleStyle);
        distanceLabelValue = new Label(distance + " km");
        distanceLabelValue.setStyle(textStyle);

        //creates the vertical frame boxes which contains the time and distance labels
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

    /**
     * This method sets the time label to the newly calculate time value.
     *
     * @param min This is the new time value
     */
    public void setTime(int min) {
        this.time = min;
        timeLabelValue.setText(min + " min");
    }

    /**
     * This method sets the distance label to the newly calculate distance value.
     *
     * @param distance This is the new distance value
     */
    public void setDistance(double distance) {
        this.distance = distance;
        distanceLabelValue.setText(distance + " km");
    }
}
