package userapp;

import database.DataBaseAnswer;
import javafx.beans.binding.StringBinding;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

public class LoginController extends AppController {
    
    @FXML
    private Button languageButton;
    @FXML
    private Text userNameText;
    @FXML
    private TextField userNameTextField;
    @FXML
    private Text passwordText;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button loginButton;
    @FXML
    private Button registerButton;
    @FXML
    private Text statusBarText;

    private void displayStatus(String status) {
        statusBarText.setText(status);
    }

    private void displayCode(int code) {
        String res;
        switch (code) {
            case DataBaseAnswer.CODE_SUCCESS:
                res = "message.code_success";
                break;
            case DataBaseAnswer.CODE_OBJECT_ALREADY_EXISTS:
                res = "message.object_already_exists";
                break;
            case DataBaseAnswer.CODE_OBJECT_NOT_FOUND:
                res = "message.object_not_found";
                break;
            case DataBaseAnswer.CODE_CONNECTION_FAILED:
                res = "message.connection_failed";
                break;
            case DataBaseAnswer.CODE_BAD_ANSWER:
                res = "message.bad_answer";
                break;
            case DataBaseAnswer.CODE_BAD_REQUEST:
                res = "message.bad_request";
                break;
            case DataBaseAnswer.CODE_PERMISSION_DENIED:
                res = "message.permission_denied";
                break;
            case DataBaseAnswer.CODE_INTERNAL_ERROR:
                res = "message.internal_error";
                break;
            case DataBaseAnswer.CODE_SQL_ERROR:
                res = "message.sql_error";
                break;
            case DataBaseAnswer.CODE_OPERATION_UNSUPPORTED:
                res = "message.operation_unsupported";
                break;
            case DataBaseAnswer.CODE_AUTHORIZING_DENIED:
                res = "message.authorizing_denied";
                break;
            default:
                res = "message.unknown_code";
                break;
        }
        displayStatus(Localizer.get(res));
    }

    @FXML
    private void initialize() {
        userNameText.textProperty().bind(Localizer.getBinging("text.username"));
        passwordText.textProperty().bind(Localizer.getBinging("text.password"));
        loginButton.textProperty().bind(Localizer.getBinging("text.login"));
        registerButton.textProperty().bind(Localizer.getBinging("text.register"));
    }

    public void languageButtonEvent(ActionEvent event) {
        launcher.openSelectLanguageScreen();
    }

    public void loginButtonEvent(ActionEvent event) {
        String userName = userNameTextField.getText();
        String password = passwordField.getText();
        if (userName.length() == 0) {
            displayStatus(Localizer.get("message.input_username"));
            return;
        }
        if (password.length() == 0) {
            displayStatus(Localizer.get("message.input_password"));
            return;
        }
        DataBaseAnswer<Void> answer = dataManipulator.login(userName, password);
        if (answer.code == DataBaseAnswer.CODE_SUCCESS) {
            launcher.openMainScreen();
        } else {
            displayCode(answer.code);
        }
    }

    public void registerButtonEvent(ActionEvent event) {
        String userName = userNameTextField.getText();
        String password = passwordField.getText();
        if (userName.length() == 0) {
            displayStatus(Localizer.get("message.input_username"));
            return;
        }
        if (password.length() == 0) {
            displayStatus(Localizer.get("message.input_password"));
            return;
        }
        DataBaseAnswer<Void> answer = dataManipulator.register(userName, password);
        if (answer.code == DataBaseAnswer.CODE_SUCCESS) {
            launcher.openMainScreen();
        } else {
            displayCode(answer.code);
        }
    }

    public void clear() {
        passwordField.setText("");
        displayStatus("");
    }

}
