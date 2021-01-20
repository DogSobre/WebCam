import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ImagesApp extends Application {

    private List<Image> images;

    @Override
    public void start(Stage primaryStage) throws Exception {

        // the root of the scene shown in the main window
        StackPane root = new StackPane();

        // create a button with specified text
        Button loadFoler = new Button("Load folder images'");
        // set a handler that is executed when the user activates the button
        // e.g. by clicking it or pressing enter while it's focused
        loadFoler.setOnAction(e -> {
            File choosedFolder = chooseSpecificFolder(primaryStage);
            this.images = loadAllFilesJPGFromDirectory(choosedFolder);

            for (int i = 0; i < this.images.size(); i++) {
                System.out.println(this.images.get(i).getFilePath());
                Button btn = new Button(this.images.get(i).getFilePath());
                btn.setLayoutX(250);
                btn.setLayoutY(10 + i * 10);
                Integer index = i;

                btn.setOnAction(a -> {
                    System.out.println(this.images.get(index));
                    //Open information dialog that says hello
                    HashMap pathAndName = this.images.get(index).getMapPathDescription();
                    System.out.println(pathAndName);
                    // Alert alert = new Alert(Alert.AlertType.INFORMATION, path);
                    //alert.showAndWait();
                });
                // add button as child of the root
                root.getChildren().add(btn);
            }

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

    public List<Image> loadAllFilesJPGFromDirectory(File folder) {
        File[] listOfFiles = folder.listFiles();
        List<Image> images = new ArrayList<>();
        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                Image image = new Image(folder + "/" + listOfFiles[i].getName());
                images.add(image);

            } else if (listOfFiles[i].isDirectory()) {
                System.out.println("Directory " + listOfFiles[i].getName());
            }

        }
        return images;
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
