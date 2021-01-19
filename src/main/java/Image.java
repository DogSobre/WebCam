import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

public class Image {


    private String filePath;
    private byte[] byteImage;

    public Image(String filePath) {
        this.filePath = filePath;
    }

    public String getFilePath() {
        return filePath;
    }


    public byte[] getByteImage() {
        return byteImage;
    }


    public void imageToByte() {
        // open image
        File imgPath = new File(this.getFilePath());
        try {
            BufferedImage bufferedImage = ImageIO.read(imgPath);
            // get DataBufferBytes from Raster
            WritableRaster raster = bufferedImage.getRaster();
            DataBufferByte data = (DataBufferByte) raster.getDataBuffer();

            this.byteImage = (data.getData());

        } catch (IOException e) {
            System.out.println(e);
        }

    }
}
