package nazari;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.scene.layout.VBox;

public class SecondaryController {
    
    @FXML private VBox detecTabContent;

    @FXML private DetectController detectController;

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;    

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {

    }

    @FXML
    private void switchToPrimary() throws IOException {
        App.setRoot("primary");
    }
}