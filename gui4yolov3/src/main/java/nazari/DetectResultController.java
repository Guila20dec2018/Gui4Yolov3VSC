/**
 * Sample Skeleton for 'detectResult.fxml' Controller Class
 */

package nazari;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class DetectResultController {

    private String darknetPath;

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="openOriginalImgButton"
    private Button openOriginalImgButton; // Value injected by FXMLLoader

    @FXML // fx:id="detectTimeTextLabel"
    private Label detectTimeTextLabel; // Value injected by FXMLLoader

    @FXML // fx:id="detecResultImageView"
    private ImageView detecResultImageView; // Value injected by FXMLLoader

    @FXML // fx:id="detectResultTableView"
    private TableView<?> detectResultTableView; // Value injected by FXMLLoader

    @FXML // fx:id="salveResultImgButton"
    private Button salveResultImgButton; // Value injected by FXMLLoader

    @FXML // fx:id="closeButton"
    private Button closeButton; // Value injected by FXMLLoader

    @FXML
    void handleCloseButtonClick(ActionEvent event) throws IOException {
        App.setRoot("secondary");
    }

    @FXML
    void handleOpenOriginalImgButtonClick(ActionEvent event) {

    }

    @FXML
    void handleSaveResultImgButtonClick(ActionEvent event) {

    }

    private void loadPredctionsResult() {
        try {
			// input the (modified) file content to the StringBuffer "input"
            BufferedReader file = new BufferedReader(
                    new FileReader(darknetPath + "/redOut"));
            String line;

            while ((line = file.readLine()) != null) {
                System.out.println(line);
                // line = ... // replace the line here
                String [] result = line.split("\\ ");
                for (int i = 0; i < result.length; i++) {
                    System.out.println(result[i]);
                }
            }
            file.close();

        } catch (Exception e) {
            System.out.println("Problem reading redOut file.");
            e.printStackTrace();
        }
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert openOriginalImgButton != null : "fx:id=\"openOriginalImgButton\" was not injected: check your FXML file 'detectResult.fxml'.";
        assert detectTimeTextLabel != null : "fx:id=\"detectTimeTextLabel\" was not injected: check your FXML file 'detectResult.fxml'.";
        assert detecResultImageView != null : "fx:id=\"detecResultImageView\" was not injected: check your FXML file 'detectResult.fxml'.";
        assert detectResultTableView != null : "fx:id=\"detectResultTableView\" was not injected: check your FXML file 'detectResult.fxml'.";
        assert salveResultImgButton != null : "fx:id=\"salveResultImgButton\" was not injected: check your FXML file 'detectResult.fxml'.";
        assert closeButton != null : "fx:id=\"closeButton\" was not injected: check your FXML file 'detectResult.fxml'.";

        darknetPath = App.getDarknetPath();
        
        InputStream is = null;
        try {
            is = new FileInputStream(darknetPath + "/predictions.jpg");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Image predictionsImage = new Image(is);
        detecResultImageView.setImage(predictionsImage);

        loadPredctionsResult();
    }
}
