package userapp;

import java.net.MalformedURLException;
import java.net.URL;

import database.DataBaseAnswer;
import database.ProtectedDataManipulator;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import movie.Movie;
import movie.Person;
import remoteDataBase.RemoteDataBase;

import java.io.File;
import java.io.IOException;

public class Launcher {

    private RemoteDataBase dataBase;
    private ProtectedDataManipulator dataManipulator;

    private Stage mainStage;
    private Stage supportStage;

    private Scene mainScene;
    private Scene loginScene;
    private Scene editElementScene;
    private Scene informationScene;
    private Scene searchByOperatorScene;
    private Scene selectLanguageScene;
    private Scene filterScene;

    private MainController mainController;
    private LoginController loginController;
    private EditElementController editElementController;
    private InformationController informationController;
    private SearchByOperatorController searchByOperatorController;
    private SelectLanguageController selectLanguageController;
    private FilterController filterController;

    public Launcher(String host, int port) {
        dataBase = new RemoteDataBase(host, port);
        dataManipulator = new ProtectedDataManipulator(dataBase);
    }

    public int launch(Stage mainStage) {
        DataBaseAnswer<String> dataBaseTestAnswer = dataBase.about();
        if (dataBaseTestAnswer.code == DataBaseAnswer.CODE_CONNECTION_FAILED) {
            return 3;
        }
        FXMLLoader loader;
        URL url;
        Parent root;
        this.mainStage = mainStage;
        try {
            // localization
            Localizer.loadLocales("GUI/locales", "app");
            Localizer.setLocale("en");
            // main scene
            loader = new FXMLLoader();
            url = new File("GUI/scenes/main.fxml").toURI().toURL();
            loader.setLocation(url);
            root = loader.load();
            mainScene = new Scene(root);
            mainController = (MainController) loader.getController();
            mainController.setCrossing(this, dataManipulator);
            // login scene
            loader = new FXMLLoader();
            url = new File("GUI/scenes/login.fxml").toURI().toURL();
            loader.setLocation(url);
            root = loader.load();
            loginScene = new Scene(root);
            loginController = (LoginController) loader.getController();
            loginController.setCrossing(this, dataManipulator);
            // edit element scene
            loader = new FXMLLoader();
            url = new File("GUI/scenes/editElement.fxml").toURI().toURL();
            loader.setLocation(url);
            root = loader.load();
            editElementScene = new Scene(root);
            editElementController = (EditElementController) loader.getController();
            editElementController.setCrossing(this, dataManipulator);
            // information scene
            loader = new FXMLLoader();
            url = new File("GUI/scenes/information.fxml").toURI().toURL();
            loader.setLocation(url);
            root = loader.load();
            informationScene = new Scene(root);
            informationController = (InformationController) loader.getController();
            informationController.setCrossing(this, dataManipulator);
            // search by operator scene
            loader = new FXMLLoader();
            url = new File("GUI/scenes/searchByOperator.fxml").toURI().toURL();
            loader.setLocation(url);
            root = loader.load();
            searchByOperatorScene = new Scene(root);
            searchByOperatorController = (SearchByOperatorController) loader.getController();
            searchByOperatorController.setCrossing(this, dataManipulator);
            // select language scene
            loader = new FXMLLoader();
            url = new File("GUI/scenes/selectLanguage.fxml").toURI().toURL();
            loader.setLocation(url);
            root = loader.load();
            selectLanguageScene = new Scene(root);
            selectLanguageController = (SelectLanguageController) loader.getController();
            selectLanguageController.setCrossing(this, dataManipulator);
            // filter scene
            loader = new FXMLLoader();
            url = new File("GUI/scenes/filter.fxml").toURI().toURL();
            loader.setLocation(url);
            root = loader.load();
            filterScene = new Scene(root);
            filterController = (FilterController) loader.getController();
            filterController.setCrossing(this, dataManipulator);
            // main stage
            mainStage.setTitle("Client"); // TODO use localization
            mainStage.setScene(loginScene);
            mainStage.show();
            // support stage
            supportStage = new Stage();
            supportStage.setTitle("Support");
        } catch (MalformedURLException e) {
            return 1;
        } catch (IOException e) {
            return 2;
        }
        return 0;
    }

    public void openLoginScreen() {
        loginController.clear();
        mainStage.setTitle(Localizer.get("title.login"));
        mainStage.setScene(loginScene);
        mainStage.show();
        supportStage.hide();
    }

    public void openMainScreen() {
        mainController.setupScreen();
        mainStage.setTitle(Localizer.get("title.main"));
        mainStage.setScene(mainScene);
        mainStage.show();
        supportStage.hide();
    }

    public void openSelectLanguageScreen() {
        supportStage.setTitle(Localizer.get("title.select_language"));
        supportStage.setScene(selectLanguageScene);
        supportStage.show();
    }

    public void openEditElementScreen(Movie movie) {
        supportStage.setTitle(Localizer.get("title.edit_element"));
        supportStage.setScene(editElementScene);
        editElementController.displayMovie(movie);
        supportStage.show();
    }

    public void openInformationScreen(String text) {
        informationController.displayText(text);
        supportStage.setTitle(Localizer.get("title.information"));
        supportStage.setScene(informationScene);
        supportStage.show();
    }

    public void openSearchByOperatorScreen() {
        supportStage.setTitle(Localizer.get("title.search_by_operator"));
        supportStage.setScene(searchByOperatorScene);
        supportStage.show();
    }

    public void openFilterScreen() {
        supportStage.setTitle(Localizer.get("title.filter"));
        supportStage.setScene(filterScene);
        supportStage.show();
    }

    public void closeSupportWindow() {
        supportStage.hide();
    }

    public void editElementOkayEvent(Movie movie) {
        supportStage.hide();
        mainController.editElementOkayEvent(movie);
    }

    public void editElementDeleteEvent(Movie movie) {
        supportStage.hide();
        mainController.editElementDeleteEvent(movie);
    }

    public void removeByOperatorEvent(Person operator) {
        supportStage.hide();
        mainController.removeByOperatorEvent(operator);
    }

    public void filterEvent(ColumnDescription filterColumn, String filterValue, ColumnDescription sortColumn) {
        supportStage.hide();
        mainController.filterEvent(filterColumn, filterValue, sortColumn);
    }

    public void localeChangeEvent() {
        supportStage.hide();
        if (mainStage.getScene().equals(mainScene)) {
            openMainScreen();
        } else {
            openLoginScreen();
        }
        editElementController.localeChangeEvent();
        filterController.localeChangeEvent();
        informationController.localeChangeEvent();
        loginController.localeChangeEvent();
        mainController.localeChangeEvent();
        searchByOperatorController.localeChangeEvent();
        selectLanguageController.localeChangeEvent();
    }

}
