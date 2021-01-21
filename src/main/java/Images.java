import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Images {

    public static DbRow dbRow = new DbRow("inception5h/tensorflow_inception_graph.pb");
    private List<MyImage> myImages;
    private HashMap mapValDescription;
    private HashMap mapPathDescription;

    public Images() {
        this.myImages = new ArrayList<>();
        this.mapPathDescription = new HashMap();
        this.mapValDescription = new HashMap();

    }

    public void setImages(List<MyImage> myImages) {
        for (int i = 0; i < myImages.size(); i++) {
            this.addMapPathDescription(myImages.get(i).getFilePath(), myImages.get(i).getDescription());
            this.addMapValDescription(myImages.get(i).getMaxPercent(), myImages.get(i).getDescription());
        }
        this.myImages = myImages;
    }

    public List<MyImage> getImages() {
        return myImages;
    }

    public HashMap getMapValDescription() {
        return mapValDescription;
    }

    public HashMap getMapPathDescription() {
        return mapPathDescription;
    }

    public void addImage(MyImage myImage) {
        this.myImages.add(myImage);
        this.addMapPathDescription(myImage.getFilePath(), myImage.getDescription());
        this.addMapValDescription(myImage.getMaxPercent(), myImage.getDescription());

    }

    public void addMapPathDescription(String path, String desc) {
        this.mapPathDescription.put(path, desc);
    }

    public void addMapValDescription(Integer val, String desc) {
        this.mapValDescription.put(val, desc);
    }
}
