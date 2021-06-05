package userapp;

import javafx.collections.FXCollections;
import javafx.collections.ObservableArray;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

public class FilterController extends AppController {
    
    @FXML
    private Text filterByText;
    @FXML
    private ComboBox filterByComboBox;
    @FXML
    private TextField filterByTextField;
    @FXML
    private Text sortByText;
    @FXML
    private ComboBox sortByComboBox;
    @FXML
    private Button okayButton;

    @FXML
    private void initialize() {
        ColumnDescription[] columnDescriptions = ColumnDescription.values();
        ObservableList<ColumnDescription> values = FXCollections.observableArrayList(columnDescriptions);
        filterByComboBox.setItems(values);
        sortByComboBox.setItems(values);
        filterByComboBox.setValue(values.get(0));
        sortByComboBox.setValue(values.get(0));
        sortByText.setText("");
        //
        filterByText.textProperty().bind(Localizer.getBinging("text.filter_by"));
        sortByText.textProperty().bind(Localizer.getBinging("text.sort_by"));;
    }

    public void okayButtonEvent(ActionEvent event) {
        ColumnDescription filterColumn = (ColumnDescription) filterByComboBox.getValue();
        ColumnDescription sortColumn = (ColumnDescription) sortByComboBox.getValue();
        String filterValue = filterByTextField.getText();
        launcher.filterEvent(filterColumn, filterValue, sortColumn);
    }

}
