package nazari;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
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

    @FXML // fx:id="yolov3tinyCfgCheckBox"
    private CheckBox yolov3tinyCfgCheckBox; // Value injected by FXMLLoader

    @FXML // fx:id="chooseCfgFileButton"
    private Button chooseCfgFileButton; // Value injected by FXMLLoader

    @FXML // fx:id="pathToCfgFileTextField"
    private TextField pathToCfgFileTextField; // Value injected by FXMLLoader

    private File directoryToDarknet;
    private File cfgFile;

    private boolean lookingForCfgFile(String cfgFileName) {
        boolean find = false;

            String s;
            Process p;
            try {
                File pathToCfgDirectory = new File(directoryToDarknet.getAbsolutePath().concat("/cfg"));
                p = Runtime.getRuntime().exec("ls", null, pathToCfgDirectory);
                BufferedReader br = new BufferedReader(
                    new InputStreamReader(p.getInputStream()));
                while ((s = br.readLine()) != null) {
                    if (s.equalsIgnoreCase(cfgFileName)) {
                        find = true;
                        //if find the file save the "pointer"
                        cfgFile = new File(pathToCfgDirectory.getAbsolutePath().concat("/" + cfgFileName));
                        System.out.println("Pointer to cfg file: " + cfgFile.getAbsolutePath());
                    }
                    System.out.println("line: " + s);
                }
                p.waitFor();
                System.out.println ("exit: " + p.exitValue());
                p.destroy();
            } catch (Exception e) {
                System.out.println(e.getStackTrace());
                e.printStackTrace();
            }

        return find;
    }
    
    @FXML
    void changeStateYolov3CfgCheckBox(ActionEvent event) {
        if (yolov3CfgCheckBox.isSelected()) {
            yolov3tinyCfgCheckBox.setSelected(false);
            pathToCfgFileTextField.setText("");

            //look for the file in .../darknet/cfg
            if (!lookingForCfgFile("yolov3.cfg")) {
                Alert alert = new Alert(AlertType.WARNING, "Assicurati di avere il file yolov3.cfg sotto .../darknet/cfg!");
                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    //formatSystem();
                    yolov3CfgCheckBox.setSelected(false);
                }
            }
        }
    }

    @FXML
    void changeStateYolov3tinyCfgCheckBox(ActionEvent event) {
        if (yolov3tinyCfgCheckBox.isSelected()) {
            yolov3CfgCheckBox.setSelected(false);
            pathToCfgFileTextField.setText("");

            //look for the file in .../darknet/cfg
            if (!lookingForCfgFile("yolov3-tiny.cfg")) {
                Alert alert = new Alert(AlertType.WARNING, "Assicurati di avere il file yolov3-tiny.cfg sotto .../darknet/cfg!");
                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    //formatSystem();
                    yolov3tinyCfgCheckBox.setSelected(false);
                }
            }
        }
    }

    private static void configureFileChooser(final FileChooser fileChooser){
        fileChooser.setTitle("Scegli il file di configurazione");
        fileChooser.setInitialDirectory(
            new File(System.getProperty("user.home"))
        );
    }

    @FXML
    void handleChooseCfgFileButtonClick(ActionEvent event) {
        Stage stage = (Stage) containerTabPane.getScene().getWindow();

        final FileChooser fileChooser = new FileChooser();
        configureFileChooser(fileChooser);
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            String selectedFilePath = selectedFile.getAbsolutePath();
            pathToCfgFileTextField.setText(selectedFilePath);
            cfgFile = selectedFile;
            System.out.println("Pointer to cfg file: " + cfgFile.getAbsolutePath());
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

        directoryToDarknet = new File(App.getDarknetPath());

    }

    @FXML
    private void switchToPrimary() throws IOException {
        App.setRoot("primary");
    }
}