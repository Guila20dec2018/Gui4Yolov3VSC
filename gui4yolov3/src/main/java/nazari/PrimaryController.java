package nazari;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Optional;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;


public class PrimaryController {

    @FXML TitledPane primaryTitledPane;

    @FXML TextField chosenPathToDarknetTextField;

    @FXML TextField directoryToDownloadDarknetTextField;

    private boolean res = false;
    private File dir = null;
    private static StringProperty localDarknetDirectoryStringProperty = new SimpleStringProperty();
    private static StringProperty directoryToDownloadDarknetStringProperty = new SimpleStringProperty();

    @FXML
    private void switchToSecondary() throws IOException {
        App.setRoot("secondary");
        System.out.println("In the button handler!");
    }

   @FXML
    private void browseLocalDarknetDirectory(ActionEvent event) {
        Stage stage = (Stage) primaryTitledPane.getScene().getWindow();

        final DirectoryChooser directoryChooser = new DirectoryChooser();
        final File selectedDirectory = directoryChooser.showDialog(stage);
        if (selectedDirectory != null) {
            String directory = selectedDirectory.getAbsolutePath();
            localDarknetDirectoryStringProperty.set(directory);
            //chosenPathToDarknetTextField.setText(directory);
            dir = selectedDirectory;
            res = true;
            //System.out.println(dir);
        }

        //System.out.println("Eseguito metodo browseLocalDarknetDirectory");
    }

    private void getTextOnInputChangedLocalDarknetDirectoryTextField() {
        //System.out.println(localDarknetDirectoryStringProperty.get());
        String typedFile = localDarknetDirectoryStringProperty.get();
        dir = new File(typedFile);
        System.out.println("Pointer to local darknet directory: " + dir.getAbsolutePath());
        //System.out.println(selectedFilePath);
        directoryToDownloadDarknetTextField.setText("");
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
                res = true;
                File directoryToDarknet = new File(dir.getAbsolutePath().concat("/darknet"));
                dir = directoryToDarknet;
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
                directoryToDownloadDarknetTextField.requestFocus();
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
            directoryToDownloadDarknetStringProperty.set(directory);
            //directoryToDownloadDarknetTextField.setText(directory);
            dir = selectedDirectory;
        }
    }

    private void getTextOnInputChangedDirectoryToDownloadDarknetTextField() {
        //System.out.println(localDarknetDirectoryStringProperty.get());
        String typedFile = directoryToDownloadDarknetStringProperty.get();
        dir = new File(typedFile);
        System.out.println("Pointer to directory to download darknet: " + dir.getAbsolutePath());
        //System.out.println(selectedFilePath);
        chosenPathToDarknetTextField.setText("");
    }

    @FXML
    private void checkAndNextScreen() throws IOException {
        if (res) {
            System.out.println("Path to darknet: " + dir.getAbsolutePath());
            App.setDarknetPath(dir.getAbsolutePath());
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

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert primaryTitledPane != null : "fx:id=\"primaryTitledPane\" was not injected: check your FXML file 'primary.fxml'.";
        assert chosenPathToDarknetTextField != null : "fx:id=\"chosenPathToDarknetTextField\" was not injected: check your FXML file 'primary.fxml'.";
        assert directoryToDownloadDarknetTextField != null : "fx:id=\"directoryToDownloadDarknetTextField\" was not injected: check your FXML file 'primary.fxml'.";

        chosenPathToDarknetTextField.textProperty().bindBidirectional(localDarknetDirectoryStringProperty);
        directoryToDownloadDarknetTextField.textProperty().bindBidirectional(directoryToDownloadDarknetStringProperty);

        chosenPathToDarknetTextField.focusedProperty().addListener(new ChangeListener<Boolean>(){
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                // Auto-generated method stub
                if (!newValue) {
                    System.out.println("Focusing out from chosenPathToDarknetTextField!");
                    if (localDarknetDirectoryStringProperty.get() == null || localDarknetDirectoryStringProperty.get().equalsIgnoreCase("")) {
                        System.out.println("invalid path");
                    }
                    else {
                        getTextOnInputChangedLocalDarknetDirectoryTextField();
                    }
                }
            }
            
        });

        directoryToDownloadDarknetTextField.focusedProperty().addListener(new ChangeListener<Boolean>(){
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                // Auto-generated method stub
                if (!newValue) {
                    System.out.println("Focusing out from directoryToDownloadDarknetTextField!");
                    if (directoryToDownloadDarknetStringProperty.get() == null || directoryToDownloadDarknetStringProperty.get().equalsIgnoreCase("")) {
                        System.out.println("invalid path");
                    }
                    else {
                        getTextOnInputChangedDirectoryToDownloadDarknetTextField();
                    }
                }
            }
            
        });
    }

}
