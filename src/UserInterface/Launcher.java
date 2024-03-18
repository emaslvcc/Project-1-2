// package UserInterface;

// import javafx.application.Application;
// import javafx.geometry.Insets;
// import javafx.scene.Scene;
// import javafx.scene.control.Button;
// import javafx.scene.control.Label;
// import javafx.scene.control.TextField;
// import javafx.scene.layout.GridPane;
// import javafx.stage.Stage;

// public class Launcher extends Application {

// @Override
// public void start(Stage primaryStage) {
// primaryStage.setTitle("Distance Calculator");

// GridPane grid = new GridPane();
// grid.setPadding(new Insets(10, 10, 10, 10));
// grid.setVgap(5);
// grid.setHgap(5);

// // 创建组件
// TextField startInput = new TextField();
// startInput.setPromptText("Enter start zip code");
// TextField endInput = new TextField();
// endInput.setPromptText("Enter end zip code");
// Button calculateButton = new Button("Calculate Distance");
// Label resultLabel = new Label();

// // 添加组件到网格
// grid.add(new Label("Start Point:"), 0, 0);
// grid.add(startInput, 1, 0);
// grid.add(new Label("End Point:"), 0, 1);
// grid.add(endInput, 1, 1);
// grid.add(calculateButton, 1, 2);
// grid.add(resultLabel, 1, 3);

// // 设置按钮事件处理器
// calculateButton.setOnAction(e -> {
// String startPoint = startInput.getText();
// String endPoint = endInput.getText();
// resultLabel.setText(DistanceCalculator.haversine(startPoint, endPoint));
// });

// Scene scene = new Scene(grid, 400, 200);
// primaryStage.setScene(scene);
// primaryStage.show();
// }

// private String calculateDistance(String start, String end) {
// try {
// String[] startCoords = start.split(",");
// String[] endCoords = end.split(",");
// double startX = Double.parseDouble(startCoords[0]);
// double startY = Double.parseDouble(startCoords[1]);
// double endX = Double.parseDouble(endCoords[0]);
// double endY = Double.parseDouble(endCoords[1]);

// double distance = Math.sqrt(Math.pow(endX - startX, 2) + Math.pow(endY -
// startY, 2));
// return "Distance: " + distance;
// } catch (Exception e) {
// return "Invalid input";
// }
// }

// public static void main(String[] args) {
// launch(args);
// }
// }
