import org.tensorflow.Tensor;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOError;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;


public class Image {


    private String filePath;
    private byte[] byteImage;
    private Tensor imagesTensor;

    public Image(String filePath) {
        this.filePath = filePath;
    }

    public String getFilePath() {
        return filePath;
    }


    public byte[] getByteImage() {
        return byteImage;
    }

    public void setImagesTensor(Tensor imagesTensor) {
        this.imagesTensor = imagesTensor;
    }

    public Tensor getImagesTensor() {
        return imagesTensor;
    }

    public void imageToByte() throws IOException {

        // open image
        File imgPath = new File(this.getFilePath());
        System.out.println(this.getFilePath());
        try {
            this.byteImage = Files.readAllBytes(imgPath.toPath());
        } catch (IOException e) {
            System.out.println(e);
        }

    }
}
