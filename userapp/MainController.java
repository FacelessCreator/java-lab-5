package userapp;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import database.DataBaseAnswer;
import etc.Hashes;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.Observable;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableArray;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.chart.PieChart.Data;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;
import javafx.scene.control.TableCell;
import movie.*;

public class MainController extends AppController {
    
    private enum EditElementReason {
        EDIT, REMOVE_LOWER, ADD_IF_MAX, ADD;
    };

    private EditElementReason editElementReason;

    @FXML
    private Button languageButton;
    @FXML
    private Button logoutButton;
    @FXML
    private Text userNameText;
    @FXML
    private TableView elementsTableView;
    @FXML
    private TableColumn<Movie, Integer> idColumn;
    @FXML
    private TableColumn<Movie, String> nameColumn;
    @FXML
    private TableColumn<Movie, Integer> coordinateXColumn;
    @FXML
    private TableColumn<Movie, Long> coordinateYColumn;
    @FXML
    private TableColumn<Movie, Long> oscarsCountColumn;
    @FXML
    private TableColumn<Movie, Long> lengthColumn;
    @FXML
    private TableColumn<Movie, String> movieGenreColumn;
    @FXML
    private TableColumn<Movie, String> mpaaRatingColumn;
    @FXML
    private TableColumn<Movie, String> operatorNameColumn;
    @FXML
    private TableColumn<Movie, String> operatorIdColumn;
    @FXML
    private TableColumn<Movie, String> operatorEyeColorColumn;
    @FXML
    private TableColumn<Movie, String> operatorNationalityColumn;
    @FXML
    private TableColumn<Movie, String> creatorNameColumn;
    @FXML
    private TableColumn<Movie, String> creationDateColumn;
    @FXML
    private Canvas elementsCanvas;
    @FXML
    private Button allElementsButton;
    @FXML
    private Button searchByOperatorButton;
    @FXML
    private Button informationButton;
    @FXML
    private Button maxElementButton;
    @FXML
    private Button minElementButton;
    @FXML
    private Button addElementButton;
    @FXML
    private Text statusBarText;
    @FXML
    private Button clearButton;
    @FXML
    private Button groupByOscarsCountButton;
    @FXML
    private Button filterButton;

    private List<Movie> movies;
    private boolean animationValue = false;
    private Timeline animationTimeLine;

    private Timeline moviesListUpdateTimeLine;

    private ColumnDescription filterByColumn = ColumnDescription.NOTHING;
    private ColumnDescription sortByColumn = ColumnDescription.NOTHING;
    private String filterByValue = "";

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

    private void redrawElements() {
        if (movies == null) {
            return;
        }
        GraphicsContext g = elementsCanvas.getGraphicsContext2D();
        g.clearRect(0, 0, elementsCanvas.getWidth(), elementsCanvas.getHeight());
        animationValue = !animationValue;
        for (Movie movie : movies) {
            int x = movie.getCoordinateX();
            long y = movie.getCoordinateY();
            long length = movie.getLength();
            String name = movie.getName();
            String creatorName = movie.getCreatorName();
            if (creatorName != null) {
                byte[] creatorHash = Hashes.getSHA384(creatorName).getBytes();
                g.setFill(javafx.scene.paint.Color.rgb(creatorHash[0], creatorHash[1], creatorHash[2]));
            } else {
                g.setFill(javafx.scene.paint.Color.rgb(0, 0, 0));
            }
            g.fillRect(x, y, length, 50);
            g.setFont(Font.font(30));
            if (animationValue) {
                g.setFill(javafx.scene.paint.Color.rgb(255, 255, 255));
            }
            g.fillText(name, x+10, y+40);
        }
    }

    private List<Movie> filterElements(List<Movie> values, ColumnDescription filterByColumn, String filterValue) {
        switch (filterByColumn) {
            case ID:
                return values.stream().filter((m) -> Long.toString(m.getId()).equals(filterValue)).collect(Collectors.toList());
            case NAME:
                return values.stream().filter((m) -> m.getName().equals(filterValue)).collect(Collectors.toList());
            case COORDINATE_X:
                return values.stream().filter((m) -> Integer.toString(m.getCoordinateX()).equals(filterValue)).collect(Collectors.toList());
            case COORDINATE_Y:
                return values.stream().filter((m) -> Long.toString(m.getCoordinateY()).equals(filterValue)).collect(Collectors.toList());
            case OSCARS_COUNT:
                return values.stream().filter((m) -> Long.toString(m.getOscarsCount()).equals(filterValue)).collect(Collectors.toList());
            case LENGTH:
                return values.stream().filter((m) -> Long.toString(m.getLength()).equals(filterValue)).collect(Collectors.toList());
            case GENRE:
                return values.stream().filter((m) -> m.getGenre() != null && m.getGenre().name().equals(filterValue)).collect(Collectors.toList());
            case MPAA_RATING:
                return values.stream().filter((m) -> m.getMpaaRating() != null && m.getMpaaRating().name().equals(filterValue)).collect(Collectors.toList());
            case OPERATOR_NAME:
                return values.stream().filter((m) -> m.getOperatorName() != null && m.getOperatorName().equals(filterValue)).collect(Collectors.toList());
            case OPERATOR_ID:
                return values.stream().filter((m) -> m.getOperatorId() != null && m.getOperatorId().equals(filterValue)).collect(Collectors.toList());
            case OPERATOR_EYE_COLOR:
                return values.stream().filter((m) -> m.getOperatorEyeColor() != null && m.getOperatorEyeColor().name().equals(filterValue)).collect(Collectors.toList());
            case OPERATOR_NATIONALITY:
                return values.stream().filter((m) -> m.getOperatorNationality().name().equals(filterValue)).collect(Collectors.toList());
            case CREATOR_NAME:
                return values.stream().filter((m) -> m.getCreatorName() != null && m.getCreatorName().equals(filterValue)).collect(Collectors.toList());
            case CREATION_TIME:
                return values.stream().filter((m) -> m.getCreationTime().toString().equals(filterValue)).collect(Collectors.toList());
            default:
                return values;
        }
    }

    private List<Movie> sortElements(List<Movie> values, ColumnDescription sortByColumn) {
        switch (sortByColumn) {
            case ID:
                return values.stream().sorted((m1, m2) -> Long.compare(m1.getId(), m2.getId())).collect(Collectors.toList());
            case NAME:
                return values.stream().sorted((m1, m2) -> m1.getName().compareTo(m2.getName())).collect(Collectors.toList());
            case COORDINATE_X:
                return values.stream().sorted((m1, m2) -> Integer.compare(m1.getCoordinateX(), m2.getCoordinateX())).collect(Collectors.toList());
            case COORDINATE_Y:
                return values.stream().sorted((m1, m2) -> Long.compare(m1.getCoordinateY(), m2.getCoordinateY())).collect(Collectors.toList());
            case OSCARS_COUNT:
                return values.stream().sorted((m1, m2) -> Long.compare(m1.getOscarsCount(), m2.getOscarsCount())).collect(Collectors.toList());
            case LENGTH:
                return values.stream().sorted((m1, m2) -> Long.compare(m1.getLength(), m2.getLength())).collect(Collectors.toList());
            case GENRE:
                return values.stream().filter((m) -> m.getGenre() != null).sorted((m1, m2) -> m1.getGenre().compareTo(m2.getGenre())).collect(Collectors.toList());
            case MPAA_RATING:
                return values.stream().filter((m) -> m.getMpaaRating() != null).sorted((m1, m2) -> m1.getMpaaRating().compareTo(m2.getMpaaRating())).collect(Collectors.toList());
            case OPERATOR_NAME:
                return values.stream().filter((m) -> m.getOperatorName() != null).sorted((m1, m2) -> m1.getOperatorName().compareTo(m2.getOperatorName())).collect(Collectors.toList());
            case OPERATOR_ID:
                return values.stream().filter((m) -> m.getOperatorId() != null).sorted((m1, m2) -> m1.getOperatorId().compareTo(m2.getOperatorId())).collect(Collectors.toList());
            case OPERATOR_EYE_COLOR:
                return values.stream().filter((m) -> m.getOperatorEyeColor() != null).sorted((m1, m2) -> m1.getOperatorEyeColor().compareTo(m2.getOperatorEyeColor())).collect(Collectors.toList());
            case OPERATOR_NATIONALITY:
                return values.stream().sorted((m1, m2) -> m1.getOperatorNationality().compareTo(m2.getOperatorNationality())).collect(Collectors.toList());
            case CREATOR_NAME:
                return values.stream().filter((m) -> m.getCreatorName() != null).sorted((m1, m2) -> m1.getCreatorName().compareTo(m2.getCreatorName())).collect(Collectors.toList());
            case CREATION_TIME:
                return values.stream().sorted((m1, m2) -> m1.getCreationTime().compareTo(m2.getCreationTime())).collect(Collectors.toList());
                default:
                return values;
        }
    }

    private boolean updateElementsList(List<Movie> movies) {
        // filter and sort values
        List<Movie> filteredList = filterElements(movies, filterByColumn, filterByValue);
        this.movies = sortElements(filteredList, sortByColumn);
        return true;
    }

    private void displayMovies(List<Movie> rawMovies) {
        if (updateElementsList(rawMovies)) {
            ObservableList<Movie> observableMovies = FXCollections.observableArrayList(this.movies);
            elementsTableView.setItems(observableMovies);
        }
    }

    private void displayAllMovies() {
        DataBaseAnswer<List<Movie>> answer = dataManipulator.getAll();
        if (answer.code != DataBaseAnswer.CODE_SUCCESS) {
            displayCode(answer.code);
        } else {
            displayMovies(answer.object);
        }
    }

    public void languageButtonEvent(ActionEvent event) {
        launcher.openSelectLanguageScreen();
    }

    public void logoutButtonEvent(ActionEvent event) {
        launcher.openLoginScreen();
    }

    public void allElementsButtonEvent(ActionEvent event) {
        filterByColumn = ColumnDescription.NOTHING;
        sortByColumn = ColumnDescription.NOTHING;
        displayAllMovies();
    }

    public void searchByOperatorButtonEvent(ActionEvent event) {
        launcher.openSearchByOperatorScreen();
    }

    public void informationButtonEvent(ActionEvent event) {
        DataBaseAnswer<String> answer = dataManipulator.getInfo();
        if (answer.code != DataBaseAnswer.CODE_SUCCESS) {
            displayCode(answer.code);
        } else {
            launcher.openInformationScreen(answer.object);
        }
    }

    public void maxElementButtonEvent(ActionEvent event) {
        editElementReason = EditElementReason.ADD_IF_MAX;
        launcher.openEditElementScreen(null);
    }

    public void minElementButtonEvent(ActionEvent event) {
        editElementReason = EditElementReason.REMOVE_LOWER;
        launcher.openEditElementScreen(null);
    }

    public void addElementButtonEvent(ActionEvent event) {
        editElementReason = EditElementReason.ADD;
        launcher.openEditElementScreen(null);
    }

    public void canvasClickEvent(double clickX, double clickY) {
        if (movies == null) {
            return;
        }
        for (Movie movie : movies) {
            int x = movie.getCoordinateX();
            long y = movie.getCoordinateY();
            long length = movie.getLength();
            if ((clickX > x) && (clickX < x + length) && (clickY > y) && (clickY < y + 50)) {
                editElementReason = EditElementReason.EDIT;
                launcher.openEditElementScreen(movie);
                return;
            }
        }
    }

    @FXML
    private void initialize() {
        elementsTableView.setRowFactory( tv -> {
            TableRow<Movie> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    Movie movie = row.getItem();
                    editElementReason = EditElementReason.EDIT;
                    launcher.openEditElementScreen(movie);
                }
            });
            return row;
        });

        idColumn.setCellValueFactory(new PropertyValueFactory<Movie, Integer>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<Movie, String>("name"));
        coordinateXColumn.setCellValueFactory(new PropertyValueFactory<Movie, Integer>("coordinateX"));
        coordinateYColumn.setCellValueFactory(new PropertyValueFactory<Movie, Long>("coordinateY"));
        oscarsCountColumn.setCellValueFactory(new PropertyValueFactory<Movie, Long>("oscarsCount"));
        lengthColumn.setCellValueFactory(new PropertyValueFactory<Movie, Long>("length"));
        movieGenreColumn.setCellValueFactory(cell -> {
            MovieGenre genre = cell.getValue().getGenre();
            String res = genre == null ? "" : Localizer.get(genre.toString());
            return new SimpleObjectProperty<>(res);
        });
        mpaaRatingColumn.setCellValueFactory(cell -> {
            MpaaRating rating = cell.getValue().getMpaaRating();
            String res = rating == null ? "" : Localizer.get(rating.toString());
            return new SimpleObjectProperty<>(res);
        });
        operatorNameColumn.setCellValueFactory(new PropertyValueFactory<Movie, String>("operatorName"));
        operatorIdColumn.setCellValueFactory(new PropertyValueFactory<Movie, String>("operatorId"));
        operatorEyeColorColumn.setCellValueFactory(cell -> {
            movie.Color color = cell.getValue().getOperatorEyeColor();
            String res = color == null ? "" : Localizer.get(color.toString());
            return new SimpleObjectProperty<>(res);
        });
        operatorNationalityColumn.setCellValueFactory(cell -> {
            Country country = cell.getValue().getOperatorNationality();
            String res = country == null ? "" : Localizer.get(country.toString());
            return new SimpleObjectProperty<>(res);
        });
        creatorNameColumn.setCellValueFactory(new PropertyValueFactory<Movie, String>("creatorName"));
        //creationDateColumn.setCellValueFactory(new PropertyValueFactory<Movie, LocalDateTime>("creationTime"));
        creationDateColumn.setCellValueFactory(cell -> {
            LocalDateTime dateTime = cell.getValue().getCreationTime();
            String formattedDateTime = dateTime == null ? "" : Localizer.get(dateTime);
            return new SimpleObjectProperty<>(formattedDateTime);
        });

        animationTimeLine = new Timeline();
        animationTimeLine.setCycleCount(Timeline.INDEFINITE);
        animationTimeLine.getKeyFrames().add(
                    new KeyFrame(Duration.millis(500), 
                        new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent t) {
                                redrawElements();
                            }
                        }, 
                        new KeyValue[0]) // don't use binding
            );
        animationTimeLine.playFromStart();

        moviesListUpdateTimeLine = new Timeline();
        moviesListUpdateTimeLine.setCycleCount(Timeline.INDEFINITE);
        moviesListUpdateTimeLine.getKeyFrames().add(
                    new KeyFrame(Duration.millis(3000), 
                        new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent t) {
                                displayAllMovies();
                            }
                        }, 
                        new KeyValue[0]) // don't use binding
            );
        moviesListUpdateTimeLine.playFromStart();

        elementsCanvas.setOnMouseClicked(event -> {
            canvasClickEvent(event.getX(), event.getY());
        });
        
        logoutButton.textProperty().bind(Localizer.getBinging("text.logout"));
        idColumn.textProperty().bind(Localizer.getBinging("text.id"));
        nameColumn.textProperty().bind(Localizer.getBinging("text.name"));
        creatorNameColumn.textProperty().bind(Localizer.getBinging("text.creator"));
        coordinateXColumn.textProperty().bind(Localizer.getBinging("text.coordinate_x"));
        coordinateYColumn.textProperty().bind(Localizer.getBinging("text.coordinate_y"));
        oscarsCountColumn.textProperty().bind(Localizer.getBinging("text.oscars_count"));
        lengthColumn.textProperty().bind(Localizer.getBinging("text.length"));
        movieGenreColumn.textProperty().bind(Localizer.getBinging("text.movie_genre"));
        mpaaRatingColumn.textProperty().bind(Localizer.getBinging("text.mpaa_rating"));
        operatorNameColumn.textProperty().bind(Localizer.getBinging("text.operator_name"));
        operatorIdColumn.textProperty().bind(Localizer.getBinging("text.operator_id"));
        operatorEyeColorColumn.textProperty().bind(Localizer.getBinging("text.operator_eye_color"));
        operatorNationalityColumn.textProperty().bind(Localizer.getBinging("text.operator_nationality"));
        creationDateColumn.textProperty().bind(Localizer.getBinging("text.creation_date"));
        allElementsButton.textProperty().bind(Localizer.getBinging("text.view_all"));
        searchByOperatorButton.textProperty().bind(Localizer.getBinging("text.remove_by_operator"));
        informationButton.textProperty().bind(Localizer.getBinging("text.information"));
        maxElementButton.textProperty().bind(Localizer.getBinging("text.add_if_max"));
        minElementButton.textProperty().bind(Localizer.getBinging("text.remove_lower"));
        addElementButton.textProperty().bind(Localizer.getBinging("text.add"));
        clearButton.textProperty().bind(Localizer.getBinging("text.clear"));
        groupByOscarsCountButton.textProperty().bind(Localizer.getBinging("text.group_by_oscars_count"));
        filterButton.textProperty().bind(Localizer.getBinging("text.filter_and_sort"));
    }

    public void setupScreen() {
        statusBarText.setText("");
        DataBaseAnswer<String> answer = dataManipulator.whoami();
        if (answer.code != DataBaseAnswer.CODE_SUCCESS) {
            displayCode(answer.code);
        } else {
            userNameText.setText(answer.object);
            displayAllMovies();
        }
    }

    public void editElementOkayEvent(Movie movie) {
        switch (editElementReason) {
            case EDIT:
            {
                DataBaseAnswer<Void> answer = dataManipulator.replace(movie.getId(), movie);
                displayCode(answer.code);
            }
                break;
            case ADD_IF_MAX:
            {
                DataBaseAnswer<Boolean> answer = dataManipulator.addIfMax(movie);
                if (answer.code != DataBaseAnswer.CODE_SUCCESS) {
                    displayCode(answer.code);
                } else {
                    if (answer.object.booleanValue()) {
                        displayStatus(Localizer.get("message.successfully_added_element"));
                    } else {
                        displayStatus(Localizer.get("message.element_is_not_max"));
                    }
                }
            }
                break;
            case ADD:
            {
                DataBaseAnswer<Long> answer = dataManipulator.add(movie);
                displayCode(answer.code);
            }
                break;
            case REMOVE_LOWER:
            {
                DataBaseAnswer<Void> answer = dataManipulator.removeLower(movie);
                displayCode(answer.code);
            }
                break;
            default:
                break;
        }
        displayAllMovies();
    }

    public void editElementDeleteEvent(Movie movie) {
        switch (editElementReason) {
            case EDIT:
            {
                DataBaseAnswer<Void> answer = dataManipulator.remove(movie.getId());
                displayCode(answer.code);
            }
                break;
            case ADD_IF_MAX:
                // nothing
                break;
            case ADD:
                // nothing
                break;
            case REMOVE_LOWER:
                // nothing
                break;
        
            default:
                break;
        }
        displayAllMovies();
    }

    public void removeByOperatorEvent(Person operator) {
        DataBaseAnswer<Void> answer = dataManipulator.removeAllByOperator(operator);
        displayCode(answer.code);
        displayAllMovies();
    }

    public void clearButtonEvent(ActionEvent event) {
        DataBaseAnswer<Void> answer = dataManipulator.clear();
        displayCode(answer.code);
        displayAllMovies();
    }

    public void groupByOscarsCountButtonEvent(ActionEvent event) {
        DataBaseAnswer<HashMap<Long, Long>> oscarsCountGroupsAnswer = dataManipulator.getGroupCountingByOscarsCount();
        DataBaseAnswer<Long> oscarsCountAnswer = dataManipulator.getSumOfOscarsCount();
        if ((oscarsCountGroupsAnswer.code == DataBaseAnswer.CODE_SUCCESS) && (oscarsCountAnswer.code == DataBaseAnswer.CODE_SUCCESS)) {
            StringBuilder string = new StringBuilder();
            string.append(Localizer.get("message.sum_of_oscars")+": ");
            string.append(oscarsCountAnswer.object);
            string.append("\n"+Localizer.get("message.oscars_count")+" | "+Localizer.get("message.movies_count")+"\n");
            for (Long oscarsCount : oscarsCountGroupsAnswer.object.keySet()) {
                string.append(oscarsCount);
                string.append(" | ");
                string.append(oscarsCountGroupsAnswer.object.get(oscarsCount));
                string.append("\n");
            }
            launcher.openInformationScreen(string.toString());
        }
    }

    public void filterButtonEvent(ActionEvent event) {
        launcher.openFilterScreen();
    }

    public void filterEvent(ColumnDescription filterColumn, String filterValue, ColumnDescription sortColumn) {
        filterByColumn = filterColumn;
        filterByValue = filterValue;
        sortByColumn = sortColumn;
        displayAllMovies();
    }

}
