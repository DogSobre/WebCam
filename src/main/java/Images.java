
import java.util.List;

public class Images {


    private List<Image> images;

    public Images(String filePath) {
        super();
    }

    public Images() {

    }

    public void addImage(Image img) {
        this.images.add(img);
    }

    public void addImages(String[] pathList) {
        for (int i = 0; i < pathList.length; i++) {
            Image img = new Image(pathList[i]);
            img.imageToByte();
            this.addImage(img);
        }
    }

    public List<Image> getByteImage() {
        return images;
    }


}
