import org.tensorflow.Tensor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;


public class MyImage implements MyFile {

    public static DbRow dbRow = new DbRow("inception5h/tensorflow_inception_graph.pb");
    public TFUtils tfUtils;
    private String filePath;
    private byte[] byteImage;
    private Tensor imageTensor;
    private Tensor rdnRes;

    private HashMap mapValDescription;
    private HashMap mapPathDescription;
    private Integer maxPercent;
    private String description;

    public MyImage(String filePath) {
        tfUtils = new TFUtils();

        this.filePath = filePath;
        this.fileToBytes(filePath);
        this.generateTensorImage();

        this.rdnRes = tfUtils.executeModelFromByteArray(dbRow.dbByte, this.imageTensor);

        this.maxPercent = tfUtils.fetchPercent(this.rdnRes);
        this.description = tfUtils.fetchDescription(this.rdnRes, filePath);

    }

    public HashMap getMapPathDescription() {
        return mapPathDescription;
    }

    public Integer getMaxPercent() {
        return maxPercent;
    }

    public Tensor getRdnRes() {
        return rdnRes;
    }


    public String getDescription() {
        return description;
    }

    public HashMap getMapValDescription() {
        return mapValDescription;
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
