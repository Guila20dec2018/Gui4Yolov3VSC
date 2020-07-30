package nazari;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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

    /**
     * Detect image
     */
    @FXML // fx:id="dogImgCheckBox"
    private CheckBox dogImgCheckBox; // Value injected by FXMLLoader

    @FXML // fx:id="eagleImgCheckBox"
    private CheckBox eagleImgCheckBox; // Value injected by FXMLLoader

    @FXML // fx:id="chooseDetectImgButton"
    private Button chooseDetectImgButton; // Value injected by FXMLLoader

    @FXML // fx:id="pathToDetectImgTextField"
    private TextField pathToDetectImgTextField; // Value injected by FXMLLoader

    private File detectImgFile;


    private final StringProperty cfgStringProperty = new SimpleStringProperty();
    private final StringProperty weightsStringProperty = new SimpleStringProperty();
    private final StringProperty detectImgStringProperty = new SimpleStringProperty();


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
                    else if (subDirectory.equalsIgnoreCase("data")) {
                        detectImgFile = new File(dir.getAbsolutePath().concat("/" + fileName));
                        System.out.println("Pointer to detect image file: " + detectImgFile.getAbsolutePath());
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
            cfgStringProperty.set(selectedFilePath);
            //pathToCfgFileTextField.setText(selectedFilePath);
            cfgFile = selectedFile;
            System.out.println("Pointer to cfg file: " + cfgFile.getAbsolutePath());
            //System.out.println(selectedFilePath);
            yolov3tinyCfgCheckBox.setSelected(false);
            yolov3CfgCheckBox.setSelected(false);
        }

    }
    
    private void getTextOnInputChangedCfgTextField() {
        String typedFile = cfgStringProperty.get();
        cfgFile = new File(typedFile);
        System.out.println("Pointer to weigths file: " + cfgFile.getAbsolutePath());
        yolov3CfgCheckBox.setSelected(false);
        yolov3tinyCfgCheckBox.setSelected(false);
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
            weightsStringProperty.set(selectedFilePath);
            //pathToWeightsFileTextField.setText(selectedFilePath);
            weigthsFile = selectedFile;
            System.out.println("Pointer to weights file: " + weigthsFile.getAbsolutePath());
            //System.out.println(selectedFilePath);
            yolov3WeigthsCheckBox.setSelected(false);
            darknetConv74CheckBox.setSelected(false);
        }
    }
    
    private void getTextOnInputChangedWeightsTextField() {
        String typedFile = weightsStringProperty.get();
        weigthsFile = new File(typedFile);
        System.out.println("Pointer to weigths file: " + weigthsFile.getAbsolutePath());
        yolov3WeigthsCheckBox.setSelected(false);
        darknetConv74CheckBox.setSelected(false);
    }


    @FXML
    void changeStateDogImgCheckBox(ActionEvent event) {
        if (dogImgCheckBox.isSelected()) {
            //look for the file in .../darknet/cfg
            if (!lookingForFile("dog.jpg", "data")) {
                Alert alert = new Alert(AlertType.WARNING, "Assicurati di avere il file dog.jpg sotto .../darknet/data!");
                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    //formatSystem();
                    dogImgCheckBox.setSelected(false);
                }
            }
            else {
                eagleImgCheckBox.setSelected(false);
                pathToDetectImgTextField.setText("");
            }
        }
    }

    @FXML
    void changeStateEagleImgCheckBox(ActionEvent event) {
        if (eagleImgCheckBox.isSelected()) {
            //look for the file in .../darknet/cfg
            if (!lookingForFile("eagle.jpg", "data")) {
                Alert alert = new Alert(AlertType.WARNING, "Assicurati di avere il file eagle.jpg sotto .../darknet/data!");
                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    //formatSystem();
                    eagleImgCheckBox.setSelected(false);
                }
            }
            else {
                dogImgCheckBox.setSelected(false);
                pathToDetectImgTextField.setText("");
            }
        }
    }

    @FXML
    void handleChooseDetectImgButtonClick(ActionEvent event) {
        Stage stage = (Stage) containerTabPane.getScene().getWindow();

        final FileChooser fileChooser = new FileChooser();
        configureFileChooser(fileChooser, "prova (Immagine da Individuare oggetti)");
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            String selectedFilePath = selectedFile.getAbsolutePath();
            detectImgStringProperty.set(selectedFilePath);
            //pathToDetectImgTextField.setText(selectedFilePath);
            detectImgFile = selectedFile;
            System.out.println("Pointer to detect image file: " + detectImgFile.getAbsolutePath());
            //System.out.println(selectedFilePath);
            dogImgCheckBox.setSelected(false);
            eagleImgCheckBox.setSelected(false);
        }
    }

    private void getTextOnInputChangedImgTextField() {
        //System.out.println(detectImgStringProperty.get());
        String typedFile = detectImgStringProperty.get();
        detectImgFile = new File(typedFile);
        System.out.println("Pointer to detect image file: " + detectImgFile.getAbsolutePath());
        dogImgCheckBox.setSelected(false);
        eagleImgCheckBox.setSelected(false);
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

        assert dogImgCheckBox != null : "fx:id=\"dogImgCheckBox\" was not injected: check your FXML file 'secondary.fxml'.";
        assert eagleImgCheckBox != null : "fx:id=\"eagleImgCheckBox\" was not injected: check your FXML file 'secondary.fxml'.";
        assert chooseDetectImgButton != null : "fx:id=\"chooseDetectImgButton\" was not injected: check your FXML file 'secondary.fxml'.";
        assert pathToDetectImgTextField != null : "fx:id=\"pathToDetectImgTextField\" was not injected: check your FXML file 'secondary.fxml'.";

        directoryToDarknet = new File(App.getDarknetPath());
        pathToCfgFileTextField.textProperty().bindBidirectional(cfgStringProperty);
        pathToWeightsFileTextField.textProperty().bindBidirectional(weightsStringProperty);
        pathToDetectImgTextField.textProperty().bindBidirectional(detectImgStringProperty);

        pathToCfgFileTextField.focusedProperty().addListener(new ChangeListener<Boolean>(){
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                // Auto-generated method stub
                if (!newValue) {
                    System.out.println("Focusing out from pathToWeightsFileTextfield!");
                    if (cfgStringProperty.get() == null || cfgStringProperty.get().equalsIgnoreCase("")) {
                        System.out.println("pathToCfgFileTextField invalid path");
                        cfgFile = null; //to cover the case the user erase the text in textField
                    }
                    else if (cfgFile != null && cfgFile.getAbsolutePath().equalsIgnoreCase(cfgStringProperty.get())) {
                        System.out.println("Path pathToCfgFileTextField do not change");
                    }
                    else {
                        getTextOnInputChangedCfgTextField();
                    }
                }
            }
            
        });

        pathToWeightsFileTextField.focusedProperty().addListener(new ChangeListener<Boolean>(){
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                // Auto-generated method stub
                if (!newValue) {
                    System.out.println("Focusing out from pathToWeightsFileTextfield!");
                    if (weightsStringProperty.get() == null || weightsStringProperty.get().equalsIgnoreCase("")) {
                        System.out.println("pathToWeightsFileTextField invalid path");
                        weigthsFile = null; //to cover the case the user erase the text in textField
                    }
                    else if (weigthsFile != null && weigthsFile.getAbsolutePath().equalsIgnoreCase(weightsStringProperty.get())) {
                        System.out.println("Path pathToWeightsFileTextField do not change");
                    }
                    else {
                        getTextOnInputChangedWeightsTextField();
                    }
                }
            }
            
        });

        pathToDetectImgTextField.focusedProperty().addListener(new ChangeListener<Boolean>(){
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                // Auto-generated method stub
                if (!newValue) {
                    System.out.println("Focusing out from pathToDetectImgTextField!");
                    if (detectImgStringProperty.get() == null || detectImgStringProperty.get().equalsIgnoreCase("")) {
                        System.out.println("pathToDetectImgTextField invalid path");
                    }
                    else if (detectImgFile != null && detectImgFile.getAbsolutePath().equalsIgnoreCase(detectImgStringProperty.get())) {
                        System.out.println("Path pathToDetectImgTextField do not change");
                    }
                    else {
                        getTextOnInputChangedImgTextField();
                    }
                }
            }
            
        });

    }

    @FXML
    private void switchToPrimary() throws IOException {
        App.setRoot("primary");
    }
}