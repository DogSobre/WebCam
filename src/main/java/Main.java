import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[]agrv){
        System.out.println("Hello Wolrd");
        BufferedImage img = null;

        try
        {
            img = ImageIO.read(new File("C:/ImageTest/pic2.jpg")); // eventually C:\\ImageTest\\pic2.jpg
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
