import org.tensorflow.Tensor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;


/**
 * This class contain the path of the Images and all informations of classified image (description, accuracy(percentage))
 */
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
    private String fileName;
    private String folder;

    /**
     * @param folder
     * @param fileName
     * MyImage's constructor create the images with their path and give them a name
     * This constructor classify the images and set values
     */
    public MyImage(String folder, String fileName) {
        tfUtils = new TFUtils();

        this.filePath = folder + fileName;
        this.fileName = fileName;
        this.folder = folder;
        this.fileToBytes(filePath);
        this.generateTensorImage();

        this.rdnRes = tfUtils.executeModelFromByteArray(dbRow.dbByte, this.imageTensor);

        this.maxPercent = tfUtils.fetchPercent(this.rdnRes);
        this.description = tfUtils.fetchDescription(this.rdnRes);

    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
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

    String getFilePath() {
        return filePath;
    }

    public byte[] getByteImage() {
        return byteImage;
    }

    public Tensor getImageTensor() {
        return imageTensor;
    }

    /**
     * This function takes a byte's image and generate his Tensor
     */
    public void generateTensorImage() {
        byte[] imgByte = this.byteImage;
        this.imageTensor = tfUtils.byteBufferToTensor(imgByte);
    }

    /**
     * @param path
     * The function catch the image with his path and convert him into a byte array
     */
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
