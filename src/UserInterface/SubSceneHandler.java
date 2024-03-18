package UserInterface;

import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.layout.BorderPane;

public class SubSceneHandler {

    private final int innerFrameHeight;
    private final int innerFrameWidth;

    public SubSceneHandler(int outerFrameWidth, int outerFrameHeight) {
        this.innerFrameWidth = outerFrameWidth - 200;
        this.innerFrameHeight = outerFrameHeight - 200;
    }

    public SubScene createMapSubscene() {
        BorderPane root = new BorderPane();
        SubScene mapSubscene = new SubScene(root, innerFrameWidth, innerFrameHeight, true, SceneAntialiasing.BALANCED);
        return mapSubscene;
    }

}
