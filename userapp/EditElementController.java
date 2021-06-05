package userapp;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import movie.*;

public class EditElementController extends AppController {
    
    @FXML
    private Text idText;
    @FXML
    private TextField idTextField;
    @FXML
    private Text nameText;
    @FXML
    private TextField nameTextField;
    @FXML
    private Text coordinateXText;
    @FXML
    private TextField coordinateXTextField;
    @FXML
    private Text coordinateYText;
    @FXML
    private TextField coordinateYTextField;
    @FXML
    private Text oscarsCountText;
    @FXML
    private TextField oscarsCountTextField;
    @FXML
    private Text lengthText;
    @FXML
    private TextField lengthTextField;
    @FXML
    private Text movieGenreText;
    @FXML
    private TextField movieGenreTextField;
    @FXML
    private Text mpaaRatingText;
    @FXML
    private TextField mpaaRatingTextField;
    @FXML
    private Text operatorNameText;
    @FXML
    private TextField operatorNameTextField;
    @FXML
    private Text operatorIdText;
    @FXML
    private TextField operatorIdTextField;
    @FXML
    private Text operatorEyeColorText;
    @FXML
    private TextField operatorEyeColorTextField;
    @FXML
    private Text operatorNationalityText;
    @FXML
    private TextField operatorNationalityTextField;
    @FXML
    private Button deleteButton;
    @FXML
    private Button okayButton;
    @FXML
    private Text statusBarText;

    private void displayStatus(String status) {
        statusBarText.setText(status);
    }

    private Movie getMovie() {
        long id;
        int x;
        long y;
        String operatorName;
        String operatorId;
        Color operatorEyeColor;
        Country operatorNationality;
        String name;
        long oscarsCount;
        long length;
        MovieGenre genre;
        MpaaRating mpaaRating;
        try {
            id = Integer.valueOf(idTextField.getText()).intValue();
        } catch (NumberFormatException e) {
            id = -1; // maybe we just adding this element and it has no id
        }
        try {
            x = Integer.valueOf(coordinateXTextField.getText()).intValue();
            if (x <= -746) {
                displayStatus(Localizer.get("message.x_must_be"));
                return null;
            }
        } catch (NumberFormatException e) {
            displayStatus(Localizer.get("message.x_is_not_integer"));
            return null;
        }
        try {
            y = Long.valueOf(coordinateYTextField.getText()).longValue();
            if (y <= -951) {
                displayStatus(Localizer.get("message.y_must_be"));
                return null;
            }
        } catch (NumberFormatException e) {
            displayStatus(Localizer.get("message.y_is_not_integer"));
            return null;
        }
        operatorName = operatorNameTextField.getText();
        operatorId = operatorIdTextField.getText();
        if (operatorId.length() == 0) {
            operatorId = null;
        } else if (operatorId.length() < 4) {
            displayStatus(Localizer.get("message.operator_id_must_be"));
            return null;
        }
        String operatorEyeColorString = operatorEyeColorTextField.getText();
        try {
            operatorEyeColor = Color.valueOf(operatorEyeColorString);
        } catch (IllegalArgumentException e) {
            if (operatorEyeColorString.length() == 0) {
                operatorEyeColor = null;
            } else {
                displayStatus(Localizer.get("message.unknown_value_of")+" "+Localizer.get("text.operator_eye_color"));
                return null;
            }
        }
        try {
            operatorNationality = Country.valueOf(operatorNationalityTextField.getText());
        } catch (IllegalArgumentException e) {
            displayStatus(Localizer.get("message.unknown_value_of")+" "+Localizer.get("text.operator_nationality"));
            return null;
        }
        name = nameTextField.getText();
        try {
            oscarsCount = Long.valueOf(oscarsCountTextField.getText()).longValue();
            if (oscarsCount <= 0) {
                displayStatus(Localizer.get("message.oscars_count_must_be"));
                return null;
            }
        } catch (NumberFormatException e) {
            displayStatus(Localizer.get("message.oscars_count_is_not_integer"));
            return null;
        }
        try {
            length = Long.valueOf(lengthTextField.getText()).longValue();
            if (length <= 0) {
                displayStatus(Localizer.get("message.length_must_be"));
                return null;
            }
        } catch (NumberFormatException e) {
            displayStatus(Localizer.get("message.length_is_not_integer"));
            return null;
        }
        String movieGenreString = movieGenreTextField.getText();
        try {
            genre = MovieGenre.valueOf(movieGenreString);
        } catch (IllegalArgumentException e) {
            if (movieGenreString.length() == 0) {
                genre = null;
            } else {
                displayStatus(Localizer.get("message.unknown_value_of")+" "+Localizer.get("text.movie_genre"));
                return null;
            }
        }
        String mpaaRatingString = mpaaRatingTextField.getText();
        try {
            mpaaRating = MpaaRating.valueOf(mpaaRatingString);
        } catch (IllegalArgumentException e) {
            if (mpaaRatingString.length() == 0) {
                mpaaRating = null;
            } else {
                displayStatus(Localizer.get("message.unknown_value_of")+" "+Localizer.get("text.mpaa_rating"));
                return null;
            }
        }
        Coordinates coordinates = new Coordinates(x, y);
        Person operator = new Person(operatorName, operatorId, operatorEyeColor, operatorNationality); // TODO THERE IS A CRAZY BUG
        Movie movie = new Movie(name, coordinates, oscarsCount, length, genre, mpaaRating, operator);
        movie.setId(id);
        return movie;
    }

    public void displayMovie(Movie movie) {
        if (movie != null) {
            long id = movie.getId();
            String name = movie.getName();
            int coordinateX = movie.getCoordinateX();
            long coordinateY = movie.getCoordinateY();
            String operatorName = movie.getOperatorName();
            String operatorId = movie.getOperatorId();
            Color operatorEyeColor = movie.getOperatorEyeColor();
            Country operatorNationality = movie.getOperatorNationality();
            long oscarsCount = movie.getOscarsCount();
            long length = movie.getLength();
            MovieGenre genre = movie.getGenre();
            MpaaRating mpaaRating = movie.getMpaaRating();
            idTextField.setText(String.valueOf(id));
            coordinateXTextField.setText(String.valueOf(coordinateX));
            coordinateYTextField.setText(String.valueOf(coordinateY));
            if (operatorName == null) {
                operatorNameTextField.setText("");
            } else {
                operatorNameTextField.setText(operatorName);
            }
            if (operatorId == null) {
                operatorIdTextField.setText("");
            } else {
                operatorIdTextField.setText(operatorId);
            }
            if (operatorEyeColor == null) {
                operatorEyeColorTextField.setText("");
            } else {
                operatorEyeColorTextField.setText(operatorEyeColor.name());
            }
            if (operatorNationality == null) {
                operatorNationalityTextField.setText("");
            } else {
                operatorNationalityTextField.setText(operatorNationality.name());
            }
            nameTextField.setText(name);
            oscarsCountTextField.setText(String.valueOf(oscarsCount));
            lengthTextField.setText(String.valueOf(length));
            if (genre == null) {
                movieGenreTextField.setText("");
            } else {
                movieGenreTextField.setText(genre.name());
            }
            if (mpaaRating == null) {
                mpaaRatingTextField.setText("");
            } else {
                mpaaRatingTextField.setText(mpaaRating.name());
            }
            
        } else {
            idTextField.setText("");
            coordinateXTextField.setText("");
            coordinateYTextField.setText("");
            operatorNameTextField.setText("");
            operatorIdTextField.setText("");
            operatorEyeColorTextField.setText("");
            operatorNationalityTextField.setText("");
            nameTextField.setText("");
            oscarsCountTextField.setText("");
            lengthTextField.setText("");
            movieGenreTextField.setText("");
            mpaaRatingTextField.setText("");
        }
    }

    @FXML
    private void initialize() {
        idText.textProperty().bind(Localizer.getBinging("text.id"));
        coordinateXText.textProperty().bind(Localizer.getBinging("text.coordinate_x"));
        coordinateYText.textProperty().bind(Localizer.getBinging("text.coordinate_y"));
        operatorNameText.textProperty().bind(Localizer.getBinging("text.operator_name"));
        operatorIdText.textProperty().bind(Localizer.getBinging("text.operator_id"));
        operatorEyeColorText.textProperty().bind(Localizer.getBinging("text.operator_eye_color"));
        operatorNationalityText.textProperty().bind(Localizer.getBinging("text.operator_nationality"));
        nameText.textProperty().bind(Localizer.getBinging("text.name"));
        oscarsCountText.textProperty().bind(Localizer.getBinging("text.oscars_count"));
        lengthText.textProperty().bind(Localizer.getBinging("text.length"));
        movieGenreText.textProperty().bind(Localizer.getBinging("text.movie_genre"));
        mpaaRatingText.textProperty().bind(Localizer.getBinging("text.mpaa_rating"));
    }

    public void deleteButtonEvent(ActionEvent event) {
        Movie movie = getMovie();
        if (movie != null) {
            launcher.editElementDeleteEvent(movie);
        }
    }

    public void okayButtonEvent(ActionEvent event) {
        Movie movie = getMovie();
        if (movie != null) {
            launcher.editElementOkayEvent(movie);
        }
    }

}
