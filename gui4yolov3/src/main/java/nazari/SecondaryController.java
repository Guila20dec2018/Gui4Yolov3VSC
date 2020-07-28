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

    private File directoryToDarknet;

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="containerTabPane"
    private TabPane containerTabPane; // Value injected by FXMLLoader

    /**
     * Config file
     */
    @FXML // fx:id="yolov3CfgCheckBox"
    private CheckBox yolov3CfgCheckBox; // Value injected by FXMLLoader

    @FXML // fx:id="yolov3tinyCfgCheckBox"
    private CheckBox yolov3tinyCfgCheckBox; // Value injected by FXMLLoader

    @FXML // fx:id="chooseCfgFileButton"
    private Button chooseCfgFileButton; // Value injected by FXMLLoader

    @FXML // fx:id="pathToCfgFileTextField"
    private TextField pathToCfgFileTextField; // Value injected by FXMLLoader
    
    private File cfgFile;

    /**
     * Weigths file
     */
    @FXML // fx:id="yolov3WeigthsCheckBox"
    private CheckBox yolov3WeigthsCheckBox; // Value injected by FXMLLoader

    @FXML // fx:id="darknetConv74CheckBox"
    private CheckBox darknetConv74CheckBox; // Value injected by FXMLLoader

    @FXML // fx:id="chooseWeightsFileButton"
    private Button chooseWeightsFileButton; // Value injected by FXMLLoader

    @FXML // fx:id="pathToWeightsFileTextField"
    private TextField pathToWeightsFileTextField; // Value injected by FXMLLoader

    private File weigthsFile;


    private boolean lookingForFile(String fileName, String subDirectory) {
        boolean find = false;

        String s;
        Process p;
        try {
            File dir = new File(directoryToDarknet.getAbsolutePath().concat("/" + subDirectory));
            p = Runtime.getRuntime().exec("ls", null, dir);
            BufferedReader br = new BufferedReader(
                new InputStreamReader(p.getInputStream()));
            while ((s = br.readLine()) != null) {
                if (s.equalsIgnoreCase(fileName)) {
                    find = true;
                    //if find the file save the "pointer"
                    if (subDirectory.equalsIgnoreCase("cfg")) {
                        cfgFile = new File(dir.getAbsolutePath().concat("/" + fileName));
                        System.out.println("Pointer to cfg file: " + cfgFile.getAbsolutePath());
                    }
                    else if (subDirectory.equalsIgnoreCase("")) {
                        weigthsFile = new File(dir.getAbsolutePath().concat("/" + fileName));
                        System.out.println("Pointer to weights file: " + weigthsFile.getAbsolutePath());
                    }
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
            //look for the file in .../darknet/cfg
            if (!lookingForFile("yolov3.cfg", "cfg")) {
                Alert alert = new Alert(AlertType.WARNING, "Assicurati di avere il file yolov3.cfg sotto .../darknet/cfg!");
                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    //formatSystem();
                    yolov3CfgCheckBox.setSelected(false);
                }
            }
            else {
                yolov3tinyCfgCheckBox.setSelected(false);
                pathToCfgFileTextField.setText("");
            }
        }
    }

    @FXML
    void changeStateYolov3tinyCfgCheckBox(ActionEvent event) {
        if (yolov3tinyCfgCheckBox.isSelected()) {
            //look for the file in .../darknet/cfg
            if (!lookingForFile("yolov3-tiny.cfg", "cfg")) {
                Alert alert = new Alert(AlertType.WARNING, "Assicurati di avere il file yolov3-tiny.cfg sotto .../darknet/cfg!");
                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    //formatSystem();
                    yolov3tinyCfgCheckBox.setSelected(false);
                }
            }
            else {
                yolov3CfgCheckBox.setSelected(false);
                pathToCfgFileTextField.setText("");
            }
        }
    }

    private static void configureFileChooser(final FileChooser fileChooser, String fileType){
        fileChooser.setTitle("Scegli il file di " + fileType);
        fileChooser.setInitialDirectory(
            new File(System.getProperty("user.home"))
        );
    }

    @FXML
    void handleChooseCfgFileButtonClick(ActionEvent event) {
        Stage stage = (Stage) containerTabPane.getScene().getWindow();

        final FileChooser fileChooser = new FileChooser();
        configureFileChooser(fileChooser, "configurazione");
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


    @FXML
    void changeStateYolov3WeigthsCheckBox(ActionEvent event) {
        if (yolov3WeigthsCheckBox.isSelected()) {
            //look for the file in .../darknet/cfg
            if (!lookingForFile("yolov3.weights", "")) {
                Alert alert = new Alert(AlertType.WARNING, "Assicurati di avere il file yolov3.weights sotto .../darknet!");
                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    //formatSystem();
                    yolov3WeigthsCheckBox.setSelected(false);
                }
            }
            else {
                darknetConv74CheckBox.setSelected(false);
                pathToWeightsFileTextField.setText("");
            }
        }
    }

    @FXML
    void changeStateDarknetConv74CheckBox(ActionEvent event) {
        if (darknetConv74CheckBox.isSelected()) {
            //look for the file in .../darknet/cfg
            if (!lookingForFile("darknet53.conv.74", "")) {
                Alert alert = new Alert(AlertType.WARNING, "Assicurati di avere il file darknet53.conv.74 sotto .../darknet!");
                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    //formatSystem();
                    darknetConv74CheckBox.setSelected(false);
                }
            }
            else {
                yolov3WeigthsCheckBox.setSelected(false);
                pathToWeightsFileTextField.setText("");
            }
        }
    }

    @FXML
    void handleChooseWeightsFileButtonClick(ActionEvent event) {
        Stage stage = (Stage) containerTabPane.getScene().getWindow();

        final FileChooser fileChooser = new FileChooser();
        configureFileChooser(fileChooser, "pesi");
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            String selectedFilePath = selectedFile.getAbsolutePath();
            pathToWeightsFileTextField.setText(selectedFilePath);
            weigthsFile = selectedFile;
            System.out.println("Pointer to weights file: " + weigthsFile.getAbsolutePath());
            //System.out.println(selectedFilePath);
            yolov3WeigthsCheckBox.setSelected(false);
            darknetConv74CheckBox.setSelected(false);
        }
    }
    
    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert yolov3CfgCheckBox != null : "fx:id=\"yolov3CfgCheckBox\" was not injected: check your FXML file 'secondary.fxml'.";
        assert yolov3tinyCfgCheckBox != null : "fx:id=\"yolo3tinyCfgCheckBox\" was not injected: check your FXML file 'secondary.fxml'.";
        assert chooseCfgFileButton != null : "fx:id=\"chooseCfgFileButton\" was not injected: check your FXML file 'secondary.fxml'.";
        assert pathToCfgFileTextField != null : "fx:id=\"pathToCfgFileTextField\" was not injected: check your FXML file 'secondary.fxml'.";
        
        assert yolov3WeigthsCheckBox != null : "fx:id=\"yolov3WeigthsCheckBox\" was not injected: check your FXML file 'secondary.fxml'.";
        assert darknetConv74CheckBox != null : "fx:id=\"darknetConv74CheckBox\" was not injected: check your FXML file 'secondary.fxml'.";
        assert chooseWeightsFileButton != null : "fx:id=\"chooseWeightsFileButton\" was not injected: check your FXML file 'secondary.fxml'.";
        assert pathToWeightsFileTextField != null : "fx:id=\"pathToWeightsFileTextField\" was not injected: check your FXML file 'secondary.fxml'.";

        directoryToDarknet = new File(App.getDarknetPath());

    }

    @FXML
    private void switchToPrimary() throws IOException {
        App.setRoot("primary");
    }
}