import org.tensorflow.Tensor;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOError;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;


public class Image implements MyFile {

    public TFUtils tfUtils;
    private String filePath;
    private byte[] byteImage;
    private Tensor imageTensor;

    public Image(String filePath) {
        tfUtils = new TFUtils();
        this.filePath = filePath;
        this.fileToBytes(filePath);
        this.generateTensorImage();
    }

    public String getFilePath() {
        return filePath;
    }


    public byte[] getByteImage() {
        return byteImage;
    }

    public Tensor getImageTensor() {
        return imageTensor;
    }


    public void generateTensorImage() {
        byte[] imgByte = this.byteImage;
        this.imageTensor = tfUtils.byteBufferToTensor(imgByte);
    }

    @Override
    public void fileToBytes(String path) {
        try {
            // open image
            File imgPath = new File(path);
            this.byteImage = Files.readAllBytes(imgPath.toPath());
        } catch (IOException e) {
            System.out.println(e);
        }
    }
}
