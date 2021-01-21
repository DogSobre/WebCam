import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.apache.commons.io.FileUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

public class ImagesApp extends Application {
    public Integer MAX_PERCENT = 100;
    public Integer MIN_PERCENT = 0;
    private Images images = new Images();
    private Integer percentCondion = MIN_PERCENT;
    private List<String> descriptionsCondition = new ArrayList<>();
    private File folderToSave = null;
    private Integer timeVideoInS = 10;

    @Override
    public void start(Stage primaryStage) throws Exception {
        VBox vBox = new VBox();
        VBox rootVbow = new VBox();
        rootVbow.setLayoutX(50);
        rootVbow.setLayoutY(50);
        // the root of the scene shown in the main window
        StackPane root = new StackPane();
        String textOnSleectedFolder = this.folderToSave != null ? FileUtils.readFileToString(this.folderToSave, "UTF-8") : "Save as";
        // create a button with specified text
        Button loadFoler = new Button("Load new folder images'");
        Button loadWebCam = new Button("Load with webcam'");
        Button folderToSave = new Button(textOnSleectedFolder);


        Label percentMinLabel = new Label("Percent minimum:");
        TextField percentTextField = new TextField();
        percentTextField.textProperty().addListener((observable, oldVal, newVal) -> {
            setPercentCondion(newVal);
        });

        Label imageLabel = new Label("Label images separate by ',': ");
        TextField imageTextField1 = new TextField();
        imageTextField1.textProperty().addListener((observable, oldVal, newVal) -> {
            setDescriptionsCondition(newVal);
        });

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
            images.setImages(loadAllFilesJPGFromDirectory(choosedFolder));
            this.createButtonsByImage(images.getImages(), vBox);
            this.selecImageToSave(images.getImages());
            root.getChildren().add(vBox);
        });

        loadWebCam.setOnAction(a -> {
            setTimeInS(timeInSTextField2.getText());
            WebcamCapture gs = new WebcamCapture(this.timeVideoInS, this.folderToSave.getPath());
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
        primaryStage.setTitle("Home");
        // add scene to the stage
        primaryStage.setScene(scene);

        // make the stage visible
        primaryStage.show();
    }

    public Integer getPercentCondion() {
        return percentCondion;
    }

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

    public void setDescriptionsCondition(String descriptionsCondition) {
        // Convert String Array to List
        this.descriptionsCondition = Arrays.asList(descriptionsCondition.split(","));
    }

    public File getFolderToSave() {
        return folderToSave;
    }

    public void setFolderToSave(File folderToSave) {
        this.folderToSave = folderToSave;
    }

    public List<MyImage> loadAllFilesJPGFromDirectory(File folder) {
        File[] listOfFiles = folder.listFiles();
        List<MyImage> myImages = new ArrayList<>();
        for (int i = 0; i < listOfFiles.length; i++) {

            if (listOfFiles[i].isFile()) {
                MyImage myImage = new MyImage(folder + "/", listOfFiles[i].getName());

                myImages.add(myImage);

            } else if (listOfFiles[i].isDirectory()) {
                System.out.println("Directory " + listOfFiles[i].getName());
            }

        }
        return myImages;
    }

    public void setTimeInS(String time) {
        Integer foo;
        try {
            foo = Integer.parseInt(time);
        } catch (NumberFormatException e) {
            foo = MAX_PERCENT;
        }
        this.timeVideoInS = foo;
    }


    public void selecImageToSave(List<MyImage> img) {
        for (int i = 0; i < img.size(); i++) {

            try {
                MyImage myImage = img.get(i);
                Boolean ifDescInArray = this.descriptionsCondition.contains(myImage.getDescription());
                if (this.percentCondion < myImage.getMaxPercent())
                    if (this.descriptionsCondition == null)
                        this.saveFileAs(myImage);
                    else if (ifDescInArray)
                        this.saveFileAs(myImage);
            } catch (IOException e) {
                System.out.println(e);
            }
        }
    }

    public void createButtonsByImage(List<MyImage> img, VBox vBox) {
        for (int i = 0; i < img.size(); i++) {
            Button b = new Button(img.get(i).getDescription());
            Integer index = i;
            b.setOnAction(a -> {
                createNewAlertWithImageDescr(img.get(index).getFilePath(), img.get(index).getDescription());
            });
            vBox.getChildren().add(b);
        }
    }

    public void saveFileAs(MyImage myImage) throws IOException {
        String filePath = this.folderToSave.getPath() + "/" + myImage.getFileName();
        BufferedImage bImage = ImageIO.read(new File(myImage.getFilePath()));
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ImageIO.write(bImage, "jpg", bos);
        byte[] data = bos.toByteArray();
        ByteArrayInputStream bis = new ByteArrayInputStream(data);
        BufferedImage bImage2 = ImageIO.read(bis);
        ImageIO.write(bImage2, "jpg", new File(filePath));
        System.out.println("image " + myImage.getFileName() + " created as :" + filePath);
    }

    public void createNewAlertWithImageDescr(String path, String desc) {
        try {
            Image image = new Image(new FileInputStream(path));
            ImageView imageView = new ImageView(image);
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


            //Creating a scene object
            Scene scene = new Scene(sp, 600, 400);

            //Setting title to the Stage
            newWindow.setTitle(path);

            //Adding scene to the stage
            newWindow.setScene(scene);

            //Displaying the contents of the stage
            newWindow.show();
        } catch (IOException e) {
            System.out.println(e);
        }
    }


    public File chooseSpecificFolder(Stage primaryStage) {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Choose folder with JPG");
        File defaultDirectory = new File("/home/francesco/Documents/codingFactory/javaAv/WebCam");
        chooser.setInitialDirectory(defaultDirectory);
        File selectedDirectory = chooser.showDialog(primaryStage);
        return selectedDirectory;
    }
}
