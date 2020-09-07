package sample;

//********************************************************************//
//                             Main class                             //
//                                                                    //
//     This file's job is to:                                         //
//         - Call functions to check and/or create                    //
//           the CryptoAlert directory and file                       //
//         - To start up and help display main screen                 //
//                                                                    //
//                                                                    //
//********************************************************************//



import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;


public class Main extends Application {

    //AlertData is class that builds and handles the alert data being created and deleted
    private final FileManager fileManager = new FileManager();

    //MainController controls the first window the user sees
    private createMainController mainController = new createMainController();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        //Loading FXML file
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("Main.fxml"));
        Parent root = loader.load();

        //Gives full control to createMainController class
        mainController = loader.getController();

        //Display FXML file on screen
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Crypto Alerts");

        //Icon image setup
        Image icon = new Image("BC_Logo_.png");
        stage.getIcons().add(icon);
        stage.show();

        //Checking if directory (CryptoAlerts) exists
        fileManager.checkAlertDirectoryExists();

        mainController.startUp();
    }

    @Override
    public void stop() { mainController.closeThreads();}
}
