package co.siempo.phone.models;

import java.util.ArrayList;

/**
 * Created by parth on 4/5/18.
 */

public class ImageItem {
    public final String name;
    public boolean isVisable;
    public ArrayList<String> drawableId;

    public ImageItem(String name, ArrayList<String> drawableId, boolean isVisible) {
        this.name = name;
        this.drawableId = drawableId;
        this.isVisable = isVisible;
    }

    public String getName() {
        return name;
    }

    public ArrayList<String> getDrawableId() {
        return drawableId;
    }
}
