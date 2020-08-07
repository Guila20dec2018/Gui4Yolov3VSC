package nazari;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ProcessBuilder.Redirect;
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

    @FXML // fx:id="cudnnCheckBox"sw
    private CheckBox cudnnCheckBox; // Value injected by FXMLLoader

    @FXML // fx:id="thresholdSpinner"
    private Spinner<Double> thresholdSpinner; // Value injected by FXMLLoader

    @FXML // fx:id="compileButton"
    private Button compileButton; // Value injected by FXMLLoader

    @FXML // fx:id="detectButton"
    private Button detectButton; // Value injected by FXMLLoader

    private final StringProperty cfgStringProperty = new SimpleStringProperty();
    private final StringProperty weightsStringProperty = new SimpleStringProperty();
    private final StringProperty detectImgStringProperty = new SimpleStringProperty();

    private boolean needCompilation = true;

    private final StringProperty batchCfgStringProperty = new SimpleStringProperty();
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
        else {
            cfgFile = null;
            cfgStringProperty.setValue("");
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
        else {
            cfgFile = null;
            cfgStringProperty.setValue("");
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
        else {
            weigthsFile = null;
            weightsStringProperty.setValue("");
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
        else {
            weigthsFile = null;
            weightsStringProperty.setValue("");
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
        else {
            detectImgFile = null;
            detectImgStringProperty.setValue("");
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
        else {
            detectImgFile = null;
            detectImgStringProperty.setValue("");
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

    // gpu, opencv, cudnn
    private void loadMakefileFlags() {
        try {
            // input the (modified) file content to the StringBuffer "input"
            BufferedReader file = new BufferedReader(
                    new FileReader(directoryToDarknet.getAbsolutePath() + "/Makefile"));
            String line;

            while ((line = file.readLine()) != null) {
                String searchMe = line;
                String findGPU = "GPU=";
                String findOPENCV = "OPENCV=";
                String findCUDNN = "CUDNN=";
                if (searchMe.regionMatches(0, findGPU, 0, findGPU.length())) {
                    System.out.println("Found gpu: " + searchMe);
                    if (searchMe.equalsIgnoreCase(findGPU + "1")) {
                        gpuCheckBox.setSelected(true);
                    }
                }
                else if (searchMe.regionMatches(0, findOPENCV, 0, findOPENCV.length())) {
                    System.out.println("Found opencv: " + searchMe);
                    if (searchMe.equalsIgnoreCase(findOPENCV + "1")) {
                        opencvCheckBox.setSelected(true);
                    }
                }
                else if (searchMe.regionMatches(0, findCUDNN, 0, findCUDNN.length())) {
                    System.out.println("Found cudnn: " + searchMe);
                    if (searchMe.equalsIgnoreCase(findCUDNN + "1")) {
                        cudnnCheckBox.setSelected(true);
                    }
                }
            }
            file.close();

        } catch (Exception e) {
            System.out.println("Problem reading Makefile file.");
            e.printStackTrace();
        }
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
                    System.out.println("Found line: " + line);
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
                compileButton.requestFocus();
            } else {
                gpuCheckBox.setSelected(false);
            }
        } else {
            changeFlagInMakefile("GPU", 0);
            needCompilation = true;
            compileButton.requestFocus();
        }
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
                compileButton.requestFocus();
            } else {
                opencvCheckBox.setSelected(false);
            }
        } else {
            changeFlagInMakefile("OPENCV", 0);
            needCompilation = true;
            compileButton.requestFocus();
        }
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
                compileButton.requestFocus();
            } else {
                cudnnCheckBox.setSelected(false);
            }
        } else {
            changeFlagInMakefile("CUDNN", 0);
            needCompilation = true;
            compileButton.requestFocus();
        }
    }

    @FXML
    void checkAndCompile(ActionEvent event) {
        if (needCompilation) {
            String s;
            Process p;
            String lastLine = "";
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

    private boolean checkFilesSelection() {
        if (cfgFile == null) {
            Alert alert = new Alert(AlertType.WARNING, "Per avviare e' necessario scegliere un file di configurazione.");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                pathToCfgFileTextField.requestFocus();
                return false;
            }
        }
        if (weigthsFile == null) {
            Alert alert = new Alert(AlertType.WARNING, "Per avviare e' necessario scegliere un file di pesi.");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                pathToWeightsFileTextField.requestFocus();
                return false;
            }
        }
        if (detectImgFile == null) {
            Alert alert = new Alert(AlertType.WARNING, "Per avviare e' necessario scegliere una immagine per essere analizzata.");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                pathToDetectImgTextField.requestFocus();
                return false;
            }
        }
        return true;
    }

    private boolean cfgParamsChanged() {
        if (batchCfgStringProperty.getValue() != null && !batchCfgStringProperty.getValue().equalsIgnoreCase("")) {
            //System.out.println("bacth changed");
            return true;
        }
        if (subdivisionsCfgStringProperty.getValue() != null && !subdivisionsCfgStringProperty.getValue().equalsIgnoreCase("")) {
            return true;
        }
        if (widthCfgStringProperty.getValue() != null && !widthCfgStringProperty.getValue().equalsIgnoreCase("")) {
            return true;
        }
        if (heightStringProperty.getValue() != null && !heightStringProperty.getValue().equalsIgnoreCase("")) {
            return true;
        }
        return false;
    }

    private void writeCfgFile() {
        //System.out.println("Params changed");
        try {
            // input the (modified) file content to the StringBuffer "input"
            BufferedReader file = new BufferedReader(
                    new FileReader(cfgStringProperty.getValue()));
            StringBuffer inputBuffer = new StringBuffer();
            String line;

            while ((line = file.readLine()) != null) {
                String searchMe = line;
                String findBatch = "batch=";
                String findSubdivisions = "subdivisions=";
                String findWidth = "width=";
                String findHeight = "height=";
                if (searchMe.regionMatches(0, findBatch, 0, findBatch.length()) && 
                        (batchCfgStringProperty != null && !batchCfgStringProperty.getValue().equalsIgnoreCase(""))) {
                    line = findBatch + batchCfgStringProperty.getValue();
                    System.out.println("Found batch: " + searchMe + " , new batch: " + line);
                }
                else if (searchMe.regionMatches(0, findSubdivisions, 0, findSubdivisions.length()) && 
                        (subdivisionsCfgStringProperty != null && !subdivisionsCfgStringProperty.getValue().equalsIgnoreCase(""))) {
                    line = findSubdivisions + subdivisionsCfgStringProperty.getValue();
                    System.out.println("Found subdivisions: " + searchMe + " , new subdivisions: " + line);
                }
                else if (searchMe.regionMatches(0, findWidth, 0, findWidth.length()) && 
                        (widthCfgStringProperty != null && !widthCfgStringProperty.getValue().equalsIgnoreCase(""))) {
                    line = findWidth + widthCfgStringProperty.getValue();
                    System.out.println("Found width: " + searchMe + " , new width: " + line);
                }
                else if (searchMe.regionMatches(0, findHeight, 0, findHeight.length()) && 
                        (heightStringProperty != null && !heightStringProperty.getValue().equalsIgnoreCase(""))) {
                    line = findHeight + heightStringProperty.getValue();
                    System.out.println("Found height: " + searchMe + " , new height: " + line);
                }
                inputBuffer.append(line);
                inputBuffer.append('\n');
            }
            file.close();

            // write the new string with the replaced line OVER the same file
            FileOutputStream fileOut = new FileOutputStream(cfgStringProperty.getValue());
            fileOut.write(inputBuffer.toString().getBytes());
            fileOut.close();

        } catch (Exception e) {
            System.out.println("Problem reading cfg file.");
            e.printStackTrace();
        }
    }

    private void switchToDetectResultScreen() throws IOException {
        App.setRoot("detectResult");
    }

    @FXML
    void runDetector(ActionEvent event) {
        if (!needCompilation && checkFilesSelection()) {
            if (cfgParamsChanged()) {
                writeCfgFile();
            }
            try {
                System.out.println(Double.toString(thresholdSpinner.getValue()));
                String threshold = Double.toString(thresholdSpinner.getValue());

                ProcessBuilder pb = new ProcessBuilder("./darknet", "detect", cfgFile.getAbsolutePath(), 
                        weigthsFile.getAbsolutePath(), detectImgFile.getAbsolutePath(), threshold);
                System.out.println("Command: " + pb.command());
                pb.directory(new File(directoryToDarknet.getAbsolutePath()));
                //pb.inheritIO();
                //behaves in exactly the same way as the invocation
                pb.redirectInput(Redirect.INHERIT)
                .redirectOutput(new File(directoryToDarknet.getAbsolutePath() + "/redOut"))
                .redirectError(new File(directoryToDarknet.getAbsolutePath() + "/redErr"));

                Process process = pb.start();
                process.waitFor();
                System.out.println("Process exit value: " + process.exitValue());

                if (process.exitValue() == 0) {
                    Alert alert = new Alert(AlertType.CONFIRMATION, "Individuamento eseguito corretamente.\nDesidera vedere i risultati?");
                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.isPresent() && result.get() == ButtonType.OK) {
                        try {
                            switchToDetectResultScreen();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                else {
                    needCompilation = true;
                    //read error msg from file and show in alert
                    Alert alert = new Alert(AlertType.ERROR, "Errore eseguendo darknet: ...");
                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.isPresent() && result.get() == ButtonType.OK) {
                        //formatSystem();
                    }

                }
            } catch (Exception e) {
                System.out.println(e.getStackTrace());
            }
        }
        else if (needCompilation) {
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
        assert batchCfgTextField != null : "fx:id=\"batchCfgTextField\" was not injected: check your FXML file 'detect.fxml'.";
        assert subdivisionsCfgTextField != null : "fx:id=\"subdivisionsCfgTextField\" was not injected: check your FXML file 'detect.fxml'.";
        assert widthCfgTextField != null : "fx:id=\"widthCfgTextField\" was not injected: check your FXML file 'detect.fxml'.";
        assert heightCfgTextField != null : "fx:id=\"heightCfgTextField\" was not injected: check your FXML file 'detect.fxml'.";


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

        batchCfgTextField.textProperty().bindBidirectional(batchCfgStringProperty);
        subdivisionsCfgTextField.textProperty().bindBidirectional(subdivisionsCfgStringProperty);
        widthCfgTextField.textProperty().bindBidirectional(widthCfgStringProperty);
        heightCfgTextField.textProperty().bindBidirectional(heightStringProperty);

        //check the value of the textFilds, only numeric permitted
        batchCfgTextField.focusedProperty().addListener(new ChangeListener<Boolean>(){
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (!newValue && (batchCfgStringProperty.getValue() != null && 
                        !batchCfgStringProperty.getValue().equalsIgnoreCase(""))) {
                    try {
                        Integer.parseInt(batchCfgStringProperty.getValue());
                    } catch (Exception e) {
                        System.out.println("Exception in batch textFild " + e.getMessage());
                        Alert alert = new Alert(AlertType.ERROR, "Il valore di batch deve essere numerico!");
                        Optional<ButtonType> result = alert.showAndWait();
                        if (result.isPresent() && result.get() == ButtonType.OK) {
                            //formatSystem();
                        }
                        batchCfgTextField.requestFocus();
                    }
                }
            }
            
        });
        subdivisionsCfgTextField.focusedProperty().addListener(new ChangeListener<Boolean>(){
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (!newValue && (subdivisionsCfgStringProperty.getValue() != null && 
                        !subdivisionsCfgStringProperty.getValue().equalsIgnoreCase(""))) {
                    try {
                        Integer.parseInt(subdivisionsCfgStringProperty.getValue());
                    } catch (Exception e) {
                        System.out.println("Exception in subdivisions textFild " + e.getMessage());
                        Alert alert = new Alert(AlertType.ERROR, "Il valore di subdivisions deve essere numerico!");
                        Optional<ButtonType> result = alert.showAndWait();
                        if (result.isPresent() && result.get() == ButtonType.OK) {
                            //formatSystem();
                        }
                        subdivisionsCfgTextField.requestFocus();
                    }
                }
            }
            
        });
        widthCfgTextField.focusedProperty().addListener(new ChangeListener<Boolean>(){
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (!newValue && (widthCfgStringProperty.getValue() != null && 
                        !widthCfgStringProperty.getValue().equalsIgnoreCase(""))) {
                    try {
                        Integer.parseInt(widthCfgStringProperty.getValue());
                    } catch (Exception e) {
                        System.out.println("Exception in width textFild " + e.getMessage());
                        Alert alert = new Alert(AlertType.ERROR, "Il valore di width deve essere numerico!");
                        Optional<ButtonType> result = alert.showAndWait();
                        if (result.isPresent() && result.get() == ButtonType.OK) {
                            //formatSystem();
                        }
                        widthCfgTextField.requestFocus();
                    }
                }
            }
            
        });
        heightCfgTextField.focusedProperty().addListener(new ChangeListener<Boolean>(){
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (!newValue && (heightStringProperty.getValue() != null && 
                        !heightStringProperty.getValue().equalsIgnoreCase(""))) {
                    try {
                        Integer.parseInt(heightStringProperty.getValue());
                    } catch (Exception e) {
                        System.out.println("Exception in height textFild " + e.getMessage());
                        Alert alert = new Alert(AlertType.ERROR, "Il valore di heigth deve essere numerico!");
                        Optional<ButtonType> result = alert.showAndWait();
                        if (result.isPresent() && result.get() == ButtonType.OK) {
                            //formatSystem();
                        }
                        heightCfgTextField.requestFocus();
                    }
                }
            }
            
        });

        loadMakefileFlags();

    }

    @FXML
    private void switchToPrimary() throws IOException {
        App.setRoot("primary");
    }
    
}