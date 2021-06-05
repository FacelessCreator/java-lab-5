package userapp;

import database.DataBaseAnswer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import movie.Color;
import movie.Country;
import movie.Person;

public class SearchByOperatorController extends AppController {
    
    @FXML
    private Text operatorIdText;
    @FXML
    private TextField operatorIdTextField;
    @FXML
    private Button okayButton;

    @FXML
    private void initialize() {
        operatorIdText.textProperty().bind(Localizer.getBinging("text.operator_id"));
    }

    public void okayButtonEvent(ActionEvent event) {
        String operatorId = operatorIdTextField.getText();
        Person operator = new Person("", operatorId, Color.BLACK, Country.RUSSIA);
        launcher.removeByOperatorEvent(operator);
    }

}
