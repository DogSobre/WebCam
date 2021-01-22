import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;

import javafx.event.Event;
import javafx.event.EventHandler;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.Light;
import javafx.scene.effect.Lighting;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import org.apache.commons.io.FileUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.nio.file.Paths;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * ImageApp is the class who's managed the Application
 */
public class ImagesApp extends Application {
    public Integer MAX_PERCENT = 100;
    public Integer MIN_PERCENT = 0;
    private Images images = new Images();
    private Integer percentCondion = MIN_PERCENT;
    private List<String> descriptionsCondition = new ArrayList<>();
    private File folderToSave = null;

    //new File(String.valueOf(Paths.get(System.getProperty("user.home"), "Downloads")) );
    private Integer timeVideoInS = 10;
    final ColorPicker colorPicker = new ColorPicker();
    private Color colorFilterImage = null;

    /**
     * @param primaryStage
     * @throws Exception
     * That starts the main window of the Application
     */
    @Override
    public void start(Stage primaryStage) throws Exception {

        VBox vBox = new VBox();
        VBox rootVbow = new VBox();
        rootVbow.setLayoutX(50);
        rootVbow.setLayoutY(50);
        // The root of the scene shown in the main window

        StackPane root = new StackPane();
        String textOnSleectedFolder = this.folderToSave != null ? FileUtils.readFileToString(this.folderToSave, "UTF-8") : "Save as";

        // Create a button with specified text
        Button loadFoler = new Button("Load new folder images'");
        Button loadWebCam = new Button("Load with webcam'");
        Button folderToSave = new Button(textOnSleectedFolder);

        // Choose the minimum percent the user wants to match with his image
        Label percentMinLabel = new Label("Percent minimum:");
        TextField percentTextField = new TextField();
        percentTextField.textProperty().addListener((observable, oldVal, newVal) -> {
            setPercentCondion(newVal);
        });

        // Filter the images by description wrote by the user
        Label imageLabel = new Label("Label images separate by ',': ");
        TextField imageTextField1 = new TextField();
        imageTextField1.textProperty().addListener((observable, oldVal, newVal) -> {
            setDescriptionsCondition(newVal);
        });

        // Take pictures from the webcam every x seconds (the variable x is the number of seconds between each image taken)
        Label timeInSLabel = new Label("Time in s for save image when recording video: ");
        TextField timeInSTextField2 = new TextField();
        timeInSTextField2.textProperty().addListener((observable, oldVal, newVal) -> {
            setTimeInS(newVal);
        });

        rootVbow.getChildren().addAll(percentMinLabel, percentTextField);
        rootVbow.setSpacing(10);
        rootVbow.getChildren().addAll(imageLabel, imageTextField1);
        rootVbow.setSpacing(10);
        rootVbow.getChildren().addAll(timeInSLabel, timeInSTextField2);
        rootVbow.setSpacing(10);
        rootVbow.getChildren().addAll(folderToSave);

        // set a handler that is executed when the user activates the button
        // e.g. by clicking it or pressing enter while it's focused
        loadFoler.setOnAction(e -> {
            File choosedFolder = chooseSpecificFolder(primaryStage);
            Stage imageChargedStage = new Stage();
            images.setImages(loadAllFilesJPGFromDirectory(choosedFolder));
            this.createButtonsByImage(images.getImages(), vBox);
            this.selecImageToSave(images.getImages());
            StackPane imagesCharged = new StackPane();
            imagesCharged.getChildren().add(vBox);

            // create a scene specifying the root and the size
            Scene imagesChargedScene = new Scene(imagesCharged, 300, 300);
            imageChargedStage.setTitle("Selected images analysed");

            // add scene to the stage
            imageChargedStage.setScene(imagesChargedScene);

            // make the stage visible
            imageChargedStage.show();
        });

        loadWebCam.setOnAction(a -> {
            setTimeInS(timeInSTextField2.getText());
            WebcamCapture gs = new WebcamCapture(this.timeVideoInS, this.folderToSave != null ? this.folderToSave.getPath() : String.valueOf(Paths.get(System.getProperty("user.home"), "Downloads")));
            Thread th = new Thread(gs);
            th.start();
        });

        folderToSave.setOnAction(e -> {
            File choosedFolder = chooseSpecificFolder(primaryStage);
            this.setFolderToSave(choosedFolder);
            folderToSave.setText(choosedFolder.getPath());
        });

        // add button as child of the root
        rootVbow.getChildren().add(loadFoler);
        rootVbow.getChildren().add(loadWebCam);

        // add button as child of the root
        root.getChildren().add(rootVbow);

        // create a scene specifying the root and the size
        Scene scene = new Scene(root, 500, 300);
        primaryStage.setTitle("Images classified");

        // add scene to the stage
        primaryStage.setScene(scene);

        // make the stage visible
        primaryStage.show();
    }

    public Integer getPercentCondion() {
        return percentCondion;
    }

    /**
     * @param percentCondion
     * This function set the condition for the function who's chosen the minimum percent to save the image
     */
    public void setPercentCondion(String percentCondion) {
        Integer foo;
        try {
            foo = Integer.parseInt(percentCondion);
        } catch (NumberFormatException e) {
            foo = MAX_PERCENT;
        }
        this.percentCondion = foo;
    }

    public List<String> getDescriptionsCondition() {
        return descriptionsCondition;
    }

    /**
     * @param descriptionsCondition
     * This function set the condition for the function who's chosen the description, to save the image
     */
    public void setDescriptionsCondition(String descriptionsCondition) {
        // Convert String Array to List
        descriptionsCondition = descriptionsCondition.replaceAll("\\s+", "");
        this.descriptionsCondition = Arrays.asList(descriptionsCondition.split(","));
    }

    public File getFolderToSave() {
        return folderToSave;
    }

    /**
     * @param folderToSave
     * Select the
     */
    public void setFolderToSave(File folderToSave) {
        this.folderToSave = folderToSave;
    }

    /**
     * @param folder
     * The function allows to choose and open a image folder
     * @return
     */
    public List<MyImage> loadAllFilesJPGFromDirectory(File folder) {
        File[] listOfFiles = folder.listFiles();
        List<MyImage> myImages = new ArrayList<>();
        for (int i = 0; i < listOfFiles.length; i++) {

            if (listOfFiles[i].getName().contains(".jpg"))
                if (listOfFiles[i].isFile()) {
                    MyImage myImage = new MyImage(folder + "/", listOfFiles[i].getName());
                    myImages.add(myImage);
                } else if (listOfFiles[i].isDirectory()) {
                    System.out.println("Directory " + listOfFiles[i].getName());
                }
        } return myImages;
    }

    /**
     * @param time
     */
    public void setTimeInS(String time) {
        Integer foo;
        try {
            foo = Integer.parseInt(time);
        } catch (NumberFormatException e) {
            foo = MAX_PERCENT;
        } this.timeVideoInS = foo;
    }

    /**
     * @param img
     *
     */
    public void selecImageToSave(List<MyImage> img) {
        for (int i = 0; i < img.size(); i++) {
            MyImage myImage = img.get(i);
            Boolean ifDescInArray = this.descriptionsCondition.contains(myImage.getDescription().replaceAll("\\s+", ""));
            if (this.percentCondion < myImage.getMaxPercent()) {
                if (this.descriptionsCondition.size() == 0)
                    this.saveFileAs(myImage);
                else if (ifDescInArray)
                    this.saveFileAs(myImage);
            }
        }
    }

    /**
     * @param img
     * @param vBox
     */
    public void createButtonsByImage(List<MyImage> img, VBox vBox) {
        for (int i = 0; i < img.size(); i++) {
            Button b = new Button(img.get(i).getDescription() + " : " + img.get(i).getMaxPercent() + "%");
            Integer index = i;
            b.setOnAction(a -> {
                createNewAlertWithImageDescr(img.get(index).getFilePath(), img.get(index).getDescription() + " : " + img.get(index).getMaxPercent() + "%");
            });
            vBox.getChildren().add(b);
        }
    }

    /**
     * @param myImage
     */
    private void saveFileAs(MyImage myImage) {
        try {
            String destinationFile = this.folderToSave != null ? this.folderToSave.getPath() + "/" + myImage.getFileName() : String.valueOf(Paths.get(System.getProperty("user.home"), "Downloads") + "/" + myImage.getFileName());
            URL url = new URL(myImage.getFilePath());
            InputStream is = url.openStream();
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(new File(destinationFile)));

            byte[] b = new byte[2048];
            int length;

            while ((length = is.read(b)) != -1) {
                bos.write(b, 0, length);
            } is.close();
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    /**
     * @param path
     * @param desc
     *
     */
    public void createNewAlertWithImageDescr(String path, String desc) {
        try {
            Image image = new Image(new FileInputStream(path));
            ImageView imageView = new ImageView(image);

            String textOnSleectedFolder = this.folderToSave != null ? FileUtils.readFileToString(this.folderToSave, "UTF-8") : "Save as";

            Button saveImage = new Button("Save");

            //Setting the position of the image
            imageView.setX(50);
            imageView.setY(25);

            //setting the fit height and width of the image view
            imageView.setFitHeight(455);
            imageView.setFitWidth(500);

            // Items
            ScrollPane sp = new ScrollPane();
            Label secondLabel = new Label(desc);
            StackPane secondaryLayout = new StackPane();

            secondaryLayout.getChildren().add(secondLabel);

            //Creating a Group object
            Group root = new Group(imageView);
            root.getChildren().add(secondaryLayout);

            sp.setContent(root);

            // New window (Stage)
            Stage newWindow = new Stage();

            colorPicker.setOnAction(new EventHandler() {

                /**
                 * @param t
                 */
                public void handle(Event t) {
                    Color c = colorPicker.getValue();
                    imageView.setEffect(filterColor(c));
                    colorFilterImage = c;
                }
            });
            colorPicker.setLayoutX(600);
            colorPicker.setLayoutY(600);

            saveImage.setLayoutX(600);
            saveImage.setLayoutY(720);

            // Open new window with the pictures folder
            saveImage.setOnAction(e -> {
                File choosedFolder = chooseSpecificFolder(newWindow);
                this.setFolderToSave(choosedFolder);
                saveImage.setText(choosedFolder.getPath());
                String fileNameUse = desc.split("\\s+")[0];

                String imageViewPath = this.folderToSave != null ? this.folderToSave.getPath() + "/" : String.valueOf(Paths.get(System.getProperty("user.home"), "Downloads") + "/");

                // Save the buffered image
                BufferedImage bfImage = null;
                try {
                    bfImage = colorFilterImage != null ? createColorBufferImage(imageViewToBufferedImage(imageView), colorFilterImage.hashCode()) :
                            imageViewToBufferedImage(imageView);
                    writeBufferedImage(bfImage, imageViewPath, fileNameUse);
                    System.out.println("Image save as" + imageViewPath);
                } catch (IOException exception) {
                    System.out.println("Can't save image");
                }
            });

            //Creating a scene object
            Scene scene = new Scene(sp, 800, 800);

            //Setting title to the Stage
            newWindow.setTitle(path);

            //Adding scene to the stage
            newWindow.setScene(scene);
            root.getChildren().add(colorPicker);
            root.getChildren().addAll(saveImage);

            //Displaying the contents of the stage
            newWindow.show();
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    /**
     * @param primaryStage
     *
     * @return selectedDirectory
     */
    // Open a Finder Window, the user can select a folder with images
    public File chooseSpecificFolder(Stage primaryStage) {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Choose folder with JPG");
        File defaultDirectory = new File(String.valueOf(Paths.get(System.getProperty("user.home"), "Downloads")));
        chooser.setInitialDirectory(defaultDirectory);
        File selectedDirectory = chooser.showDialog(primaryStage);
        return selectedDirectory;
    }

    public void setColorFilterImage(Color colorFilterImage) {
        this.colorFilterImage = colorFilterImage;
    }

    /**
     * @param bImage
     * @param path
     * @param desc
     * 
     */
    public void writeBufferedImage(BufferedImage bImage, String path, String desc) {
        try {
            String newPath = path + desc + ".jpg";
            ImageIO.write(bImage, "jpg", new File(newPath));
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    /**
     * @param originalImage
     * @param mask
     * Create a filter in front of the picture
     * @return
     * @throws IOException
     */
    private BufferedImage createColorBufferImage(BufferedImage originalImage, int mask) throws IOException {
        BufferedImage colorImage = new BufferedImage(originalImage.getWidth(),
                originalImage.getHeight(), originalImage.getType());

        for (int x = 0; x < originalImage.getWidth(); x++) {
            for (int y = 0; y < originalImage.getHeight(); y++) {
                int pixel = originalImage.getRGB(x, y) & mask;
                colorImage.setRGB(x, y, pixel);
            }
        }

        return colorImage;
    }

    /**
     * @param imageView
     * @return
     *
     */
    public BufferedImage imageViewToBufferedImage(ImageView imageView) {
        BufferedImage backImg = SwingFXUtils.fromFXImage(imageView.getImage(), null);
        return backImg;
    }

    /**
     * @param color
     * @return
     * Changes the color of the image
     */
    public Lighting filterColor(Color color) {
        Lighting lighting = new Lighting();
        lighting.setDiffuseConstant(1.0);
        lighting.setSpecularConstant(0.0);
        lighting.setSpecularExponent(0.0);
        lighting.setSurfaceScale(0.0);
        lighting.setLight(new Light.Distant(45, 45, color));
        return lighting;
    }
}
