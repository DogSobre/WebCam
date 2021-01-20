import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] agrv) {
        System.out.println("Hello Wolrd");
        String[] pathList = {"inception5h/tensorPics/keyboard.jpg", "inception5h/tensorPics/mouse.jpg", "inception5h/tensorPics/retriever.jpg", "inception5h/tensorPics/suncokret.jpg"};
        Images images = new Images();

        images.addImages(pathList);
        images.generateTensorImages();
        System.out.println(images.getImages());
    }
}
