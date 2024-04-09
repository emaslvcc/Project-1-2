package UserInterface;

import javafx.scene.SubScene;

/**
 * This class creates the subscene that is placed in the center pane.
 */
public class SubSceneHandler {

    private final int innerFrameHeight;
    private final int innerFrameWidth;

    /**
     * This is the constructor for the SubSceneHandler class.
     *
     * @param outerFrameWidth  This is used to set the width size for inner frame on which the SubScene is placed on
     * @param outerFrameHeight This is used to set the height size for inner frame on which the SubScene is placed on
     */
    public SubSceneHandler(int outerFrameWidth, int outerFrameHeight) {
        //30 and 200 are subtracted from the outer frame's dimension since the inner frame is smaller
        this.innerFrameWidth = outerFrameWidth - 30;
        this.innerFrameHeight = outerFrameHeight - 200;
    }

    /**
     * This method creates a map subscene.
     *
     * @return This returns the map subscene that will be placed in the frame
     */
    public SubScene createMapSubscene() {
        MapViewer mapViewer = new MapViewer();
        return mapViewer.createMapSubScene(innerFrameWidth, innerFrameHeight);
    }

}
