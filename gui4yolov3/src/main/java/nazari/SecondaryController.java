package nazari;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class SecondaryController {


    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="containerTabPane"
    private TabPane containerTabPane; // Value injected by FXMLLoader

    @FXML // fx:id="yolov3CfgCheckBox"
    private CheckBox yolov3CfgCheckBox; // Value injected by FXMLLoader

    @FXML // fx:id="yolo3tinyCfgCheckBox"
    private CheckBox yolov3tinyCfgCheckBox; // Value injected by FXMLLoader

    @FXML // fx:id="chooseCfgFileButton"
    private Button chooseCfgFileButton; // Value injected by FXMLLoader

    @FXML // fx:id="pathToCfgFileTextField"
    private TextField pathToCfgFileTextField; // Value injected by FXMLLoader

    @FXML
    void changeStateYolov3CfgCheckBox(ActionEvent event) {
        if (yolov3CfgCheckBox.isSelected()) {
            yolov3tinyCfgCheckBox.setSelected(false);
            pathToCfgFileTextField.setText("");
        }
    }

    @FXML
    void changeStateYolov3tinyCfgCheckBox(ActionEvent event) {
        if (yolov3tinyCfgCheckBox.isSelected()) {
            yolov3CfgCheckBox.setSelected(false);
            pathToCfgFileTextField.setText("");
        }
    }

    private static void configureFileChooser(final FileChooser fileChooser){                           
        fileChooser.setTitle("View Pictures");
        fileChooser.setInitialDirectory(
            new File(System.getProperty("user.home"))
        ); 
    }

    @FXML
    void handleChooseCfgFileButtonClick(ActionEvent event) {
        Stage stage = (Stage) containerTabPane.getScene().getWindow();

        final FileChooser fileChooser = new FileChooser();
        //configureFileChooser(fileChooser);
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            String selectedFilePath = selectedFile.getAbsolutePath();
            pathToCfgFileTextField.setText(selectedFilePath);
            //System.out.println(selectedFilePath);
            yolov3tinyCfgCheckBox.setSelected(false);
            yolov3CfgCheckBox.setSelected(false);
        }

    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert yolov3CfgCheckBox != null : "fx:id=\"yolov3CfgCheckBox\" was not injected: check your FXML file 'secondary.fxml'.";
        assert yolov3tinyCfgCheckBox != null : "fx:id=\"yolo3tinyCfgCheckBox\" was not injected: check your FXML file 'secondary.fxml'.";
        assert chooseCfgFileButton != null : "fx:id=\"chooseCfgFileButton\" was not injected: check your FXML file 'secondary.fxml'.";
        assert pathToCfgFileTextField != null : "fx:id=\"pathToCfgFileTextField\" was not injected: check your FXML file 'secondary.fxml'.";

    }

    @FXML
    private void switchToPrimary() throws IOException {
        App.setRoot("primary");
    }
}