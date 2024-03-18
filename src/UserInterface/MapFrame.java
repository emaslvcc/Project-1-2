package UserInterface;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


public class MapFrame extends Application {
    private final int outerFrameHeight = 600;
    private final int outerFrameWidth = 800;
    private final String textStyle = "-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #1b2940";
    private final String titleStyle = "-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #1b2940";
    private final String buttonStyle = "-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #1b2940";
    private final int innerFrameHeight = outerFrameHeight - 200;
    private final int innerFrameWidth = innerFrameHeight - 200;

    private int min;

    private double distance;

    public void start(Stage primaryStage) {
        final BorderPane root = new BorderPane();
        primaryStage.getIcons().add(new Image("Logo/mapLogo.png"));
        Scene scene = new Scene(root, outerFrameWidth, outerFrameHeight, true, SceneAntialiasing.BALANCED);
        BorderPane bottomPane = createBottomPane();
        BorderPane centerPane = createCenterPane();
        BorderPane topPane = createTopPane();


        root.setTop(topPane);
        root.setCenter(centerPane);
        root.setBottom(bottomPane);
        root.setPadding(new javafx.geometry.Insets(15, 15, 15, 15));
        //up, right, down, left;

        BorderPane.setMargin(topPane, new javafx.geometry.Insets(0, 0, 15, 0));
        BorderPane.setMargin(bottomPane, new javafx.geometry.Insets(15, 0, 0, 0));
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.show();
    }

    public BorderPane createTopPane() {
        BorderPane topPane = new BorderPane();
        BorderPane startCodePane = createStartCodePane();
        BorderPane endCodePane = createEndCodePane();
        BorderPane centerPane = createTopCenterPane();
        topPane.setLeft(startCodePane);
        topPane.setRight(endCodePane);
        BorderPane.setMargin(startCodePane, new javafx.geometry.Insets(0, 0, 0, 50));
        BorderPane.setMargin(endCodePane, new javafx.geometry.Insets(0, 45, 0, 0));
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
        BorderPane.setMargin(startCodeLabel, new javafx.geometry.Insets(0, 0, 5, 20));
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
        BorderPane.setMargin(endCodeLabel, new javafx.geometry.Insets(0, 0, 5, 5));
        return endCodePane;
    }


    public BorderPane createCenterPane() {
        BorderPane centerPane = new BorderPane();
        SubScene mapSubscene = createMapSubscene();
        centerPane.setCenter(mapSubscene);
        centerPane.setStyle("-fx-background-color: black;");
        centerPane.setPrefSize(innerFrameWidth, innerFrameHeight);
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


    public SubScene createMapSubscene() {
        BorderPane root = new BorderPane();
        SubScene mapSubscene = new SubScene(root, innerFrameWidth, innerFrameHeight, true, SceneAntialiasing.BALANCED);
        return mapSubscene;
    }

    public static void main(String[] args) {
        launch(args);
    }

}
