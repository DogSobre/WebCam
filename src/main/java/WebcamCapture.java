import org.bytedeco.javacv.*;
import org.bytedeco.javacv.Frame;
import org.bytedeco.opencv.opencv_core.IplImage;
import org.tensorflow.Tensor;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import static org.bytedeco.opencv.global.opencv_core.cvFlip;
import static org.bytedeco.opencv.helper.opencv_imgcodecs.cvSaveImage;

/**
 * This class it was the class who takes control of your webcam
 */
public class WebcamCapture implements Runnable {
    private Integer INTERVAL;///you may use interval
    CanvasFrame canvas = new CanvasFrame("Web Cam");
    private String description;
    private Integer maxPercent;
    private TFUtils tfUtils = new TFUtils();
    public static DbRow dbRow = new DbRow("inception5h/tensorflow_inception_graph.pb");
    private Tensor rdnRes;
    private String pahToSave;

    /**
     * @param interval
     * @param pathToSav
     *Instantiate the images who's taken by webcam
     */
    public WebcamCapture(Integer interval, String pathToSav) {

        this.INTERVAL = interval;
        this.pahToSave = pathToSav ;
        canvas.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
    }

    /**
     * The following function run the recording window and saves the image al the INTERVAL seconds
     */
    public void run() {
        FrameGrabber grabber = new OpenCVFrameGrabber(0); // 1 for next camera
        OpenCVFrameConverter.ToIplImage converter = new OpenCVFrameConverter.ToIplImage();
        IplImage img;
        int i = 0;
        try {
            grabber.start();

            while (true) {
                Frame frame = grabber.grab();

                byte[] imageInByte = this.toByteArray(this.getImage(frame), "JPG");

                // Classify the image recording and return the associated description for every images and the percentage of certainty
                this.rdnRes = tfUtils.executeModelFromByteArray(dbRow.dbByte, tfUtils.byteBufferToTensor(imageInByte));
                this.maxPercent = tfUtils.fetchPercent(this.rdnRes);
                this.description = tfUtils.fetchDescription(this.rdnRes);


                img = converter.convert(frame);

                //the grabbed frame will be flipped, re-flip to make it right
                cvFlip(img, img, 1);// l-r = 90_degrees_steps_anti_clockwise

                // Save image as the path on the @param
                cvSaveImage(this.pahToSave + "/" + File.separator + (i++) + "-aa.jpg", img);
                canvas.setTitle(this.description + " " + this.maxPercent + "%");
                canvas.showImage(converter.convert(img));
                Thread.sleep(this.INTERVAL);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * @param frame
     * Get the frame and convert it on image
     * @return
     */
    public BufferedImage getImage(Frame frame) {
        return new Java2DFrameConverter().convert(frame);
    }

    /**
     * @param bi
     * @param format
     * Get the image and convert it into a Byte Array
     * @return
     * @throws IOException
     */
    public byte[] toByteArray(BufferedImage bi, String format) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bi, format, baos);
        byte[] bytes = baos.toByteArray();
        return bytes;
    }
}