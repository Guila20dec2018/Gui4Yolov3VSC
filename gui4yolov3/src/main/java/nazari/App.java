package nazari;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;
    private static String pathToDarknet;

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("primary"), 640, 480);
        stage.setTitle("GUI - YOLOV3");
        stage.setScene(scene);
        stage.show();
    }

    static void setRoot(String fxml) throws IOException {
        if (fxml.equalsIgnoreCase("secondary")) {
            Stage stage = (Stage) scene.getWindow();
            stage.setWidth(960);
            stage.setHeight(640);
        }
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    static void setDarknetPath(String darknetPath) {
        pathToDarknet = darknetPath;
    }

    static String getDarknetPath() {
        return pathToDarknet;
    }

    public static void main(String[] args) {
        launch(args);
    }

}