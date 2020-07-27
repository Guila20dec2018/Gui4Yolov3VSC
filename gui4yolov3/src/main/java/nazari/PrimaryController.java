package nazari;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.Optional;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;


public class PrimaryController {

    @FXML TitledPane primaryTitledPane;

    @FXML TextField chosenPathToDarknetTextField;

    @FXML TextField directoryToDownloadDarknet;

    private boolean res = false;
    private File dir = null;

    @FXML
    private void switchToSecondary() throws IOException {
        App.setRoot("secondary");
        System.out.println("In the button handler!");
    }

   @FXML
    private void browseLocalDarknetDirectory(ActionEvent event) {
        //Path path;

        Stage stage = (Stage) primaryTitledPane.getScene().getWindow();

        final DirectoryChooser directoryChooser = new DirectoryChooser();
        final File selectedDirectory = directoryChooser.showDialog(stage);
        if (selectedDirectory != null) {
            String dir = selectedDirectory.getAbsolutePath();
            chosenPathToDarknetTextField.setText(dir);
            res = true;
            //System.out.println(dir);
        }

        //System.out.println("Eseguito metodo browseLocalDarknetDirectory");
    }

    @FXML
    private void downloadDarknet() {
        //download darknet from https://github.com/pjreddie/darknet if installed git or 
        //https://github.com/pjreddie/darknet/archive/master.zip if installed unzip or something similary

        if (dir != null) {
            System.out.println("Downloading darknet...");
            String s;
            Process p;
            try {
                p = Runtime.getRuntime().exec("git clone https://github.com/pjreddie/darknet", null, dir);
                BufferedReader br = new BufferedReader(
                    new InputStreamReader(p.getInputStream()));
                while ((s = br.readLine()) != null)
                    System.out.println("line: " + s);
                p.waitFor();
                System.out.println ("exit: " + p.exitValue());
                p.destroy();
            } catch (Exception e) {
                System.out.println(e.getStackTrace());
                e.printStackTrace();
            }
        }
        else {
            Alert alert = new Alert(AlertType.WARNING, "Indicare il cammino dove si desidera scaricare darknet!");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                //formatSystem();
                directoryToDownloadDarknet.requestFocus();
            } 
        }
    }

    @FXML
    private void browseDirectoryToDownlaodDarknet() {
        Stage stage = (Stage) primaryTitledPane.getScene().getWindow();

        final DirectoryChooser directoryChooser = new DirectoryChooser();
        final File selectedDirectory = directoryChooser.showDialog(stage);
        if (selectedDirectory != null) {
            String directory = selectedDirectory.getAbsolutePath();
            directoryToDownloadDarknet.setText(directory);
            dir = selectedDirectory;
        }
    }

    @FXML
    private void checkAndNextScreen() throws IOException {
        if (res) {
            App.setRoot("secondary");
        }
        else {
            Alert alert = new Alert(AlertType.WARNING, "Per procedere e' necessario avere darknet!");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                //formatSystem();
            }
        }
    }

    @FXML
    private void close() {
        Stage stage = (Stage) primaryTitledPane.getScene().getWindow();
        stage.close();
    }

}
