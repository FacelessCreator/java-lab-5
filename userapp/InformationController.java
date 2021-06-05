package userapp;

import javafx.fxml.FXML;
import javafx.scene.text.Text;

public class InformationController extends AppController {
    
    @FXML
    private Text informationText;

    public void displayText(String text) {
        informationText.setText(text);
    }

}
