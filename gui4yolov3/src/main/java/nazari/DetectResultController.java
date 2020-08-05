/**
 * Sample Skeleton for 'detectResult.fxml' Controller Class
 */

package nazari;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
    void handleCloseButtonClick(ActionEvent event) {

    }

    @FXML
    void handleOpenOriginalImgButtonClick(ActionEvent event) {

    }

    @FXML
    void handleSaveResultImgButtonClick(ActionEvent event) {

    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert openOriginalImgButton != null : "fx:id=\"openOriginalImgButton\" was not injected: check your FXML file 'detectResult.fxml'.";
        assert detectTimeTextLabel != null : "fx:id=\"detectTimeTextLabel\" was not injected: check your FXML file 'detectResult.fxml'.";
        assert detecResultImageView != null : "fx:id=\"detecResultImageView\" was not injected: check your FXML file 'detectResult.fxml'.";
        assert detectResultTableView != null : "fx:id=\"detectResultTableView\" was not injected: check your FXML file 'detectResult.fxml'.";
        assert salveResultImgButton != null : "fx:id=\"salveResultImgButton\" was not injected: check your FXML file 'detectResult.fxml'.";
        assert closeButton != null : "fx:id=\"closeButton\" was not injected: check your FXML file 'detectResult.fxml'.";

        //Image image1 = new Image("/home/stage101/Desktop/darknet/predictions.jpg");
        InputStream is = null;
        try {
            is = new FileInputStream("/home/stage101/Desktop/darknet/predictions.jpg"); // here I get FileNotFoundException
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Image image = new Image(is);
        detecResultImageView.setImage(image);
    }
}
