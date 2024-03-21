package UserInterface;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * This class creates the applications window
 */
public class MapFrame extends Application {
    //initiate the variables stating the frame size
    private final int outerFrameHeight = 600;
    private final int outerFrameWidth = 800;
    final private PaneCreators paneCreators = new PaneCreators(outerFrameWidth, outerFrameHeight);

    /**
     * This is the main method that creates the application window
     * @param primaryStage This is the stage that the application is displayed on
     */
    @Override
    public void start(Stage primaryStage) {
        final BorderPane root = new BorderPane();

        //sets the icon of the application
        primaryStage.getIcons().add(new Image("Images/mapLogo.png"));

        //creates the scene
        Scene scene = new Scene(root, outerFrameWidth, outerFrameHeight, true, SceneAntialiasing.BALANCED);

        //creates the borde panes for the top, center and bottom of the frame
        BorderPane bottomPane = paneCreators.createBottomPane();
        BorderPane centerPane = paneCreators.createCenterPane();
        BorderPane topPane = paneCreators.createTopPane();

        //sets the top, center and bottom panes to the root
        root.setTop(topPane);
        root.setCenter(centerPane);
        root.setBottom(bottomPane);

        //sets the padding for the sides of the frame. (up, right, down, left)
        root.setPadding(new javafx.geometry.Insets(15, 15, 15, 15));

        //sets the margins for the top and bottom panes
        BorderPane.setMargin(topPane, new javafx.geometry.Insets(0, 0, 15, 0));
        BorderPane.setMargin(bottomPane, new javafx.geometry.Insets(15, 0, 0, 0));
        primaryStage.setScene(scene);

        //makes the fame not resizable and removes the default window decorations such as the close, minimize and maximize buttons
        primaryStage.setResizable(false);
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.show();
    }
}

