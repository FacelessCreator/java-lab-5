package userapp;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class SelectLanguageController extends AppController {
    
    @FXML
    private Button russianButton;
    @FXML
    private Button germanyButton;
    @FXML
    private Button bulgarianButton;
    @FXML
    private Button unitedKingdomButton;

    public void russianButtonEvent(ActionEvent event) {
        Localizer.setLocale("ru");
        launcher.localeChangeEvent();
    }

    public void germanyButtonEvent(ActionEvent event) {
        Localizer.setLocale("de");
        launcher.localeChangeEvent();
    }

    public void bulgarianButtonEvent(ActionEvent event) {
        Localizer.setLocale("bg");
        launcher.localeChangeEvent();
    }

    public void unitedKingdomButtonEvent(ActionEvent event) {
        Localizer.setLocale("en");
        launcher.localeChangeEvent();
    }

}
