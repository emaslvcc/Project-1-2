package UserInterface;

import javafx.scene.SubScene;


public class SubSceneHandler {

    private final int innerFrameHeight;
    private final int innerFrameWidth;

    public SubSceneHandler(int outerFrameWidth, int outerFrameHeight) {
        this.innerFrameWidth = outerFrameWidth - 30;
        this.innerFrameHeight = outerFrameHeight - 200;
    }

    public SubScene createMapSubscene() {
        MapViewer mapViewer = new MapViewer();
        return mapViewer.createMapSubScene(innerFrameWidth, innerFrameHeight);
    }

}