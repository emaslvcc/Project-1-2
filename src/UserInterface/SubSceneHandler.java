package UserInterface;

import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.layout.BorderPane;


public class SubSceneHandler {

    private final int innerFrameHeight;
    private final int innerFrameWidth;

    public SubSceneHandler(int outerFrameWidth, int outerFrameHeight) {
        this.innerFrameWidth = outerFrameWidth - 30;
        this.innerFrameHeight = outerFrameHeight - 200;
    }

    public SubScene createMapSubscene() {
        MapViewer mapViewer = new MapViewer();
        SubScene mapSubscene = mapViewer.createMapSubScene(innerFrameWidth, innerFrameHeight);
        return mapSubscene;
    }

}
