import org.tensorflow.Tensor;

public class Main {
    public static void main(String[] agrv) {
        TFUtils tfUtils = new TFUtils();
        DbRow dbRow = new DbRow("inception5h/tensorflow_inception_graph.pb");
        String[] pathList = {"inception5h/tensorPics/keyboard.jpg", "inception5h/tensorPics/mouse.jpg", "inception5h/tensorPics/retriever.jpg", "inception5h/tensorPics/suncokret.jpg"};
        Image img = new Image("inception5h/tensorPics/keyboard.jpg");
        Tensor tensor = tfUtils.executeModelFromByteArray(dbRow.getDbByte(), img.getImageTensor());
        System.out.println(tensor);
    }
}
