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
    final private PaneCreators paneCreators = new PaneCreators();

    public void start(Stage primaryStage) {
        final BorderPane root = new BorderPane();
        primaryStage.getIcons().add(new Image("Logo/mapLogo.png"));
        Scene scene = new Scene(root, outerFrameWidth, outerFrameHeight, true, SceneAntialiasing.BALANCED);
        BorderPane bottomPane = paneCreators.createBottomPane();
        BorderPane centerPane = paneCreators.createCenterPane();
        BorderPane topPane = paneCreators.createTopPane();


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

    public int getOuterFrameHeight() {
        return outerFrameHeight;
    }

    public int getOuterFrameWidth() {
        return outerFrameWidth;
    }


}
