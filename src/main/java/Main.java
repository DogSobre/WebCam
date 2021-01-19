import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] agrv) {
        System.out.println("Hello Wolrd");
        String[] pathList = {"inception5h/tensorPics/jack.jpg", "inception5h/tensorPics/jack.jpg"};
        Images images = new Images();
        images.addImages(pathList);
        System.out.println(images);

    }
}
