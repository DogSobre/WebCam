import org.tensorflow.Tensor;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] agrv) throws Exception {
        String[] pathList = {"inception5h/tensorPics/keyboard.jpg", "inception5h/tensorPics/mouse.jpg", "inception5h/tensorPics/retriever.jpg", "inception5h/tensorPics/suncokret.jpg"};
        List<Image> images = new ArrayList<>();
        for (int i = 0; i < pathList.length; i++) {
            Image img = new Image(pathList[i]);
            images.add(img);
            System.out.println(img.getMapValDescription());
        }
    }
}
