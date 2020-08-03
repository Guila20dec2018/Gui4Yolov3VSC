package nazari;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
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
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class DetectController {

    private File directoryToDarknet;

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="detectVBox"
    private VBox detectVBox; // Value injected by FXMLLoader

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

    @FXML // fx:id="batchCfgTextField"
    private TextField batchCfgTextField; // Value injected by FXMLLoader

    @FXML // fx:id="subdivisionsCfgTextField"
    private TextField subdivisionsCfgTextField; // Value injected by FXMLLoader

    @FXML // fx:id="widthCfgTextField"
    private TextField widthCfgTextField; // Value injected by FXMLLoader

    @FXML // fx:id="heightCfgTextField"
    private TextField heightCfgTextField; // Value injected by FXMLLoader

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

    /**
     * Gpu opencv and threshold fields, threshold spinner and compile and run
     * buttons
     */
    @FXML // fx:id="gpuCheckBox"
    private CheckBox gpuCheckBox; // Value injected by FXMLLoader

    @FXML // fx:id="opencvCheckBox"
    private CheckBox opencvCheckBox; // Value injected by FXMLLoader

    @FXML // fx:id="cudnnCheckBox"
    private CheckBox cudnnCheckBox; // Value injected by FXMLLoader

    @FXML // fx:id="thresholdSpinner"
    private Spinner<?> thresholdSpinner; // Value injected by FXMLLoader

    @FXML // fx:id="compileButton"
    private Button compileButton; // Value injected by FXMLLoader

    @FXML // fx:id="detectButton"
    private Button detectButton; // Value injected by FXMLLoader

    private final StringProperty cfgStringProperty = new SimpleStringProperty();
    private final StringProperty weightsStringProperty = new SimpleStringProperty();
    private final StringProperty detectImgStringProperty = new SimpleStringProperty();

    private boolean needCompilation = true;

    private final StringProperty batchCfStringProperty = new SimpleStringProperty();
    private final StringProperty subdivisionsCfgStringProperty = new SimpleStringProperty();
    private final StringProperty widthCfgStringProperty = new SimpleStringProperty();
    private final StringProperty heightStringProperty = new SimpleStringProperty();

    private boolean lookingForFile(String fileName, String subDirectory) {
        boolean find = false;

        String s;
        Process p;
        try {
            File dir = new File(directoryToDarknet.getAbsolutePath().concat("/" + subDirectory));
            p = Runtime.getRuntime().exec("ls", null, dir);
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while ((s = br.readLine()) != null) {
                if (s.equalsIgnoreCase(fileName)) {
                    find = true;
                    // if find the file save the "pointer"
                    if (subDirectory.equalsIgnoreCase("cfg")) {
                        cfgFile = new File(dir.getAbsolutePath().concat("/" + fileName));
                        cfgStringProperty.setValue(cfgFile.getAbsolutePath());
                        System.out.println("Pointer to cfg file: " + cfgFile.getAbsolutePath());
                    } else if (subDirectory.equalsIgnoreCase("")) {
                        weigthsFile = new File(dir.getAbsolutePath().concat("/" + fileName));
                        weightsStringProperty.setValue(weigthsFile.getAbsolutePath());
                        System.out.println("Pointer to weights file: " + weigthsFile.getAbsolutePath());
                    } else if (subDirectory.equalsIgnoreCase("data")) {
                        detectImgFile = new File(dir.getAbsolutePath().concat("/" + fileName));
                        detectImgStringProperty.setValue(detectImgFile.getAbsolutePath());
                        System.out.println("Pointer to detect image file: " + detectImgFile.getAbsolutePath());
                    }
                }
                System.out.println("line: " + s);
            }
            p.waitFor();
            System.out.println("exit: " + p.exitValue());
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
            // look for the file in .../darknet/cfg
            if (!lookingForFile("yolov3.cfg", "cfg")) {
                Alert alert = new Alert(AlertType.WARNING,
                        "Assicurati di avere il file yolov3.cfg sotto .../darknet/cfg!");
                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    // formatSystem();
                    yolov3CfgCheckBox.setSelected(false);
                }
            } else {
                yolov3tinyCfgCheckBox.setSelected(false);
                //pathToCfgFileTextField.setText("");
                if (cfgStringProperty != null && !cfgStringProperty.getValue().equalsIgnoreCase("")) {
                    loadCfgParams(); 
                }
            }
        }
    }

    @FXML
    void changeStateYolov3tinyCfgCheckBox(ActionEvent event) {
        if (yolov3tinyCfgCheckBox.isSelected()) {
            // look for the file in .../darknet/cfg
            if (!lookingForFile("yolov3-tiny.cfg", "cfg")) {
                Alert alert = new Alert(AlertType.WARNING,
                        "Assicurati di avere il file yolov3-tiny.cfg sotto .../darknet/cfg!");
                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    // formatSystem();
                    yolov3tinyCfgCheckBox.setSelected(false);
                }
            } else {
                yolov3CfgCheckBox.setSelected(false);
                //pathToCfgFileTextField.setText("");
                if (cfgStringProperty != null && !cfgStringProperty.getValue().equalsIgnoreCase("")) {
                    loadCfgParams(); 
                }
            }
        }
    }

    private static void configureFileChooser(final FileChooser fileChooser, String fileType) {
        fileChooser.setTitle("Scegli il file di " + fileType);
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
    }

    @FXML
    void handleChooseCfgFileButtonClick(ActionEvent event) {
        Stage stage = (Stage) detectVBox.getScene().getWindow();

        final FileChooser fileChooser = new FileChooser();
        configureFileChooser(fileChooser, "configurazione");
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            String selectedFilePath = selectedFile.getAbsolutePath();
            cfgStringProperty.set(selectedFilePath);
            // pathToCfgFileTextField.setText(selectedFilePath);
            cfgFile = selectedFile;
            System.out.println("Pointer to cfg file: " + cfgFile.getAbsolutePath());
            // System.out.println(selectedFilePath);
            yolov3tinyCfgCheckBox.setSelected(false);
            yolov3CfgCheckBox.setSelected(false);
            if (cfgStringProperty != null && !cfgStringProperty.getValue().equalsIgnoreCase("")) {
                loadCfgParams(); 
            }
        }

    }

    private void getTextOnInputChangedCfgTextField() {
        String typedFile = cfgStringProperty.get();
        cfgFile = new File(typedFile);
        System.out.println("Pointer to cfg file: " + cfgFile.getAbsolutePath());
        yolov3CfgCheckBox.setSelected(false);
        yolov3tinyCfgCheckBox.setSelected(false);
        if (cfgStringProperty != null && !cfgStringProperty.getValue().equalsIgnoreCase("")) {
            loadCfgParams(); 
        }
    }

    private void loadCfgParams() {
        try {
            // input the (modified) file content to the StringBuffer "input"
            BufferedReader file = new BufferedReader(
                    new FileReader(cfgStringProperty.getValue()));
            //StringBuffer inputBuffer = new StringBuffer();
            String line;

            while ((line = file.readLine()) != null) {
                String searchMe = line;
                String findBatch = "batch=";
                String findSubdivisions = "subdivisions=";
                String findWidth = "width=";
                String findHeight = "height=";
                if (searchMe.regionMatches(0, findBatch, 0, findBatch.length())) {
                    System.out.println("Found batch. " + searchMe);
                    batchCfgTextField.setPromptText(searchMe.replaceAll("[^0-9]", ""));
                }
                else if (searchMe.regionMatches(0, findSubdivisions, 0, findSubdivisions.length())) {
                    System.out.println("Found subdivisions. " + searchMe);
                    subdivisionsCfgTextField.setPromptText(searchMe.replaceAll("[^0-9]", ""));
                }
                else if (searchMe.regionMatches(0, findWidth, 0, findWidth.length())) {
                    System.out.println("Found width. " + searchMe);
                    widthCfgTextField.setPromptText(searchMe.replaceAll("[^0-9]", ""));
                }
                else if (searchMe.regionMatches(0, findHeight, 0, findHeight.length())) {
                    System.out.println("Found height. " + searchMe);
                    heightCfgTextField.setPromptText(searchMe.replaceAll("[^0-9]", ""));
                }
                //if (!foundIt)
                    //System.out.println("No match found.");
            }
            file.close();

        } catch (Exception e) {
            System.out.println("Problem reading cfg file.");
            e.printStackTrace();
        }
    }

    // weights methods
    @FXML
    void changeStateYolov3WeigthsCheckBox(ActionEvent event) {
        if (yolov3WeigthsCheckBox.isSelected()) {
            // look for the file in .../darknet/cfg
            if (!lookingForFile("yolov3.weights", "")) {
                Alert alert = new Alert(AlertType.WARNING,
                        "Assicurati di avere il file yolov3.weights sotto .../darknet!");
                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    // formatSystem();
                    yolov3WeigthsCheckBox.setSelected(false);
                }
            } else {
                darknetConv74CheckBox.setSelected(false);
                //pathToWeightsFileTextField.setText("");
            }
        }
    }

    @FXML
    void changeStateDarknetConv74CheckBox(ActionEvent event) {
        if (darknetConv74CheckBox.isSelected()) {
            // look for the file in .../darknet/cfg
            if (!lookingForFile("darknet53.conv.74", "")) {
                Alert alert = new Alert(AlertType.WARNING,
                        "Assicurati di avere il file darknet53.conv.74 sotto .../darknet!");
                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    // formatSystem();
                    darknetConv74CheckBox.setSelected(false);
                }
            } else {
                yolov3WeigthsCheckBox.setSelected(false);
                //pathToWeightsFileTextField.setText("");
            }
        }
    }

    @FXML
    void handleChooseWeightsFileButtonClick(ActionEvent event) {
        Stage stage = (Stage) detectVBox.getScene().getWindow();

        final FileChooser fileChooser = new FileChooser();
        configureFileChooser(fileChooser, "pesi");
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            String selectedFilePath = selectedFile.getAbsolutePath();
            weightsStringProperty.set(selectedFilePath);
            // pathToWeightsFileTextField.setText(selectedFilePath);
            weigthsFile = selectedFile;
            System.out.println("Pointer to weights file: " + weigthsFile.getAbsolutePath());
            // System.out.println(selectedFilePath);
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
            // look for the file in .../darknet/cfg
            if (!lookingForFile("dog.jpg", "data")) {
                Alert alert = new Alert(AlertType.WARNING,
                        "Assicurati di avere il file dog.jpg sotto .../darknet/data!");
                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    // formatSystem();
                    dogImgCheckBox.setSelected(false);
                }
            } else {
                eagleImgCheckBox.setSelected(false);
                //pathToDetectImgTextField.setText("");
            }
        }
    }

    @FXML
    void changeStateEagleImgCheckBox(ActionEvent event) {
        if (eagleImgCheckBox.isSelected()) {
            // look for the file in .../darknet/cfg
            if (!lookingForFile("eagle.jpg", "data")) {
                Alert alert = new Alert(AlertType.WARNING,
                        "Assicurati di avere il file eagle.jpg sotto .../darknet/data!");
                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    // formatSystem();
                    eagleImgCheckBox.setSelected(false);
                }
            } else {
                dogImgCheckBox.setSelected(false);
                //pathToDetectImgTextField.setText("");
            }
        }
    }

    @FXML
    void handleChooseDetectImgButtonClick(ActionEvent event) {
        Stage stage = (Stage) detectVBox.getScene().getWindow();

        final FileChooser fileChooser = new FileChooser();
        configureFileChooser(fileChooser, "prova (Immagine da Individuare oggetti)");
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            String selectedFilePath = selectedFile.getAbsolutePath();
            detectImgStringProperty.set(selectedFilePath);
            // pathToDetectImgTextField.setText(selectedFilePath);
            detectImgFile = selectedFile;
            System.out.println("Pointer to detect image file: " + detectImgFile.getAbsolutePath());
            // System.out.println(selectedFilePath);
            dogImgCheckBox.setSelected(false);
            eagleImgCheckBox.setSelected(false);
        }
    }

    private void getTextOnInputChangedImgTextField() {
        // System.out.println(detectImgStringProperty.get());
        String typedFile = detectImgStringProperty.get();
        detectImgFile = new File(typedFile);
        System.out.println("Pointer to detect image file: " + detectImgFile.getAbsolutePath());
        dogImgCheckBox.setSelected(false);
        eagleImgCheckBox.setSelected(false);
    }

    private void changeFlagInMakefile(String flag, int value) {
        try {
            // input the (modified) file content to the StringBuffer "input"
            BufferedReader file = new BufferedReader(
                    new FileReader(directoryToDarknet.getAbsolutePath() + "/Makefile"));
            StringBuffer inputBuffer = new StringBuffer();
            String line;

            while ((line = file.readLine()) != null) {
                // System.out.println(line);
                // line = ... // replace the line here
                if (line.equalsIgnoreCase(flag + "=" + Math.abs(value - 1))) {
                    System.out.println("Found line");
                    line = flag + "=" + value;
                    System.out.println("Line after: " + line);
                } else if (line.equalsIgnoreCase(flag + "=" + value)) {
                    System.out.println("Found line, flag already setted: " + line);
                }
                inputBuffer.append(line);
                inputBuffer.append('\n');
            }
            file.close();

            // write the new string with the replaced line OVER the same file
            FileOutputStream fileOut = new FileOutputStream(directoryToDarknet.getAbsolutePath() + "/Makefile");
            fileOut.write(inputBuffer.toString().getBytes());
            fileOut.close();

        } catch (Exception e) {
            System.out.println("Problem reading Makefile.");
            e.printStackTrace();
        }
    }

    @FXML
    void changeStateGpuCheckBox(ActionEvent event) {
        if (gpuCheckBox.isSelected()) {
            Alert alert = new Alert(AlertType.CONFIRMATION,
                    "Per questa opzione assicurati di aver installato correttamente i driver di nvidia e cuda toolkit!");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                needCompilation = true;
                // change flag in Makefile
                changeFlagInMakefile("GPU", 1);
            } else {
                gpuCheckBox.setSelected(false);
            }
        } else {
            changeFlagInMakefile("GPU", 0);
        }
        compileButton.requestFocus();
    }

    @FXML
    void changeStateOpencvCheckBox(ActionEvent event) {
        if (opencvCheckBox.isSelected()) {
            Alert alert = new Alert(AlertType.CONFIRMATION,
                    "Per questa opzione assicurati di aver installato correttamente OpenCV!");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                needCompilation = true;
                // change flag in Makefile
                changeFlagInMakefile("OPENCV", 1);
            } else {
                opencvCheckBox.setSelected(false);
            }
        } else {
            changeFlagInMakefile("OPENCV", 0);
        }
        compileButton.requestFocus();
    }

    @FXML
    void changeStateCudnnCheckBox(ActionEvent event) {
        if (cudnnCheckBox.isSelected()) {
            Alert alert = new Alert(AlertType.CONFIRMATION,
                    "Per questa opzione assicurati di aver installato correttamente CuDNN!");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                needCompilation = true;
                // change flag in Makefile
                changeFlagInMakefile("CUDNN", 1);
            } else {
                cudnnCheckBox.setSelected(false);
            }
        } else {
            changeFlagInMakefile("CUDNN", 0);
        }
        compileButton.requestFocus();
    }

    @FXML
    void checkAndCompile(ActionEvent event) {
        if (needCompilation) {
            String s;
            String lastLine = "";
            Process p;
            try {
                File dir = new File(directoryToDarknet.getAbsolutePath());
                p = Runtime.getRuntime().exec("make", null, dir);
                BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
                while ((s = br.readLine()) != null) {
                    System.out.println("line: " + s);
                    lastLine = s;
                }
                p.waitFor();
                System.out.println("exit: " + p.exitValue());
                int processExitValue = p.exitValue();
                p.destroy();
                if (processExitValue == 0) {
                    needCompilation = false;
                } else {
                    Alert alert = new Alert(AlertType.ERROR, "Errore di compilazione: \n\n" + lastLine);
                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.isPresent() && result.get() == ButtonType.OK) {
                        // formatSystem();
                    }
                }
            } catch (Exception e) {
                System.out.println(e.getStackTrace());
                // e.printStackTrace();
            }

        } else {
            Alert alert = new Alert(AlertType.INFORMATION, "Compilazione non necessaria!");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                // formatSystem();
            }
        }
    }

    @FXML
    void runDetector(ActionEvent event) {
        // check if al files was selected
        // before write down in the cfg file the params batch subdivisions width height
        if (batchCfStringProperty.getValue() == null || batchCfStringProperty.getValue().equalsIgnoreCase("")) {
            System.out.println("bacth didnt change");
        }
        if (!needCompilation) {
            String s;
            Process p;
            try {
                File dir = new File(directoryToDarknet.getAbsolutePath());
                String detectCommand = "./darknet detect " + cfgFile.getAbsolutePath() + " "
                        + weigthsFile.getAbsolutePath() + " " + detectImgFile.getAbsolutePath() + " -thresh "
                        + thresholdSpinner.getValue();
                System.out.println(detectCommand);
                /* 
                 * p = Runtime.getRuntime().exec(detectCommand, null, dir); BufferedReader br =
                 * new BufferedReader( new InputStreamReader(p.getInputStream())); while ((s =
                 * br.readLine()) != null) { System.out.println("line: " + s); } p.waitFor();
                 * System.out.println ("exit: " + p.exitValue());
                 * 
                 * p.destroy();
                 */
            } catch (Exception e) {
                System.out.println(e.getStackTrace());
                // e.printStackTrace();
            }

        } else {
            Alert alert = new Alert(AlertType.WARNING, "Compilazione necessaria!");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                // formatSystem();
                compileButton.requestFocus();
            }
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

        assert dogImgCheckBox != null : "fx:id=\"dogImgCheckBox\" was not injected: check your FXML file 'secondary.fxml'.";
        assert eagleImgCheckBox != null : "fx:id=\"eagleImgCheckBox\" was not injected: check your FXML file 'secondary.fxml'.";
        assert chooseDetectImgButton != null : "fx:id=\"chooseDetectImgButton\" was not injected: check your FXML file 'secondary.fxml'.";
        assert pathToDetectImgTextField != null : "fx:id=\"pathToDetectImgTextField\" was not injected: check your FXML file 'secondary.fxml'.";

        assert gpuCheckBox != null : "fx:id=\"gpuCheckBox\" was not injected: check your FXML file 'secondary.fxml'.";
        assert opencvCheckBox != null : "fx:id=\"opencvCheckBox\" was not injected: check your FXML file 'secondary.fxml'.";
        assert thresholdSpinner != null : "fx:id=\"thresholdSpinner\" was not injected: check your FXML file 'secondary.fxml'.";
        assert compileButton != null : "fx:id=\"compileButton\" was not injected: check your FXML file 'secondary.fxml'.";
        assert detectButton != null : "fx:id=\"detectButton\" was not injected: check your FXML file 'secondary.fxml'.";

        directoryToDarknet = new File(App.getDarknetPath());
        pathToCfgFileTextField.textProperty().bindBidirectional(cfgStringProperty);
        pathToWeightsFileTextField.textProperty().bindBidirectional(weightsStringProperty);
        pathToDetectImgTextField.textProperty().bindBidirectional(detectImgStringProperty);

        pathToCfgFileTextField.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                // Auto-generated method stub
                if (!newValue) {
                    System.out.println("Focusing out from pathToWeightsFileTextfield!");
                    if (cfgStringProperty.get() == null || cfgStringProperty.get().equalsIgnoreCase("")) {
                        System.out.println("pathToCfgFileTextField invalid path");
                        yolov3CfgCheckBox.setSelected(false);
                        yolov3tinyCfgCheckBox.setSelected(false);
                        cfgFile = null; // to cover the case the user erase the text in textField
                        //erase params values
                    } else if (cfgFile != null && cfgFile.getAbsolutePath().equalsIgnoreCase(cfgStringProperty.get())) {
                        System.out.println("Path pathToCfgFileTextField do not change");
                    } else {
                        getTextOnInputChangedCfgTextField();
                    }
                }
            }

        });

        pathToWeightsFileTextField.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                // Auto-generated method stub
                if (!newValue) {
                    System.out.println("Focusing out from pathToWeightsFileTextfield!");
                    if (weightsStringProperty.get() == null || weightsStringProperty.get().equalsIgnoreCase("")) {
                        System.out.println("pathToWeightsFileTextField invalid path");
                        yolov3WeigthsCheckBox.setSelected(false);
                        darknetConv74CheckBox.setSelected(false);
                        weigthsFile = null; // to cover the case the user erase the text in textField
                    } else if (weigthsFile != null
                            && weigthsFile.getAbsolutePath().equalsIgnoreCase(weightsStringProperty.get())) {
                        System.out.println("Path pathToWeightsFileTextField do not change");
                    } else {
                        getTextOnInputChangedWeightsTextField();
                    }
                }
            }

        });

        pathToDetectImgTextField.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                // Auto-generated method stub
                if (!newValue) {
                    System.out.println("Focusing out from pathToDetectImgTextField!");
                    if (detectImgStringProperty.get() == null || detectImgStringProperty.get().equalsIgnoreCase("")) {
                        System.out.println("pathToDetectImgTextField invalid path");
                        dogImgCheckBox.setSelected(false);
                        eagleImgCheckBox.setSelected(false);
                        detectImgFile = null;// to cover the case the user erase the text in textField
                    } else if (detectImgFile != null
                            && detectImgFile.getAbsolutePath().equalsIgnoreCase(detectImgStringProperty.get())) {
                        System.out.println("Path pathToDetectImgTextField do not change");
                    } else {
                        getTextOnInputChangedImgTextField();
                    }
                }
            }

        });

        // System.out.println(thresholdSpinner.getValue());
        /*
         * thresholdSpinner.focusedProperty().addListener(new ChangeListener<Boolean>(){
         * 
         * @Override public void changed(ObservableValue<? extends Boolean> observable,
         * Boolean oldValue, Boolean newValue) { // Auto-generated method stub if
         * (!newValue) { System.out.println(thresholdSpinner.getValue()); } }
         * 
         * });
         */

        batchCfgTextField.textProperty().bindBidirectional(batchCfStringProperty);
        subdivisionsCfgTextField.textProperty().bindBidirectional(subdivisionsCfgStringProperty);
        widthCfgTextField.textProperty().bindBidirectional(widthCfgStringProperty);
        heightCfgTextField.textProperty().bindBidirectional(heightStringProperty);

    }

    @FXML
    private void switchToPrimary() throws IOException {
        App.setRoot("primary");
    }
    
}