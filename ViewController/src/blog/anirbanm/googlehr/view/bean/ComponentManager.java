package blog.anirbanm.googlehr.view.bean;

import oracle.adf.view.rich.component.rich.input.RichInputFile;

public class ComponentManager {

    private RichInputFile imagePath;

    public ComponentManager() {
        super();
    }

    public void setImagePath(RichInputFile imagePath) {
        this.imagePath = imagePath;
    }

    public RichInputFile getImagePath() {
        return imagePath;
    }
}
