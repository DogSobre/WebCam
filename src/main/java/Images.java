import org.tensorflow.Tensor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Images {

    public TFUtils tfUtils;
    private List<Image> images;


    public Images(String filePath) {
        super();
    }

    public Images() {

    }

    public List<Image> getImages() {
        return images;
    }

    public void addImage(Image img) {
        this.images.add(img);
    }

    public void addImages(String[] pathList) {

        List<Image> copyImg = new ArrayList<>();
        for (int i = 0; i < pathList.length; i++) {
            try {
                Image img = new Image(pathList[i]);
                img.imageToByte();
                copyImg.add(img);
            } catch (IOException e) {
                System.out.println(e);
                continue;

            }
        }
        this.images = copyImg;

    }

    public List<Tensor> generateTensorImages() {
        List<Tensor> tensors = new ArrayList<>();
        for (int i = 0; i < this.images.size(); i++) {
            byte[] imgByte = this.images.get(i).getByteImage();
            Tensor imgTensor = tfUtils.byteBufferToTensor(imgByte);
            tensors.add(imgTensor);
        }
        System.out.println(tensors);
        return tensors;
    }

    public List<Image> getByteImage() {
        return images;
    }


}
