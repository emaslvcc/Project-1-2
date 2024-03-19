package UserInterface;

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

public class PaneCreators {
    private final String textStyle = "-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #1b2940";
    private final String titleStyle = "-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #1b2940";
    private final String buttonStyle = "-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #1b2940";
    private int min;
    private double distance;


    private int outerFrameHeight;
    private int outerFrameWidth ;
    private final SubSceneHandler SSHandler = new SubSceneHandler(outerFrameWidth, outerFrameHeight);

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
        TextField startCodeField = new TextField();
        startCodePane.setTop(startCodeLabel);
        startCodePane.setCenter(startCodeField);
        BorderPane.setMargin(startCodeLabel, new Insets(0, 0, 5, 20));
        return startCodePane;
    }

    private BorderPane createTopCenterPane() {
        BorderPane topCenterPane = new BorderPane();
        Button calculateButton = new Button("Calculate");
        calculateButton.setOnAction(e -> {
            System.out.println("Hi");
        });

        calculateButton.setPrefSize(200, 40);
        calculateButton.setPrefSize(200, 40);
        calculateButton.setStyle(buttonStyle);
        calculateButton.setCursor(Cursor.HAND);

        ChoiceBox<String> modeBox = new ChoiceBox<>();
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

    private BorderPane createEndCodePane() {
        BorderPane endCodePane = new BorderPane();
        Label endCodeLabel = new Label("Destination Zipcode: ");
        endCodeLabel.setStyle(titleStyle);
        TextField endCodeField = new TextField();
        endCodePane.setTop(endCodeLabel);
        endCodePane.setCenter(endCodeField);
        BorderPane.setMargin(endCodeLabel, new Insets(0, 0, 5, 5));
        return endCodePane;
    }

    public BorderPane createCenterPane() {
        BorderPane centerPane = new BorderPane();
        SubScene mapSubscene = SSHandler.createMapSubscene();
        centerPane.setCenter(mapSubscene);
       // centerPane.setStyle("-fx-background-color: black;");
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
        Label timeLabelValue = new Label(min + " min");
        timeLabelValue.setStyle(textStyle);

        Label distanceLabelTitle = new Label("Distance: ");
        distanceLabelTitle.setStyle(titleStyle);
        Label distanceLabelValue = new Label(Double.toString(distance) + " km");
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
