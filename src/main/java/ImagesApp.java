import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ImagesApp extends Application {

    private Images images = new Images();


    @Override
    public void start(Stage primaryStage) throws Exception {
        VBox vBox = new VBox();
        // the root of the scene shown in the main window
        StackPane root = new StackPane();

        // create a button with specified text
        Button loadFoler = new Button("Load new folder images'");
        // set a handler that is executed when the user activates the button
        // e.g. by clicking it or pressing enter while it's focused
        loadFoler.setOnAction(e -> {
            File choosedFolder = chooseSpecificFolder(primaryStage);
            images.setImages(loadAllFilesJPGFromDirectory(choosedFolder));
            this.createButtonsByImage(images.getImages(), vBox);
            root.getChildren().add(vBox);

        });

        // add button as child of the root
        root.getChildren().add(loadFoler);
        // create a scene specifying the root and the size
        Scene scene = new Scene(root, 500, 300);
        primaryStage.setTitle("Home");
        // add scene to the stage
        primaryStage.setScene(scene);

        // make the stage visible
        primaryStage.show();
    }

    public List<MyImage> loadAllFilesJPGFromDirectory(File folder) {
        File[] listOfFiles = folder.listFiles();
        List<MyImage> myImages = new ArrayList<>();
        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                MyImage myImage = new MyImage(folder + "/" + listOfFiles[i].getName());
                myImages.add(myImage);

            } else if (listOfFiles[i].isDirectory()) {
                System.out.println("Directory " + listOfFiles[i].getName());
            }

        }
        return myImages;
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

    public File chooseSpecificFolder(Stage primaryStage) {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Choose folder with JPG");
        File defaultDirectory = new File("/home/francesco/Documents/codingFactory/javaAv/WebCam");
        chooser.setInitialDirectory(defaultDirectory);
        File selectedDirectory = chooser.showDialog(primaryStage);
        return selectedDirectory;
    }
}
